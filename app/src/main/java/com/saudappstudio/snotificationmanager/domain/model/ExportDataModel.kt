package com.saudappstudio.snotificationmanager.domain.model

/**
 * Export/Import payload model for backup and migration.
 */
data class ExportDataModel(
    val exportVersion: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val apps: List<AppModel>,
    val firebaseProjects: List<FirebaseProjectModel>,
    val topics: List<TopicModel>,
    val templates: List<TemplateModel>
)
