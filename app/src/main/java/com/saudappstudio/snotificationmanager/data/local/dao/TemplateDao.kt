package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.saudappstudio.snotificationmanager.data.local.entities.TemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Query("SELECT * FROM templates ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getTemplateById(id: String): TemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<TemplateEntity>)

    @Update
    suspend fun updateTemplate(template: TemplateEntity)

    @Query("UPDATE templates SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Query("DELETE FROM templates WHERE id = :id")
    suspend fun deleteTemplateById(id: String)

    @Query("DELETE FROM templates")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM templates")
    suspend fun getCount(): Int
}
