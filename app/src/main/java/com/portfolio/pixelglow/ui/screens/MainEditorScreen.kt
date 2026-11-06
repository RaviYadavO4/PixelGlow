package com.portfolio.pixelglow.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.portfolio.pixelglow.data.model.FilterModel
import com.portfolio.pixelglow.data.model.FilterProvider
import com.portfolio.pixelglow.ui.viewmodel.AdjustSubTool
import com.portfolio.pixelglow.ui.viewmodel.EditorTool
import com.portfolio.pixelglow.ui.viewmodel.EditorViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEditorScreen(viewModel: EditorViewModel, onSystemCloseApp: () -> Unit) {
    val imageUri by viewModel.selectedImageUri.collectAsState()
    val activeMatrix by viewModel.activeMatrix.collectAsState()
    val currentTool by viewModel.currentTool.collectAsState()
    val adjustSubTool by viewModel.currentAdjustSubTool.collectAsState()

    val brightness by viewModel.brightness.collectAsState()
    val saturation by viewModel.saturation.collectAsState()
    val vignette by viewModel.vignette.collectAsState()
    val rotationDegrees by viewModel.rotationDegrees.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()

    val drafts by viewModel.draftProjects.collectAsState()
    val exported by viewModel.exportedProjects.collectAsState()
    val fullPreviewProject by viewModel.fullPreviewProject.collectAsState()

    val customText by viewModel.customText.collectAsState()
    val showOriginal by viewModel.showOriginal.collectAsState()
    val rgbShift by viewModel.rgbShift.collectAsState()
    val isNeonLeakEnabled by viewModel.isNeonLeakEnabled.collectAsState()
    var textOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> if (uri != null) viewModel.createNewProject(uri) }

    // Smart System Back Navigation Interceptor
    BackHandler(enabled = true) {
        viewModel.handleSystemBackNavigation(onAppClose = onSystemCloseApp)
    }

    // 🌟 CINEMATIC 400MS SMOOTH FILTER FADE ANIMATION LOGIC (VERSION CONFLICT SOLVED)
    val animatedBrightness by animateFloatAsState(targetValue = brightness, animationSpec = tween(400), label = "brightAnim")
    val animatedSaturation by animateFloatAsState(targetValue = saturation, animationSpec = tween(400), label = "satAnim")
    val animatedRgbShift by animateFloatAsState(targetValue = rgbShift, animationSpec = tween(400), label = "rgbAnim")

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070709))) {
        if (imageUri == null) {
            // --- 1. PRO STUDIO DASHBOARD PANEL VIEW ---
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("PixelGlow", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Text("Creative Local Studio", color = Color.Gray, fontSize = 13.sp)
                    }
                    Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(0.05f)).border(1.dp, Color(0xFF00ADB5), CircleShape).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }.padding(12.dp)) {
                        Icon(Icons.Default.AddPhotoAlternate, "", tint = Color(0xFF00ADB5), modifier = Modifier.size(22.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DRAFTS SECTION (WITH DELETE CRUD)
                if (drafts.isNotEmpty()) {
                    Text("In Progress (Drafts)", color = Color(0xFF00ADB5), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                        items(drafts) { project ->
                            Box(modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp))) {
                                AsyncImage(model = project.imageUri, contentDescription = "", modifier = Modifier.fillMaxSize().clickable { viewModel.loadProjectFromDb(project) }, contentScale = ContentScale.Crop)
                                Icon(Icons.Default.Cancel, "", tint = Color.Red.copy(0.8f), modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(20.dp).clickable { viewModel.deleteDraft(project.id) })
                                Surface(color = Color.Black.copy(0.6f), modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                                    Text("Draft #${project.id}", color = Color.LightGray, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(2.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // EXPORTED MASTERPIECES ROW GRID
                Text("Exported Masterpieces", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                if (drafts.isEmpty() && exported.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AutoAwesome, "", tint = Color.DarkGray, modifier = Modifier.size(44.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Studio Workspace Empty", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                } else {
                    LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
                        items(exported) { project ->
                            AsyncImage(model = project.imageUri, contentDescription = "", modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(18.dp)).border(1.dp, Color.White.copy(0.04f), RoundedCornerShape(18.dp)).clickable { viewModel.showFullPreview(project) }, contentScale = ContentScale.Crop)
                        }
                    }
                }
            }
        } else {
            // --- 2. LIVE STUDIO EDITING LAYOUT ---
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.saveAsDraft() }, modifier = Modifier.size(38.dp).background(Color(0xFF141418), CircleShape)) {
                        Icon(Icons.Default.ArrowBackIosNew, "", tint = Color.White, modifier = Modifier.size(14.dp))
                    }

                    // Before/After Live Comparison Layout Trigger
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .size(38.dp)
                            .background(if (showOriginal) Color(0xFF00ADB5) else Color(0xFF141418), CircleShape)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { viewModel.toggleOriginalPreview(true) },
                                    onDragEnd = { viewModel.toggleOriginalPreview(false) },
                                    onDrag = { _, _ -> }
                                )
                            }
                    ) {
                        Icon(Icons.Default.Compare, "", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = "Export", color = Color(0xFF00ADB5), fontSize = 14.sp, fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .clickable {
                                val xRatio = if (textOffset.x != 0f) textOffset.x / 500f else 0f
                                val yRatio = if (textOffset.y != 0f) textOffset.y / 500f else 0f
                                viewModel.exportFinalImage(
                                    textXRatio = xRatio, textYRatio = yRatio,
                                    onSuccess = { Toast.makeText(context, "Export Saved & Synced!", Toast.LENGTH_SHORT).show() },
                                    onFailure = { Toast.makeText(context, "Export Error", Toast.LENGTH_SHORT).show() }
                                )
                            }
                            .background(Color(0xFF00ADB5).copy(0.1f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
// Core Processing Canvas Presentation Area
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val finalMatrix = remember(activeMatrix, animatedBrightness, animatedSaturation, animatedRgbShift, showOriginal) {
                        ColorMatrix().apply {
                            if (showOriginal) { reset() } else {
                                set(activeMatrix)
                                val satMatrix = ColorMatrix().apply { setToSaturation(animatedSaturation) }
                                timesAssign(satMatrix)
                                val rgbGlitchMatrix = ColorMatrix(floatArrayOf(1f,0f,0f,0f,animatedRgbShift, 0f,1f,0f,0f,0f, 0f,0f,1f,0f,-animatedRgbShift, 0f,0f,0f,1f,0f))
                                timesAssign(rgbGlitchMatrix)
                                val brightnessMatrix = ColorMatrix(floatArrayOf(animatedBrightness,0f,0f,0f,0f, 0f,animatedBrightness,0f,0f,0f, 0f,0f,animatedBrightness,0f,0f, 0f,0f,0f,1f,0f))
                                timesAssign(brightnessMatrix)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.wrapContentSize().clip(RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Canvas",
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .rotate(if (showOriginal) 0f else rotationDegrees)
                                .drawWithContent {
                                    drawContent()
                                    if (vignette > 0f && !showOriginal) {
                                        drawRect(
                                            brush = Brush.radialGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = vignette)),
                                                radius = size.minDimension / 1.1f
                                            ),
                                            blendMode = BlendMode.Multiply
                                        )
                                    }
                                    if (isNeonLeakEnabled && !showOriginal) {
                                        drawRect(
                                            brush = Brush.linearGradient(
                                                colors = listOf(Color(0xFFFF007F).copy(alpha = 0.35f), Color(0xFF00F2FE).copy(alpha = 0.25f)),
                                                start = Offset(0f, 0f), end = Offset(size.width, size.height)
                                            ),
                                            blendMode = BlendMode.Screen
                                        )
                                    }
                                },
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.colorMatrix(finalMatrix) // Smoothly animated color matrix mapping
                        )
// Draggable Neon Typography Subtitle Layer
                        if (customText.isNotEmpty() && !showOriginal) {
                            Text(
                                text = customText, color = Color(0xFF00F2FE), fontSize = 24.sp, fontWeight = FontWeight.Black,
                                modifier = Modifier
                                    .offset { IntOffset(textOffset.x.roundToInt(), textOffset.y.roundToInt()) }
                                    .pointerInput(Unit) {
                                        detectDragGestures { change, dragAmount ->
                                            change.consume()
                                            textOffset = Offset(textOffset.x + dragAmount.x, textOffset.y + dragAmount.y)
                                        }
                                    }
                                    .background(Color.Black.copy(0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
// Controls Bottom Board Board Panel
                Column(
                    modifier = Modifier.fillMaxWidth().background(Color(0xFF0E0E11), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
                        when (currentTool) {
                            is EditorTool.Filters -> {
                                LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    itemsIndexed(FilterProvider.allFilters) { index, filter ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(68.dp).clickable { viewModel.applyFilter(index, filter.matrix) }) {
                                            Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).border(1.dp, Color.White.copy(0.08f), RoundedCornerShape(16.dp))) {
                                                AsyncImage(model = imageUri, contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, colorFilter = ColorFilter.colorMatrix(filter.matrix))
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(filter.name, color = Color.White, fontSize = 10.sp, maxLines = 1)
                                        }
                                    }
                                }
                            }
                            is EditorTool.Adjust -> {
                                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                                    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(horizontal = 8.dp)) {
                                        item { Text("Light", color = if (adjustSubTool == AdjustSubTool.BRIGHTNESS) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.clickable { viewModel.changeAdjustSubTool(AdjustSubTool.BRIGHTNESS) }.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                        item { Text("Color", color = if (adjustSubTool == AdjustSubTool.SATURATION) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.clickable { viewModel.changeAdjustSubTool(AdjustSubTool.SATURATION) }.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                        item { Text("Vignette", color = if (adjustSubTool == AdjustSubTool.VIGNETTE) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.clickable { viewModel.changeAdjustSubTool(AdjustSubTool.VIGNETTE) }.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                        item { Text("Text", color = if (adjustSubTool == AdjustSubTool.TEXT_OVERLAY) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.clickable { viewModel.changeAdjustSubTool(AdjustSubTool.TEXT_OVERLAY) }.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                        item { Text("RGB Glitch", color = if (adjustSubTool == AdjustSubTool.RGB_GLITCH) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.clickable { viewModel.changeAdjustSubTool(AdjustSubTool.RGB_GLITCH) }.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                        item { Text("Color Leak", color = if (adjustSubTool == AdjustSubTool.COLOR_LEAK) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.clickable { viewModel.changeAdjustSubTool(AdjustSubTool.COLOR_LEAK) }.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                    when (adjustSubTool) {
                                        AdjustSubTool.BRIGHTNESS -> Slider(value = brightness, onValueChange = { viewModel.updateBrightness(it) }, valueRange = 0.5f..1.5f, colors = SliderDefaults.colors(thumbColor = Color(0xFF00ADB5), activeTrackColor = Color(0xFF00ADB5)))
                                        AdjustSubTool.SATURATION -> Slider(value = saturation, onValueChange = { viewModel.updateSaturation(it) }, valueRange = 0.0f..2.0f, colors = SliderDefaults.colors(thumbColor = Color(0xFF00ADB5), activeTrackColor = Color(0xFF00ADB5)))
                                        AdjustSubTool.VIGNETTE -> Slider(value = vignette, onValueChange = { viewModel.updateVignette(it) }, valueRange = 0.0f..1.0f, colors = SliderDefaults.colors(thumbColor = Color(0xFF00ADB5), activeTrackColor = Color(0xFF00ADB5)))
                                        AdjustSubTool.RGB_GLITCH -> Slider(value = rgbShift, onValueChange = { viewModel.updateRgbShift(it) }, valueRange = -40f..40f, colors = SliderDefaults.colors(thumbColor = Color(0xFF00ADB5), activeTrackColor = Color(0xFF00ADB5)))
                                        AdjustSubTool.COLOR_LEAK -> {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                Text("Enable Cinematic Neon Leak", color = Color.White, fontSize = 13.sp)
                                                Switch(checked = isNeonLeakEnabled, onCheckedChange = { viewModel.toggleNeonLeak(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF00ADB5)))
                                            }
                                        }
                                        AdjustSubTool.TEXT_OVERLAY -> {
                                            TextField(
                                                value = customText, onValueChange = { viewModel.updateCustomText(it) },
                                                placeholder = { Text("Type premium neon subtitle...", color = Color.Gray, fontSize = 12.sp) },
                                                colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedIndicatorColor = Color(0xFF00ADB5)),
                                                modifier = Modifier.fillMaxWidth().height(50.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            is EditorTool.Transform -> {
                                Button(onClick = { viewModel.rotateImage() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A1A22)), shape = RoundedCornerShape(14.dp)) {
                                    Icon(Icons.Default.RotateRight, "", tint = Color(0xFF00ADB5))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Rotate Canvas 90°", color = Color.White)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color.White.copy(0.05f), modifier = Modifier.padding(horizontal = 24.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        NavigationButton("FILTERS", currentTool is EditorTool.Filters) { viewModel.changeTool(EditorTool.Filters) }
                        NavigationButton("ADJUST", currentTool is EditorTool.Adjust) { viewModel.changeTool(EditorTool.Adjust) }
                        NavigationButton("TRANSFORM", currentTool is EditorTool.Transform) { viewModel.changeTool(EditorTool.Transform) }
                    }
                }
            }
        }
// --- 3. EXPORTED ITEM IMMERSIVE OVERLAY (WITH DELETE CRUD) ---
        fullPreviewProject?.let { project ->
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050507))) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.closeFullPreview() }, modifier = Modifier.size(40.dp).background(Color.White.copy(0.1f), CircleShape)) {
                        Icon(Icons.Default.Close, "", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.deleteExportedProject(project) }, modifier = Modifier.size(40.dp).background(Color.Red.copy(0.15f), CircleShape)) {
                        Icon(Icons.Default.Delete, "", tint = Color.Red)
                    }
                }
                AsyncImage(model = project.imageUri, contentDescription = "", modifier = Modifier.fillMaxWidth(0.92f).wrapContentHeight().clip(RoundedCornerShape(20.dp)).align(Alignment.Center), contentScale = ContentScale.Fit)
                Button(onClick = { viewModel.createNewProject(Uri.parse(project.imageUri)) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ADB5)), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth(0.8f).align(Alignment.BottomCenter).padding(bottom = 48.dp).height(54.dp)) {
                    Icon(Icons.Default.HistoryEdu, "", tint = Color.Black)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Re-edit This Masterpiece", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (isSaving) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.8f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00ADB5), strokeWidth = 3.dp)
            }
        }
    }
}
// 🌟 4. SUB ACTION BUTTON DESIGN SCHEME (SPELING FIXED & DECLARED PERFECTLY)
@Composable
fun NavigationButton(text: String, active: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        color = if (active) Color(0xFF00ADB5) else Color.Gray,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        modifier = Modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp, horizontal = 12.dp)
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun MainDashboardPreview() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070709))) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("PixelGlow", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("Creative Local Studio", color = Color.Gray, fontSize = 13.sp)
                }
                Box(modifier = Modifier.clip(CircleShape).background(Color.White.copy(0.05f)).border(1.dp, Color(0xFF00ADB5), CircleShape).padding(12.dp)) {
                    Icon(Icons.Default.AddPhotoAlternate, "", tint = Color(0xFF00ADB5), modifier = Modifier.size(22.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("In Progress (Drafts)", color = Color(0xFF00ADB5), fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.fillMaxWidth()) {
                items(3) { index ->
                    Box(modifier = Modifier.size(110.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF1A1A22)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp))) {
                        Icon(Icons.Default.Image, "", tint = Color.Gray, modifier = Modifier.align(Alignment.Center).size(36.dp))
                        Icon(Icons.Default.Cancel, "", tint = Color.Red.copy(0.8f), modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(20.dp))
                        Surface(color = Color.Black.copy(0.6f), modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                            Text("Draft #${index + 1}", color = Color.LightGray, fontSize = 10.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(2.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("Exported Masterpieces", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(14.dp), modifier = Modifier.weight(1f)) {
                items(4) {
                    Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF141418)).border(1.dp, Color.White.copy(0.04f), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.AutoAwesome, "", tint = Color(0xFF00ADB5).copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 740)
@Composable
fun MainEditorStudioPreview() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF070709))) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(38.dp).background(Color(0xFF141418), CircleShape)) {
                    Icon(Icons.Default.ArrowBackIosNew, "", tint = Color.White, modifier = Modifier.size(14.dp))
                }
                IconButton(onClick = {}, modifier = Modifier.size(38.dp).background(Color(0xFF141418), CircleShape)) {
                    Icon(Icons.Default.Compare, "", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                Text(
                    text = "Export", color = Color(0xFF00ADB5), fontSize = 14.sp, fontWeight = FontWeight.Black,
                    modifier = Modifier.background(Color(0xFF00ADB5).copy(0.1f), RoundedCornerShape(12.dp)).padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)).background(Color(0xFF16161D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, "", tint = Color.DarkGray, modifier = Modifier.size(72.dp))
                    Text(
                        text = "NEON GLOW CREATIVE STUDIO",
                        color = Color(0xFF00F2FE),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .background(Color.Black.copy(0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth().background(Color(0xFF0E0E11), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)).padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(130.dp), contentAlignment = Alignment.Center) {
                    LazyRow(contentPadding = PaddingValues(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        itemsIndexed(FilterProvider.allFilters) { index, filter ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(68.dp)) {
                                Box(modifier = Modifier.size(64.dp).clip(RoundedCornerShape(16.dp)).background(Color(0xFF22222B)).border(1.dp, if (index == 1) Color(0xFF00ADB5) else Color.White.copy(0.08f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AutoAwesome, "", tint = if (index == 1) Color(0xFF00ADB5) else Color.Gray, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(filter.name, color = if (index == 1) Color(0xFF00ADB5) else Color.White, fontSize = 10.sp, maxLines = 1)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color.White.copy(0.05f), modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavigationButton("FILTERS", true) {}
                    NavigationButton("ADJUST", false) {}
                    NavigationButton("TRANSFORM", false) {}
                }
            }
        }
    }
}