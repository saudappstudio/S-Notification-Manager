package com.saudappstudio.snotificationmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.saudappstudio.snotificationmanager.data.local.entities.CrashIssueEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for local Room database operations on crash issues.
 */
@Dao
interface CrashlyticsDao {

    @Query("SELECT * FROM crash_issues ORDER BY lastSeenTimestamp DESC")
    fun getAllCrashIssues(): Flow<List<CrashIssueEntity>>

    @Query("SELECT * FROM crash_issues WHERE appId = :appId ORDER BY lastSeenTimestamp DESC")
    fun getCrashIssuesByApp(appId: String): Flow<List<CrashIssueEntity>>

    @Query("SELECT * FROM crash_issues WHERE id = :issueId LIMIT 1")
    suspend fun getCrashIssueById(issueId: String): CrashIssueEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrashIssues(issues: List<CrashIssueEntity>)

    @Query("UPDATE crash_issues SET status = :status WHERE id = :issueId")
    suspend fun updateStatus(issueId: String, status: String)

    @Query("DELETE FROM crash_issues")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM crash_issues")
    suspend fun getCount(): Int
}
