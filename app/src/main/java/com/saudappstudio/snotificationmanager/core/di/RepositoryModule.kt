package com.saudappstudio.snotificationmanager.core.di

import android.content.Context
import com.saudappstudio.snotificationmanager.core.datastore.PreferencesManager
import com.saudappstudio.snotificationmanager.data.local.dao.AnalyticsDao
import com.saudappstudio.snotificationmanager.data.local.dao.AppDao
import com.saudappstudio.snotificationmanager.data.local.dao.CrashlyticsDao
import com.saudappstudio.snotificationmanager.data.local.dao.FirebaseProjectDao
import com.saudappstudio.snotificationmanager.data.local.dao.NotificationHistoryDao
import com.saudappstudio.snotificationmanager.data.local.dao.TemplateDao
import com.saudappstudio.snotificationmanager.data.local.dao.TopicDao
import com.saudappstudio.snotificationmanager.data.remote.api.CloudinaryApiService
import com.saudappstudio.snotificationmanager.data.remote.api.NetlifyApiService
import com.saudappstudio.snotificationmanager.data.repository.AnalyticsRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.AppRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.CloudinaryRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.CrashlyticsRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.FirebaseProjectRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.NotificationRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.SettingsRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.TemplateRepositoryImpl
import com.saudappstudio.snotificationmanager.data.repository.TopicRepositoryImpl
import com.saudappstudio.snotificationmanager.domain.repository.AnalyticsRepository
import com.saudappstudio.snotificationmanager.domain.repository.AppRepository
import com.saudappstudio.snotificationmanager.domain.repository.CloudinaryRepository
import com.saudappstudio.snotificationmanager.domain.repository.CrashlyticsRepository
import com.saudappstudio.snotificationmanager.domain.repository.FirebaseProjectRepository
import com.saudappstudio.snotificationmanager.domain.repository.NotificationRepository
import com.saudappstudio.snotificationmanager.domain.repository.SettingsRepository
import com.saudappstudio.snotificationmanager.domain.repository.TemplateRepository
import com.saudappstudio.snotificationmanager.domain.repository.TopicRepository
import com.saudappstudio.snotificationmanager.domain.usecase.ExportConfigurationUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.ImportConfigurationUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.SeedInitialDataUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.SendNotificationUseCase
import com.saudappstudio.snotificationmanager.domain.usecase.TestBackendConnectionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppRepository(appDao: AppDao): AppRepository = AppRepositoryImpl(appDao)

    @Provides
    @Singleton
    fun provideFirebaseProjectRepository(firebaseProjectDao: FirebaseProjectDao): FirebaseProjectRepository =
        FirebaseProjectRepositoryImpl(firebaseProjectDao)

    @Provides
    @Singleton
    fun provideTopicRepository(topicDao: TopicDao): TopicRepository = TopicRepositoryImpl(topicDao)

    @Provides
    @Singleton
    fun provideTemplateRepository(templateDao: TemplateDao): TemplateRepository = TemplateRepositoryImpl(templateDao)

    @Provides
    @Singleton
    fun provideNotificationRepository(
        notificationHistoryDao: NotificationHistoryDao,
        apiService: NetlifyApiService
    ): NotificationRepository = NotificationRepositoryImpl(notificationHistoryDao, apiService)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        preferencesManager: PreferencesManager,
        apiService: NetlifyApiService,
        appDao: AppDao,
        firebaseProjectDao: FirebaseProjectDao,
        topicDao: TopicDao,
        templateDao: TemplateDao
    ): SettingsRepository = SettingsRepositoryImpl(
        preferencesManager = preferencesManager,
        apiService = apiService,
        appDao = appDao,
        firebaseProjectDao = firebaseProjectDao,
        topicDao = topicDao,
        templateDao = templateDao
    )

    @Provides
    @Singleton
    fun provideSendNotificationUseCase(
        notificationRepository: NotificationRepository,
        settingsRepository: SettingsRepository
    ): SendNotificationUseCase = SendNotificationUseCase(notificationRepository, settingsRepository)

    @Provides
    @Singleton
    fun provideTestBackendConnectionUseCase(
        settingsRepository: SettingsRepository
    ): TestBackendConnectionUseCase = TestBackendConnectionUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideExportConfigurationUseCase(
        settingsRepository: SettingsRepository
    ): ExportConfigurationUseCase = ExportConfigurationUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideImportConfigurationUseCase(
        settingsRepository: SettingsRepository
    ): ImportConfigurationUseCase = ImportConfigurationUseCase(settingsRepository)

    @Provides
    @Singleton
    fun provideCloudinaryRepository(
        @ApplicationContext context: Context,
        cloudinaryApiService: CloudinaryApiService
    ): CloudinaryRepository = CloudinaryRepositoryImpl(context, cloudinaryApiService)

    @Provides
    @Singleton
    fun provideCrashlyticsRepository(
        crashlyticsDao: CrashlyticsDao,
        apiService: NetlifyApiService
    ): CrashlyticsRepository = CrashlyticsRepositoryImpl(crashlyticsDao, apiService)

    @Provides
    @Singleton
    fun provideAnalyticsRepository(
        analyticsDao: AnalyticsDao,
        apiService: NetlifyApiService,
        appDao: AppDao,
        firebaseProjectDao: FirebaseProjectDao
    ): AnalyticsRepository = AnalyticsRepositoryImpl(analyticsDao, apiService, appDao, firebaseProjectDao)

    @Provides
    @Singleton
    fun provideSeedInitialDataUseCase(
        appDao: AppDao,
        firebaseProjectDao: FirebaseProjectDao,
        topicDao: TopicDao,
        templateDao: TemplateDao,
        notificationHistoryDao: NotificationHistoryDao
    ): SeedInitialDataUseCase = SeedInitialDataUseCase(
        appDao, firebaseProjectDao, topicDao, templateDao, notificationHistoryDao
    )
}
