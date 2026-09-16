package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saudappstudio.snotificationmanager.data.local.entities.AppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM apps ORDER BY name ASC")
    fun getAllApps(): Flow<List<AppEntity>>

    @Query("SELECT * FROM apps WHERE id = :id")
    suspend fun getAppById(id: String): AppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: AppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<AppEntity>)

    @Update
    suspend fun updateApp(app: AppEntity)

    @Query("DELETE FROM apps WHERE id = :id")
    suspend fun deleteAppById(id: String)

    @Query("DELETE FROM apps")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM apps")
    suspend fun getCount(): Int
}
