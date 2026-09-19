package com.saudappstudio.snotificationmanager.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueModel
import com.saudappstudio.snotificationmanager.domain.model.CrashIssueStatus

/**
 * Room entity storing cached Firebase Crashlytics issues locally.
 */
@Entity(tableName = "crash_issues")
data class CrashIssueEntity(
    @PrimaryKey val id: String,
    val appId: String,
    val appName: String,
    val packageName: String,
    val title: String,
    val subtitle: String,
    val topStackFrame: String,
    val crashCount: Int,
    val userCount: Int,
    val isFatal: Boolean,
    val status: String,
    val firstSeenTimestamp: Long,
    val lastSeenTimestamp: Long,
    val appVersion: String,
    val androidVersion: String,
    val deviceModel: String,
    val stackTrace: String = ""
) {
    fun toDomainModel(): CrashIssueModel = CrashIssueModel(
        id = id,
        appId = appId,
        appName = appName,
        packageName = packageName,
        title = title,
        subtitle = subtitle,
        topStackFrame = topStackFrame,
        crashCount = crashCount,
        userCount = userCount,
        isFatal = isFatal,
        status = runCatching { CrashIssueStatus.valueOf(status) }.getOrDefault(CrashIssueStatus.OPEN),
        firstSeenTimestamp = firstSeenTimestamp,
        lastSeenTimestamp = lastSeenTimestamp,
        appVersion = appVersion,
        androidVersion = androidVersion,
        deviceModel = deviceModel
    )

    companion object {
        fun fromDomain(model: CrashIssueModel, stackTrace: String = ""): CrashIssueEntity = CrashIssueEntity(
            id = model.id,
            appId = model.appId,
            appName = model.appName,
            packageName = model.packageName,
            title = model.title,
            subtitle = model.subtitle,
            topStackFrame = model.topStackFrame,
            crashCount = model.crashCount,
            userCount = model.userCount,
            isFatal = model.isFatal,
            status = model.status.name,
            firstSeenTimestamp = model.firstSeenTimestamp,
            lastSeenTimestamp = model.lastSeenTimestamp,
            appVersion = model.appVersion,
            androidVersion = model.androidVersion,
            deviceModel = model.deviceModel,
            stackTrace = stackTrace
        )
    }
}
