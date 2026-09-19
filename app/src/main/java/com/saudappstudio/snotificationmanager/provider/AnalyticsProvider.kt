package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R

/**
 * Data structure for time range selection option items.
 *
 * @property key Code identifier (e.g. "7D").
 * @property labelRes String resource reference for localization.
 */
data class TimeRangeOption(
    val key: String,
    @StringRes val labelRes: Int
)

/**
 * Provider supplying static time-range filter presets.
 */
object AnalyticsProvider {

    /**
     * List of supported time range filter options referencing string resources.
     */
    val timeRangeOptions: List<TimeRangeOption> = listOf(
        TimeRangeOption(key = "TODAY", labelRes = R.string.analytics_time_today),
        TimeRangeOption(key = "7D", labelRes = R.string.analytics_time_7d),
        TimeRangeOption(key = "30D", labelRes = R.string.analytics_time_30d),
        TimeRangeOption(key = "90D", labelRes = R.string.analytics_time_90d)
    )
}
