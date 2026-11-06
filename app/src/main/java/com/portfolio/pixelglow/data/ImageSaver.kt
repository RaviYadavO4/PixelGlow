package com.portfolio.pixelglow.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.ui.graphics.ColorMatrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

class ImageSaver(private val context: Context) {

    suspend fun saveEditedImage(
        imageUri: Uri,
        colorMatrix: ColorMatrix,
        brightness: Float,
        saturation: Float,
        vignette: Float,
        rgbShift: Float,
        isNeonLeakEnabled: Boolean,
        customText: String,
        textXRatio: Float, // 🌟 स्क्रीन विड्थ के मुकाबले टेक्स्ट की पोजीशन का रेशियो
        textYRatio: Float  // 🌟 स्क्रीन हाइट के मुकाबले टेक्स्ट की पोजीशन का रेशियो
    ): Uri? = withContext(Dispatchers.IO) {
        try {
            // 1. गैलरी से ओरिजिनल इमेज को Bitmap में लोड करना
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close() ?: return@withContext null

            // 2. नया म्यूटेशन Bitmap तैयार करना
            val editedBitmap = Bitmap.createBitmap(
                originalBitmap.width,
                originalBitmap.height,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(editedBitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            // 3. फ़िल्टर और एडजस्टमेंट्स कंबाइन करना (Native Graphics Engine)
            val finalMatrix = ColorMatrix().apply {
                set(colorMatrix)
                val satMatrix = ColorMatrix().apply { setToSaturation(saturation) }
                timesAssign(satMatrix)

                val rgbGlitchMatrix = ColorMatrix(floatArrayOf(
                    1f, 0f, 0f, 0f, rgbShift,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, -rgbShift,
                    0f, 0f, 0f, 1f, 0f
                ))
                timesAssign(rgbGlitchMatrix)

                val brightnessMatrix = ColorMatrix(floatArrayOf(
                    brightness, 0f, 0f, 0f, 0f,
                    0f, brightness, 0f, 0f, 0f,
                    0f, 0f, brightness, 0f, 0f,
                    0f, 0f, 0f, 1f, 0f
                ))
                timesAssign(brightnessMatrix)
            }

            paint.colorFilter = ColorMatrixColorFilter(finalMatrix.values)
            canvas.drawBitmap(originalBitmap, 0f, 0f, paint)

            // 4. 🌟 NATIVE GRADIENT COLOR LEAK RERING (If enabled)
            if (isNeonLeakEnabled) {
                val leakPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    val shader = android.graphics.LinearGradient(
                        0f, 0f, editedBitmap.width.toFloat(), editedBitmap.height.toFloat(),
                        android.graphics.Color.argb((255 * 0.35f).toInt(), 255, 0, 127), // Pink
                        android.graphics.Color.argb((255 * 0.25f).toInt(), 0, 242, 254), // Cyan
                        android.graphics.Shader.TileMode.CLAMP
                    )
                    this.shader = shader
                    this.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SCREEN)
                }
                canvas.drawRect(0f, 0f, editedBitmap.width.toFloat(), editedBitmap.height.toFloat(), leakPaint)
            }

            // 5. 🌟 🌟 NATIVE CANVAS TEXT INJECTION LOGIC (टेक्स्ट को इमेज के ऊपर पक्के से छापना)
            if (customText.isNotEmpty()) {
                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = android.graphics.Color.parseColor("#00F2FE") // Neon Cyan Color
                    // इमेज के रेजोल्यूशन के हिसाब से टेक्स्ट साइज ऑटो-स्केल करना (जैसे विड्थ का 4%)
                    textSize = editedBitmap.width * 0.045f
                    style = Paint.Style.FILL
                    isFakeBoldText = true
                    // टेक्स्ट के पीछे डार्क ड्रॉप शैडो इफ़ेक्ट
                    setShadowLayer(10f, 0f, 0f, android.graphics.Color.BLACK)
                }

                // रेशियो के आधार पर ओरिजिनल हाई-रेजोल्यूशन इमेज पर कोऑर्डिनेट्स कैलकुलेट करना
                // हम 0.5f डिफ़ॉल्ट (सेंटर) मानकर ऑफसेट रेशियो जोड़ते हैं
                val xPos = editedBitmap.width * (0.5f + textXRatio)
                val yPos = editedBitmap.height * (0.5f + textYRatio)

                // टेक्स्ट को सेंटर-अलाइन करने के लिए बाउंड्स मापना
                val textBounds = Rect()
                textPaint.getTextBounds(customText, 0, customText.length, textBounds)
                val finalX = xPos - (textBounds.width() / 2f)

                // 🌟 इमेज के पिक्सल्स के ऊपर टेक्स्ट को परमानेंट ड्रा करना
                canvas.drawText(customText, finalX, yPos, textPaint)
            }

            // 6. MediaStore Storage (गैलरी में सेव करने का लॉजिक पहले जैसा ही रहेगा)
            val filename = "PixelGlow_${System.currentTimeMillis()}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PixelGlow")
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }

            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val uri = context.contentResolver.insert(imageCollection, contentValues)

            if (uri != null) {
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
                outputStream.use {
                    editedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, it!!)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    context.contentResolver.update(uri, contentValues, null, null)
                }
            }

            originalBitmap.recycle()
            editedBitmap.recycle()

            return@withContext uri
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}
