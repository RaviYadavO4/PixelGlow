package com.portfolio.pixelglow.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.ui.graphics.ColorMatrix
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.pixelglow.data.ImageSaver
import com.portfolio.pixelglow.data.local.AppDatabase
import com.portfolio.pixelglow.data.local.ProjectEntity
import com.portfolio.pixelglow.data.model.FilterProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class EditorTool {
    object Filters : EditorTool()
    object Adjust : EditorTool()
    object Transform : EditorTool()
}

enum class AdjustSubTool {
    BRIGHTNESS, SATURATION, VIGNETTE, TEXT_OVERLAY, RGB_GLITCH, COLOR_LEAK
}

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val imageSaver = ImageSaver(context)
    private val projectDao = AppDatabase.getDatabase(context).projectDao()

    val draftProjects: StateFlow<List<ProjectEntity>> = projectDao.getAllDrafts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exportedProjects: StateFlow<List<ProjectEntity>> = projectDao.getAllExported()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isSplashLoading = MutableStateFlow(true)
    val isSplashLoading: StateFlow<Boolean> = _isSplashLoading.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri: StateFlow<Uri?> = _selectedImageUri.asStateFlow()

    private val _currentTool = MutableStateFlow<EditorTool>(EditorTool.Filters)
    val currentTool: StateFlow<EditorTool> = _currentTool.asStateFlow()

    private val _currentAdjustSubTool = MutableStateFlow(AdjustSubTool.BRIGHTNESS)
    val currentAdjustSubTool: StateFlow<AdjustSubTool> = _currentAdjustSubTool.asStateFlow()

    private val _activeMatrix = MutableStateFlow(ColorMatrix())
    val activeMatrix: StateFlow<ColorMatrix> = _activeMatrix.asStateFlow()

    private val _brightness = MutableStateFlow(1.0f)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    private val _saturation = MutableStateFlow(1.0f)
    val saturation: StateFlow<Float> = _saturation.asStateFlow()

    private val _vignette = MutableStateFlow(0.0f)
    val vignette: StateFlow<Float> = _vignette.asStateFlow()

    private val _rgbShift = MutableStateFlow(0f)
    val rgbShift: StateFlow<Float> = _rgbShift.asStateFlow()

    private val _rotationDegrees = MutableStateFlow(0f)
    val rotationDegrees: StateFlow<Float> = _rotationDegrees.asStateFlow()

    private val _customText = MutableStateFlow("")
    val customText: StateFlow<String> = _customText.asStateFlow()

    private val _showOriginal = MutableStateFlow(false)
    val showOriginal: StateFlow<Boolean> = _showOriginal.asStateFlow()

    private val _isNeonLeakEnabled = MutableStateFlow(false)
    val isNeonLeakEnabled: StateFlow<Boolean> = _isNeonLeakEnabled.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _fullPreviewProject = MutableStateFlow<ProjectEntity?>(null)
    val fullPreviewProject: StateFlow<ProjectEntity?> = _fullPreviewProject.asStateFlow()

    private var currentProjectId: Int = 0
    private var currentFilterIndex: Int = 0

    init {
        viewModelScope.launch {
            delay(2500)
            _isSplashLoading.value = false
        }
    }

    fun createNewProject(uri: Uri) {
        _selectedImageUri.value = uri
        _rotationDegrees.value = 0f
        _brightness.value = 1.0f
        _saturation.value = 1.0f
        _vignette.value = 0.0f
        _rgbShift.value = 0f
        _customText.value = ""
        _isNeonLeakEnabled.value = false
        _activeMatrix.value = ColorMatrix()
        currentProjectId = 0
        currentFilterIndex = 0
        _fullPreviewProject.value = null
    }

    fun showFullPreview(project: ProjectEntity) { _fullPreviewProject.value = project }
    fun closeFullPreview() { _fullPreviewProject.value = null }

    fun loadProjectFromDb(project: ProjectEntity) {
        currentProjectId = project.id
        _selectedImageUri.value = Uri.parse(project.imageUri)
        _brightness.value = project.brightness
        _rotationDegrees.value = project.rotationDegrees
        currentFilterIndex = project.filterIndex
        _fullPreviewProject.value = null
        _saturation.value = 1.0f
        _vignette.value = 0.0f
        _rgbShift.value = 0f
        _customText.value = ""
        _isNeonLeakEnabled.value = false

        if (project.filterIndex in FilterProvider.allFilters.indices) {
            _activeMatrix.value = ColorMatrix(FilterProvider.allFilters[project.filterIndex].matrix.values)
        }
    }

    fun changeTool(tool: EditorTool) { _currentTool.value = tool }
    fun changeAdjustSubTool(subTool: AdjustSubTool) { _currentAdjustSubTool.value = subTool }

    fun applyFilter(index: Int, matrix: ColorMatrix) {
        currentFilterIndex = index
        _activeMatrix.value = ColorMatrix(matrix.values)
    }

    fun updateBrightness(value: Float) { _brightness.value = value }
    fun updateSaturation(value: Float) { _saturation.value = value }
    fun updateVignette(value: Float) { _vignette.value = value }
    fun updateRgbShift(value: Float) { _rgbShift.value = value }
    fun updateCustomText(text: String) { _customText.value = text }
    fun toggleOriginalPreview(show: Boolean) { _showOriginal.value = show }
    fun toggleNeonLeak(enabled: Boolean) { _isNeonLeakEnabled.value = enabled }
    fun rotateImage() { _rotationDegrees.value = (_rotationDegrees.value + 90f) % 360f }

    fun deleteDraft(projectId: Int) {
        viewModelScope.launch { projectDao.deleteProjectById(projectId) }
    }

    // 🌟 DELETE EXPORTED MASTERPIECE FROM ROOM DB & FULLSCREEN DIALOG
    fun deleteExportedProject(project: ProjectEntity) {
        viewModelScope.launch {
            projectDao.deleteProjectById(project.id)
            try {
                // MediaStore से फ़ाइल डिलीट करने का प्रयास (Scoped Storage Permissions implementation handles this)
                context.contentResolver.delete(Uri.parse(project.imageUri), null, null)
            } catch (e: Exception) { e.printStackTrace() }
            _fullPreviewProject.value = null
        }
    }

    fun handleSystemBackNavigation(onAppClose: () -> Unit) {
        when {
            _fullPreviewProject.value != null -> { _fullPreviewProject.value = null }
            _selectedImageUri.value != null -> { saveAsDraft() }
            else -> { onAppClose() }
        }
    }

    fun saveAsDraft() {
        val uri = _selectedImageUri.value ?: return
        viewModelScope.launch {
            val entity = ProjectEntity(
                id = currentProjectId,
                imageUri = uri.toString(),
                brightness = _brightness.value,
                rotationDegrees = _rotationDegrees.value,
                filterIndex = currentFilterIndex,
                isCompleted = false
            )
            if (currentProjectId == 0) projectDao.insertProject(entity) else projectDao.updateProject(entity)
            _selectedImageUri.value = null
        }
    }

    fun exportFinalImage(textXRatio: Float, textYRatio: Float, onSuccess: (Uri) -> Unit, onFailure: () -> Unit) {
        val uri = _selectedImageUri.value ?: return
        viewModelScope.launch {
            _isSaving.value = true
            val savedUri = imageSaver.saveEditedImage(uri, _activeMatrix.value, _brightness.value, _saturation.value, _vignette.value, _rgbShift.value, _isNeonLeakEnabled.value, _customText.value, textXRatio, textYRatio)
            _isSaving.value = false
            if (savedUri != null) {
                projectDao.insertProject(ProjectEntity(imageUri = savedUri.toString(), brightness = _brightness.value, rotationDegrees = _rotationDegrees.value, filterIndex = currentFilterIndex, isCompleted = true))
                if (currentProjectId != 0) projectDao.deleteProjectById(currentProjectId)
                _selectedImageUri.value = null
                onSuccess(savedUri)
            } else { onFailure() }
        }
    }
}
