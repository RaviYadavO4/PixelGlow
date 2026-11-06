package com.portfolio.pixelglow.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects WHERE isCompleted = 0 ORDER BY id DESC")
    fun getAllDrafts(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE isCompleted = 1 ORDER BY id DESC")
    fun getAllExported(): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProjectById(projectId: Int)
}
