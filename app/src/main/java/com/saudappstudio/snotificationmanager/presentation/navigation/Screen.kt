package com.saudappstudio.snotificationmanager.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.saudappstudio.snotificationmanager.R

/**
 * Sealed class representing application destination routes.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Apps : Screen("apps")
    object AddApp : Screen("add_app")
    object AppDetails : Screen("app_details/{appId}") {
        fun createRoute(appId: String) = "app_details/$appId"
    }
    object EditApp : Screen("edit_app/{appId}") {
        fun createRoute(appId: String) = "edit_app/$appId"
    }
    object FirebaseProjects : Screen("firebase_projects")
    object AddFirebaseProject : Screen("add_firebase_project")
    object Topics : Screen("topics")
    object AddTopic : Screen("add_topic")
    object Templates : Screen("templates")
    object CreateTemplate : Screen("create_template")
    object EditTemplate : Screen("edit_template/{templateId}") {
        fun createRoute(templateId: String) = "edit_template/$templateId"
    }
    object SendNotification : Screen("send_notification?appId={appId}&templateId={templateId}") {
        fun createRoute(appId: String = "", templateId: String = "") =
            "send_notification?appId=$appId&templateId=$templateId"
    }
    object InAppMessaging : Screen("in_app_messaging?appId={appId}") {
        fun createRoute(appId: String = "") = "in_app_messaging?appId=$appId"
    }
    object History : Screen("history")
    object NotificationDetails : Screen("notification_details/{historyId}") {
        fun createRoute(historyId: String) = "notification_details/$historyId"
    }
    object CrashlyticsDashboard : Screen("crashlytics_dashboard?appId={appId}") {
        fun createRoute(appId: String = "") = "crashlytics_dashboard?appId=$appId"
    }
    object AnalyticsDashboard : Screen("analytics_dashboard?appId={appId}") {
        fun createRoute(appId: String = "") = "analytics_dashboard?appId=$appId"
    }
    object CrashDetails : Screen("crash_details/{issueId}") {
        fun createRoute(issueId: String) = "crash_details/$issueId"
    }
    object Settings : Screen("settings")
    object BackendSettings : Screen("backend_settings")
    object SecuritySettings : Screen("security_settings")
    object About : Screen("about")
}

/**
 * Data representation for bottom navigation bar items.
 */
sealed class BottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.Home.route, R.string.nav_home, Icons.Default.Home)
    object Apps : BottomNavItem(Screen.Apps.route, R.string.nav_apps, Icons.Default.Apps)
    object Templates : BottomNavItem(Screen.Templates.route, R.string.nav_templates, Icons.Default.Message)
    object History : BottomNavItem(Screen.History.route, R.string.nav_history, Icons.Default.History)
    object Settings : BottomNavItem(Screen.Settings.route, R.string.nav_settings, Icons.Default.Settings)
}
