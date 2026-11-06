package com.portfolio.pixelglow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val brightness: Float,
    val rotationDegrees: Float,
    val filterIndex: Int,
    val isCompleted: Boolean = false // false = Draft/Re-edit, true = Exported
)
