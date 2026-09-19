package com.saudappstudio.snotificationmanager.core.di

import android.content.Context
import androidx.room.Room
import com.saudappstudio.snotificationmanager.data.local.dao.AppDao
import com.saudappstudio.snotificationmanager.data.local.dao.CrashlyticsDao
import com.saudappstudio.snotificationmanager.data.local.dao.AnalyticsDao
import com.saudappstudio.snotificationmanager.data.local.dao.FirebaseProjectDao
import com.saudappstudio.snotificationmanager.data.local.dao.NotificationHistoryDao
import com.saudappstudio.snotificationmanager.data.local.dao.TemplateDao
import com.saudappstudio.snotificationmanager.data.local.dao.TopicDao
import com.saudappstudio.snotificationmanager.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "saud_notification_manager.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAppDao(database: AppDatabase): AppDao = database.appDao()

    @Provides
    fun provideFirebaseProjectDao(database: AppDatabase): FirebaseProjectDao = database.firebaseProjectDao()

    @Provides
    fun provideTopicDao(database: AppDatabase): TopicDao = database.topicDao()

    @Provides
    fun provideTemplateDao(database: AppDatabase): TemplateDao = database.templateDao()

    @Provides
    fun provideNotificationHistoryDao(database: AppDatabase): NotificationHistoryDao = database.notificationHistoryDao()

    @Provides
    fun provideCrashlyticsDao(database: AppDatabase): CrashlyticsDao = database.crashlyticsDao()

    @Provides
    fun provideAnalyticsDao(database: AppDatabase): AnalyticsDao = database.analyticsDao()
}
