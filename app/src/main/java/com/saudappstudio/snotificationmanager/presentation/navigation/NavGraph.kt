package com.saudappstudio.snotificationmanager.presentation.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.saudappstudio.snotificationmanager.presentation.apps.AddAppScreen
import com.saudappstudio.snotificationmanager.presentation.apps.AppDetailsScreen
import com.saudappstudio.snotificationmanager.presentation.apps.AppsScreen
import com.saudappstudio.snotificationmanager.presentation.apps.AppsViewModel
import com.saudappstudio.snotificationmanager.presentation.firebase.AddFirebaseProjectScreen
import com.saudappstudio.snotificationmanager.presentation.firebase.FirebaseProjectsScreen
import com.saudappstudio.snotificationmanager.presentation.firebase.FirebaseProjectsViewModel
import com.saudappstudio.snotificationmanager.presentation.history.HistoryScreen
import com.saudappstudio.snotificationmanager.presentation.history.HistoryViewModel
import com.saudappstudio.snotificationmanager.presentation.history.NotificationDetailsScreen
import com.saudappstudio.snotificationmanager.presentation.home.HomeScreen
import com.saudappstudio.snotificationmanager.presentation.home.HomeViewModel
import com.saudappstudio.snotificationmanager.presentation.send.SendNotificationScreen
import com.saudappstudio.snotificationmanager.presentation.send.SendNotificationViewModel
import com.saudappstudio.snotificationmanager.presentation.settings.SettingsScreen
import com.saudappstudio.snotificationmanager.presentation.settings.SettingsViewModel
import com.saudappstudio.snotificationmanager.presentation.templates.CreateTemplateScreen
import com.saudappstudio.snotificationmanager.presentation.templates.TemplatesScreen
import com.saudappstudio.snotificationmanager.presentation.templates.TemplatesViewModel
import com.saudappstudio.snotificationmanager.presentation.topics.AddTopicScreen
import com.saudappstudio.snotificationmanager.presentation.topics.TopicsScreen
import com.saudappstudio.snotificationmanager.presentation.topics.TopicsViewModel

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Apps,
        BottomNavItem.Templates,
        BottomNavItem.History,
        BottomNavItem.Settings
    )

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = item.icon, contentDescription = stringResource(item.labelRes)) },
                            label = { Text(stringResource(item.labelRes)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Home
            composable(Screen.Home.route) {
                val viewModel: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onAppClick = { appId -> navController.navigate(Screen.AppDetails.createRoute(appId)) },
                    onNotificationClick = { historyId -> navController.navigate(Screen.NotificationDetails.createRoute(historyId)) }
                )
            }

            // Apps
            composable(Screen.Apps.route) {
                val viewModel: AppsViewModel = hiltViewModel()
                AppsScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onAppClick = { appId -> navController.navigate(Screen.AppDetails.createRoute(appId)) },
                    onEditAppClick = { appId -> navController.navigate(Screen.EditApp.createRoute(appId)) }
                )
            }

            composable(Screen.AddApp.route) {
                val viewModel: AppsViewModel = hiltViewModel()
                AddAppScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EditApp.route,
                arguments = listOf(navArgument("appId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appId = backStackEntry.arguments?.getString("appId") ?: ""
                val viewModel: AppsViewModel = hiltViewModel()
                AddAppScreen(
                    initialAppId = appId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AppDetails.route,
                arguments = listOf(navArgument("appId") { type = NavType.StringType })
            ) { backStackEntry ->
                val appId = backStackEntry.arguments?.getString("appId") ?: ""
                val viewModel: AppsViewModel = hiltViewModel()
                AppDetailsScreen(
                    appId = appId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onEditApp = { id -> navController.navigate(Screen.EditApp.createRoute(id)) },
                    onSendNotificationForApp = { id ->
                        navController.navigate(Screen.SendNotification.createRoute(appId = id))
                    },
                    onNotificationClick = { historyId ->
                        navController.navigate(Screen.NotificationDetails.createRoute(historyId))
                    }
                )
            }

            // Firebase Projects
            composable(Screen.FirebaseProjects.route) {
                val viewModel: FirebaseProjectsViewModel = hiltViewModel()
                FirebaseProjectsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onAddProjectClick = { navController.navigate(Screen.AddFirebaseProject.route) }
                )
            }

            composable(Screen.AddFirebaseProject.route) {
                val viewModel: FirebaseProjectsViewModel = hiltViewModel()
                AddFirebaseProjectScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Topics
            composable(Screen.Topics.route) {
                val viewModel: TopicsViewModel = hiltViewModel()
                TopicsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onAddTopicClick = { navController.navigate(Screen.AddTopic.route) }
                )
            }

            composable(Screen.AddTopic.route) {
                val viewModel: TopicsViewModel = hiltViewModel()
                AddTopicScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Templates
            composable(Screen.Templates.route) {
                val viewModel: TemplatesViewModel = hiltViewModel()
                TemplatesScreen(
                    viewModel = viewModel,
                    onNavigate = { route -> navController.navigate(route) },
                    onUseTemplate = { tpl ->
                        navController.navigate(Screen.SendNotification.createRoute(appId = tpl.appId, templateId = tpl.id))
                    }
                )
            }

            composable(Screen.CreateTemplate.route) {
                val viewModel: TemplatesViewModel = hiltViewModel()
                CreateTemplateScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveAndSend = { tpl ->
                        navController.navigate(Screen.SendNotification.createRoute(appId = tpl.appId, templateId = tpl.id))
                    }
                )
            }

            // Send Notification (Primary Screen)
            composable(
                route = Screen.SendNotification.route,
                arguments = listOf(
                    navArgument("appId") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                    navArgument("templateId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { backStackEntry ->
                val appId = backStackEntry.arguments?.getString("appId") ?: ""
                val templateId = backStackEntry.arguments?.getString("templateId") ?: ""
                val viewModel: SendNotificationViewModel = hiltViewModel()
                SendNotificationScreen(
                    viewModel = viewModel,
                    initialAppId = appId,
                    initialTemplateId = templateId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // History
            composable(Screen.History.route) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToDetails = { historyId ->
                        navController.navigate(Screen.NotificationDetails.createRoute(historyId))
                    }
                )
            }

            composable(
                route = Screen.NotificationDetails.route,
                arguments = listOf(navArgument("historyId") { type = NavType.StringType })
            ) { backStackEntry ->
                val historyId = backStackEntry.arguments?.getString("historyId") ?: ""
                val viewModel: HistoryViewModel = hiltViewModel()
                NotificationDetailsScreen(
                    historyId = historyId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onSendAgain = { appId, _ ->
                        navController.navigate(Screen.SendNotification.createRoute(appId = appId))
                    }
                )
            }

            // Settings
            composable(Screen.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
