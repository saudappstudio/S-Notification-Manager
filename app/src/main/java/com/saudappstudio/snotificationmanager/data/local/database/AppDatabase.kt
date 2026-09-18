package com.saudappstudio.snotificationmanager.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

/**
 * Local Room SQLite database storing applications, Firebase projects, topics, templates, and history.
 */
@Database(
    entities = [
        AppEntity::class,
        FirebaseProjectEntity::class,
        TopicEntity::class,
        TemplateEntity::class,
        NotificationHistoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
    abstract fun firebaseProjectDao(): FirebaseProjectDao
    abstract fun topicDao(): TopicDao
    abstract fun templateDao(): TemplateDao
    abstract fun notificationHistoryDao(): NotificationHistoryDao
}
