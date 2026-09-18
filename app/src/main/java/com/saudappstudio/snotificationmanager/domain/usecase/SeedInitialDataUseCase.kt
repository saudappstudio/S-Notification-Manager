package com.saudappstudio.snotificationmanager.domain.usecase

import com.google.gson.Gson
import com.saudappstudio.snotificationmanager.core.logging.Logger
import com.saudappstudio.snotificationmanager.data.local.dao.AppDao
import com.saudappstudio.snotificationmanager.data.local.dao.FirebaseProjectDao
import com.saudappstudio.snotificationmanager.data.local.dao.NotificationHistoryDao
import com.saudappstudio.snotificationmanager.data.local.dao.TemplateDao
import com.saudappstudio.snotificationmanager.data.local.dao.TopicDao
import com.saudappstudio.snotificationmanager.data.local.entities.AppEntity
import com.saudappstudio.snotificationmanager.data.local.entities.FirebaseProjectEntity
import com.saudappstudio.snotificationmanager.data.local.entities.NotificationHistoryEntity
import com.saudappstudio.snotificationmanager.data.local.entities.TemplateEntity
import com.saudappstudio.snotificationmanager.data.local.entities.TopicEntity
import com.saudappstudio.snotificationmanager.provider.SampleDataProvider

/**
 * UseCase to pre-populate local Room database with realistic sample apps, projects, topics, templates, and history.
 */
class SeedInitialDataUseCase(
    private val appDao: AppDao,
    private val firebaseProjectDao: FirebaseProjectDao,
    private val topicDao: TopicDao,
    private val templateDao: TemplateDao,
    private val notificationHistoryDao: NotificationHistoryDao
) {
    private val gson = Gson()

    suspend operator fun invoke() {
        if (appDao.getCount() == 0) {
            Logger.i("Database is empty. Populating sample data...")
            firebaseProjectDao.insertAll(SampleDataProvider.firebaseProjects.map { FirebaseProjectEntity.fromDomain(it) })
            appDao.insertAll(SampleDataProvider.applications.map { AppEntity.fromDomain(it) })
            topicDao.insertAll(SampleDataProvider.topics.map { TopicEntity.fromDomain(it) })
            templateDao.insertAll(SampleDataProvider.templates.map { tpl ->
                TemplateEntity(
                    id = tpl.id,
                    name = tpl.name,
                    appId = tpl.appId,
                    title = tpl.title,
                    message = tpl.message,
                    imageUrl = tpl.imageUrl,
                    topic = tpl.topic,
                    clickAction = tpl.clickAction,
                    deepLink = tpl.deepLink,
                    customDataJson = gson.toJson(tpl.customData),
                    isFavorite = tpl.isFavorite,
                    createdAt = tpl.createdAt,
                    updatedAt = tpl.updatedAt
                )
            })
            notificationHistoryDao.insertAll(SampleDataProvider.history.map { hist ->
                NotificationHistoryEntity(
                    id = hist.id,
                    appId = hist.appId,
                    appName = hist.appName,
                    title = hist.title,
                    message = hist.message,
                    targetType = hist.targetType.key,
                    target = hist.target,
                    environment = hist.environment.key,
                    status = hist.status,
                    messageId = hist.messageId,
                    error = hist.error,
                    imageUrl = hist.imageUrl,
                    clickAction = hist.clickAction,
                    deepLink = hist.deepLink,
                    notificationType = hist.notificationType,
                    eventTrigger = hist.eventTrigger,
                    isScheduled = hist.isScheduled,
                    scheduledTimestamp = hist.scheduledTimestamp,
                    customDataJson = gson.toJson(hist.customData),
                    sentAt = hist.sentAt
                )
            })
            Logger.i("Sample data populated successfully.")
        }
    }
}
