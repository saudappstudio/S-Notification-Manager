package com.saudappstudio.snotificationmanager.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Screen window width classification representing responsive form factors.
 */
enum class WindowSizeClass {
    COMPACT,
    MEDIUM,
    EXPANDED
}

/**
 * Utility helper to determine responsive layout sizing from Compose Configuration.
 */
object WindowSizeClassHelper {

    /**
     * Resolves the current WindowSizeClass according to device screen width.
     *
     * @return WindowSizeClass indicating Compact (<600dp), Medium (600-839dp), or Expanded (>=840dp).
     */
    @Composable
    fun rememberWindowSizeClass(): WindowSizeClass {
        val configuration = LocalConfiguration.current
        val screenWidthDp = configuration.screenWidthDp.dp
        return when {
            screenWidthDp < 600.dp -> WindowSizeClass.COMPACT
            screenWidthDp < 840.dp -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }
    }
}
