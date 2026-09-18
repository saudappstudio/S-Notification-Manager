package com.saudappstudio.snotificationmanager.provider

import com.saudappstudio.snotificationmanager.domain.model.AppModel
import com.saudappstudio.snotificationmanager.domain.model.Environment
import com.saudappstudio.snotificationmanager.domain.model.FirebaseProjectModel
import com.saudappstudio.snotificationmanager.domain.model.NotificationHistoryModel
import com.saudappstudio.snotificationmanager.domain.model.TargetType
import com.saudappstudio.snotificationmanager.domain.model.TemplateModel
import com.saudappstudio.snotificationmanager.domain.model.TopicModel

/**
 * Provider supplying realistic initial sample data for demonstration and first-run database population.
 */
object SampleDataProvider {

    val firebaseProjects: List<FirebaseProjectModel> = listOf(
        FirebaseProjectModel(
            id = "proj_dictionary",
            name = "Saud Dictionary",
            projectIdentifier = "saud-dictionary-prod",
            environment = Environment.PRODUCTION,
            backendKey = "dictionary",
            enabled = true
        ),
        FirebaseProjectModel(
            id = "proj_vocabulary",
            name = "Saud Vocabulary",
            projectIdentifier = "saud-vocabulary-prod",
            environment = Environment.PRODUCTION,
            backendKey = "vocabulary",
            enabled = true
        ),
        FirebaseProjectModel(
            id = "proj_calculator",
            name = "Saud Calculator",
            projectIdentifier = "saud-calculator-prod",
            environment = Environment.PRODUCTION,
            backendKey = "calculator",
            enabled = true
        )
    )

    val applications: List<AppModel> = listOf(
        AppModel(
            id = "app_dictionary",
            name = "Advanced English Dictionary",
            packageName = "com.saudappstudio.dictionary",
            appId = "aed_prod_01",
            iconName = "ic_notification_logo",
            firebaseProjectId = "proj_dictionary",
            environment = Environment.PRODUCTION,
            defaultTopic = "dictionary_all",
            enabled = true,
            testMode = false,
            allowPush = true,
            allowTopic = true,
            allowToken = true,
            allowImage = true,
            allowDeepLinks = true,
            requireConfirmForProd = true,
            defaultClickAction = "DEEP_LINK",
            defaultChannelId = "word_of_day_channel",
            description = "Comprehensive English lexicon and offline vocabulary reference."
        ),
        AppModel(
            id = "app_vocabulary",
            name = "Vocabulary Builder",
            packageName = "com.saudappstudio.vocabulary",
            appId = "vocab_prod_01",
            iconName = "ic_notification_logo",
            firebaseProjectId = "proj_vocabulary",
            environment = Environment.PRODUCTION,
            defaultTopic = "vocabulary_all",
            enabled = true,
            testMode = false,
            allowPush = true,
            allowTopic = true,
            allowToken = true,
            allowImage = true,
            allowDeepLinks = true,
            requireConfirmForProd = true,
            defaultClickAction = "OPEN_APP",
            defaultChannelId = "daily_quiz_channel",
            description = "Gamified vocabulary acquisition and flashcard training system."
        ),
        AppModel(
            id = "app_calculator",
            name = "Calculator",
            packageName = "com.saudappstudio.calculator",
            appId = "calc_prod_01",
            iconName = "ic_notification_logo",
            firebaseProjectId = "proj_calculator",
            environment = Environment.PRODUCTION,
            defaultTopic = "calculator_all",
            enabled = true,
            testMode = false,
            allowPush = true,
            allowTopic = true,
            allowToken = true,
            allowImage = false,
            allowDeepLinks = false,
            requireConfirmForProd = true,
            defaultClickAction = "OPEN_APP",
            defaultChannelId = "updates_channel",
            description = "High precision scientific and productivity calculator."
        )
    )

    val topics: List<TopicModel> = listOf(
        TopicModel(
            id = "topic_global",
            name = "global",
            description = "Global audience across all subscribers",
            appId = "",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_dict_all",
            name = "dictionary_all",
            description = "All dictionary users",
            appId = "app_dictionary",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_dict_wod",
            name = "dictionary_word_of_day",
            description = "Word of the Day notifications",
            appId = "app_dictionary",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_dict_updates",
            name = "dictionary_updates",
            description = "App updates and release announcements",
            appId = "app_dictionary",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_dict_premium",
            name = "dictionary_premium",
            description = "Premium subscription users",
            appId = "app_dictionary",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_vocab_all",
            name = "vocabulary_all",
            description = "All vocabulary users",
            appId = "app_vocabulary",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_vocab_daily",
            name = "vocabulary_daily",
            description = "Daily study streak reminders",
            appId = "app_vocabulary",
            environment = Environment.PRODUCTION,
            enabled = true
        ),
        TopicModel(
            id = "topic_calc_all",
            name = "calculator_all",
            description = "All calculator users",
            appId = "app_calculator",
            environment = Environment.PRODUCTION,
            enabled = true
        )
    )

    val templates: List<TemplateModel> = listOf(
        TemplateModel(
            id = "tpl_wod",
            name = "Word of the Day",
            appId = "app_dictionary",
            title = "Word of the Day: {{word}}",
            message = "Expand your vocabulary! {{word}} means: {{definition}}",
            imageUrl = "https://images.unsplash.com/photo-1457369804613-52c61a468e7d?w=800",
            topic = "dictionary_word_of_day",
            clickAction = "DEEP_LINK",
            deepLink = "sauddictionary://word/{{word}}",
            customData = mapOf("screen" to "word_detail", "source" to "fcm_push"),
            isFavorite = true
        ),
        TemplateModel(
            id = "tpl_daily_reminder",
            name = "Daily Reminder",
            appId = "app_vocabulary",
            title = "Ready for today's 3-minute quiz?",
            message = "Keep your {{user_name}} streak active! 10 new words are waiting for you.",
            imageUrl = "",
            topic = "vocabulary_daily",
            clickAction = "OPEN_APP",
            deepLink = "",
            customData = mapOf("action" to "quiz"),
            isFavorite = true
        ),
        TemplateModel(
            id = "tpl_update",
            name = "App Update Announcement",
            appId = "app_dictionary",
            title = "New features in {{app_name}}!",
            message = "We've added audio pronunciations and enhanced offline definitions. Update now!",
            imageUrl = "",
            topic = "dictionary_updates",
            clickAction = "OPEN_APP",
            deepLink = "",
            customData = mapOf("category" to "update"),
            isFavorite = false
        ),
        TemplateModel(
            id = "tpl_vocab_challenge",
            name = "Vocabulary Challenge",
            appId = "app_vocabulary",
            title = "Weekend Word Challenge!",
            message = "Master 20 GRE level words this weekend and earn a gold badge.",
            imageUrl = "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=800",
            topic = "vocabulary_all",
            clickAction = "OPEN_APP",
            deepLink = "",
            customData = emptyMap(),
            isFavorite = false
        ),
        TemplateModel(
            id = "tpl_maintenance",
            name = "Maintenance Notice",
            appId = "app_dictionary",
            title = "Scheduled Sync Maintenance",
            message = "Cloud backup services will be undergoing brief maintenance on {{date}} at {{time}}.",
            imageUrl = "",
            topic = "dictionary_all",
            clickAction = "NONE",
            deepLink = "",
            customData = emptyMap(),
            isFavorite = false
        )
    )

    val history: List<NotificationHistoryModel> = listOf(
        NotificationHistoryModel(
            id = "hist_01",
            appId = "app_dictionary",
            appName = "Advanced English Dictionary",
            title = "Word of the Day",
            message = "Discover today's new word: Serendipity - finding valuable things unexpectedly.",
            targetType = TargetType.TOPIC,
            target = "dictionary_all",
            environment = Environment.PRODUCTION,
            status = "SENT",
            messageId = "projects/saud-dictionary-prod/messages/fcm_883210941",
            sentAt = System.currentTimeMillis() - 120000L
        ),
        NotificationHistoryModel(
            id = "hist_02",
            appId = "app_vocabulary",
            appName = "Vocabulary Builder",
            title = "Daily Streak Reminder",
            message = "You have 4 hours left to complete today's review session!",
            targetType = TargetType.TOPIC,
            target = "vocabulary_daily",
            environment = Environment.PRODUCTION,
            status = "SENT",
            messageId = "projects/saud-vocabulary-prod/messages/fcm_773120194",
            sentAt = System.currentTimeMillis() - 7200000L
        ),
        NotificationHistoryModel(
            id = "hist_03",
            appId = "app_calculator",
            appName = "Calculator",
            title = "Beta Test Formula Solver",
            message = "Testing token dispatch to internal test devices.",
            targetType = TargetType.TOKEN,
            target = "dK_92xLz:APA91bF83jx...test_token",
            environment = Environment.TESTING,
            status = "SENT",
            messageId = "projects/saud-calculator-prod/messages/fcm_554109281",
            sentAt = System.currentTimeMillis() - 86400000L
        ),
        NotificationHistoryModel(
            id = "hist_04",
            appId = "app_dictionary",
            appName = "Advanced English Dictionary",
            title = "Test Invalid Topic Dispatch",
            message = "Intentional negative test dispatch to check error logging.",
            targetType = TargetType.TOPIC,
            target = "invalid_topic_name!#",
            environment = Environment.TESTING,
            status = "FAILED",
            error = "HTTP 400: Malformed topic name. Must match [a-zA-Z0-9-_.~%]+",
            sentAt = System.currentTimeMillis() - 172800000L
        ),
        NotificationHistoryModel(
            id = "hist_05",
            appId = "app_dictionary",
            appName = "Advanced English Dictionary",
            title = "Scheduled Daily Reminder",
            message = "Scheduled quiz notification to boost active retention.",
            targetType = TargetType.TOPIC,
            target = "dictionary_all",
            environment = Environment.PRODUCTION,
            status = "SCHEDULED",
            messageId = "sched_88192a",
            notificationType = "PUSH",
            isScheduled = true,
            scheduledTimestamp = System.currentTimeMillis() + 3600000L,
            sentAt = System.currentTimeMillis() + 3600000L
        ),
        NotificationHistoryModel(
            id = "hist_06",
            appId = "app_vocabulary",
            appName = "Vocabulary Builder",
            title = "In-App Streak Booster",
            message = "Triggered after 1 minute of activity in the app.",
            targetType = TargetType.TOPIC,
            target = "vocabulary_all",
            environment = Environment.PRODUCTION,
            status = "SENT",
            messageId = "iam_msg_10283",
            notificationType = "IN_APP",
            eventTrigger = "timer_1_min",
            sentAt = System.currentTimeMillis() - 300000L
        )
    )
}
