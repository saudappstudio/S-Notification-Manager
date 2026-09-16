package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saudappstudio.snotificationmanager.data.local.entities.FirebaseProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FirebaseProjectDao {
    @Query("SELECT * FROM firebase_projects ORDER BY name ASC")
    fun getAllProjects(): Flow<List<FirebaseProjectEntity>>

    @Query("SELECT * FROM firebase_projects WHERE id = :id")
    suspend fun getProjectById(id: String): FirebaseProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: FirebaseProjectEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<FirebaseProjectEntity>)

    @Update
    suspend fun updateProject(project: FirebaseProjectEntity)

    @Query("DELETE FROM firebase_projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    @Query("DELETE FROM firebase_projects")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM firebase_projects")
    suspend fun getCount(): Int
}
