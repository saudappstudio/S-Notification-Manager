package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R

/**
 * Event trigger item for In-App Messaging notification targeting.
 */
data class EventTriggerOption(
    val eventKey: String,
    @StringRes val labelRes: Int
)

/**
 * Provider object offering predefined event triggers for In-App Messaging notifications.
 */
object InAppMessagingProvider {

    /**
     * Pre-configured timer and app event trigger options.
     */
    val eventOptions: List<EventTriggerOption> = listOf(
        EventTriggerOption("timer_1_min", R.string.event_timer_1_min),
        EventTriggerOption("timer_90_sec", R.string.event_timer_90_sec),
        EventTriggerOption("timer_2_min", R.string.event_timer_2_min),
        EventTriggerOption("timer_3_min", R.string.event_timer_3_min),
        EventTriggerOption("timer_4_min", R.string.event_timer_4_min),
        EventTriggerOption("app_open", R.string.event_app_open),
        EventTriggerOption("session_start", R.string.event_session_start),
        EventTriggerOption("first_purchase", R.string.event_first_purchase),
        EventTriggerOption("custom", R.string.event_custom)
    )
}
