package com.portfolio.pixelglow.data.model

import androidx.compose.ui.graphics.ColorMatrix

data class FilterModel(
    val name: String,
    val matrix: ColorMatrix
)

object FilterProvider {
    val allFilters = listOf(
        FilterModel("Normal", ColorMatrix()),

        FilterModel("Clarendon", ColorMatrix(floatArrayOf(
            1.1f, 0f, 0f, 0f, 10f,
            0f, 1.0f, 0f, 0f, 5f,
            0f, 0f, 1.3f, 0f, 20f,
            0f, 0f, 0f, 1f, 0f
        ))),

        FilterModel("Vintage Sepia", ColorMatrix(floatArrayOf(
            0.393f, 0.769f, 0.189f, 0f, 0f,
            0.349f, 0.686f, 0.168f, 0f, 0f,
            0.272f, 0.534f, 0.131f, 0f, 0f,
            0f,     0f,     0f,     1f, 0f
        ))),

        FilterModel("Monochrome", ColorMatrix().apply { setToSaturation(0f) }),

        FilterModel("Cinematic Teal", ColorMatrix(floatArrayOf(
            0.8f, 0f, 0f, 0f, -10f,
            0f, 1.1f, 0f, 0f, 15f,
            0f, 0f, 1.4f, 0f, 35f,
            0f, 0f, 0f, 1f, 0f
        ))),

        FilterModel("Warm Vibe", ColorMatrix(floatArrayOf(
            1.2f, 0f, 0f, 0f, 15f,
            0f, 1.1f, 0f, 0f, 10f,
            0f, 0f, 0.8f, 0f, -5f,
            0f, 0f, 0f, 1f, 0f
        ))),

        FilterModel("Lark Golden", ColorMatrix(floatArrayOf(
            1.1f, 0f, 0f, 0f, 20f,
            0f, 1.0f, 0f, 0f, 10f,
            0f, 0f, 0.9f, 0f, -10f,
            0f, 0f, 0f, 1f, 0f
        ))),

        FilterModel("High Contrast", ColorMatrix(floatArrayOf(
            1.4f, 0f, 0f, 0f, -20f,
            0f, 1.4f, 0f, 0f, -20f,
            0f, 0f, 1.4f, 0f, -20f,
            0f, 0f, 0f, 1f, 0f
        )))
    )
}
