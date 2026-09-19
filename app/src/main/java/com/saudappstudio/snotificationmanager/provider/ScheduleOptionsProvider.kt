package com.saudappstudio.snotificationmanager.provider

import androidx.annotation.StringRes
import com.saudappstudio.snotificationmanager.R

/**
 * Modes available for scheduling notification dispatch timing.
 *
 * @param labelRes String resource identifier for UI display label.
 */
enum class ScheduleType(@StringRes val labelRes: Int) {
    PRESETS(R.string.schedule_mode_presets),
    CUSTOM_DELAY(R.string.schedule_mode_custom_delay),
    EXACT_TIME(R.string.schedule_mode_exact_time)
}

/**
 * Time units available for custom relative delay scheduling.
 *
 * @param minutesMultiplier Multiplier to convert unit value into total delay minutes.
 * @param labelRes String resource identifier for UI display label.
 */
enum class ScheduleTimeUnit(val minutesMultiplier: Int, @StringRes val labelRes: Int) {
    MINUTES(1, R.string.schedule_unit_minutes),
    HOURS(60, R.string.schedule_unit_hours),
    DAYS(1440, R.string.schedule_unit_days)
}

/**
 * Data item representing a quick preset delay duration.
 *
 * @param delayMinutes Number of minutes in the preset delay.
 * @param labelRes String resource identifier for the display title.
 */
data class SchedulePresetOption(
    val delayMinutes: Int,
    @StringRes val labelRes: Int
)

/**
 * Provider supplying static datasets for notification dispatch scheduling.
 * Follows Clean Architecture and strict static data separation guidelines.
 */
object ScheduleOptionsProvider {
    /**
     * List of pre-configured quick schedule delay options.
     */
    val quickPresets: List<SchedulePresetOption> = listOf(
        SchedulePresetOption(5, R.string.schedule_preset_5min),
        SchedulePresetOption(15, R.string.schedule_preset_15min),
        SchedulePresetOption(30, R.string.schedule_preset_30min),
        SchedulePresetOption(60, R.string.schedule_preset_1hr),
        SchedulePresetOption(120, R.string.schedule_preset_2hr),
        SchedulePresetOption(360, R.string.schedule_preset_6hr),
        SchedulePresetOption(720, R.string.schedule_preset_12hr),
        SchedulePresetOption(1440, R.string.schedule_preset_1day),
        SchedulePresetOption(2880, R.string.schedule_preset_2day),
        SchedulePresetOption(10080, R.string.schedule_preset_1week)
    )

    /**
     * List of supported time units for relative delay calculation.
     */
    val timeUnits: List<ScheduleTimeUnit> = listOf(
        ScheduleTimeUnit.MINUTES,
        ScheduleTimeUnit.HOURS,
        ScheduleTimeUnit.DAYS
    )
}
