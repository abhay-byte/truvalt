package com.ivarna.truvalt.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation

data class BottomNavItem(
    val route: String,
    val graph: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun MainScaffold(rootNavController: NavHostController) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem("vault_graph", "vault_graph", "Vault", Icons.Default.Lock),
        BottomNavItem("generator_graph", "generator_graph", "Generator", Icons.Default.Key),
        BottomNavItem("health_graph", "health_graph", "Health", Icons.Default.Security),
        BottomNavItem("settings_graph", "settings_graph", "Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.graph } == true,
                        onClick = {
                            tabNavController.navigate(item.graph) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = "vault_graph"
        ) {
            navigation(startDestination = Screen.VaultHome.route, route = "vault_graph") {
                composable(Screen.VaultHome.route) {
                    com.ivarna.truvalt.presentation.ui.vault.VaultHomeScreen(
                        onNavigateToItemDetail = { itemId ->
                            tabNavController.navigate(Screen.VaultItemDetail.createRoute(itemId))
                        },
                        onNavigateToItemCreate = { type ->
                            tabNavController.navigate(Screen.VaultItemCreate.createRoute(type))
                        },
                        onNavigateToTypeSelection = {
                            tabNavController.navigate(Screen.VaultItemTypeSelection.route)
                        },
                        onNavigateToGenerator = {
                            tabNavController.navigate("generator_graph") {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToHealth = {
                            tabNavController.navigate("health_graph") {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToSettings = {
                            tabNavController.navigate("settings_graph") {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onNavigateToTrash = {
                            tabNavController.navigate(Screen.Trash.route)
                        },
                        onLockVault = {
                            rootNavController.navigate(Screen.PinUnlock.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.VaultItemTypeSelection.route) {
                    com.ivarna.truvalt.presentation.ui.vault.VaultItemTypeSelectionScreen(
                        onTypeSelected = { type ->
                            tabNavController.navigate(Screen.VaultItemCreate.createRoute(type.id)) {
                                popUpTo(Screen.VaultItemTypeSelection.route) { inclusive = true }
                            }
                        },
                        onDismiss = {
                            tabNavController.popBackStack()
                        }
                    )
                }

                composable(
                    route = Screen.VaultItemDetail.route,
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                    com.ivarna.truvalt.presentation.ui.vault.VaultItemDetailScreen(
                        itemId = itemId,
                        onNavigateBack = { tabNavController.popBackStack() },
                        onNavigateToEdit = { tabNavController.navigate(Screen.VaultItemEdit.createRoute(itemId)) }
                    )
                }

                composable(
                    route = Screen.VaultItemCreate.route,
                    arguments = listOf(navArgument("type") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    })
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type")
                    com.ivarna.truvalt.presentation.ui.vault.VaultItemEditScreen(
                        itemId = null,
                        itemType = type,
                        onNavigateBack = { tabNavController.popBackStack() },
                        onSaveComplete = { tabNavController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.VaultItemEdit.route,
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId") ?: return@composable
                    com.ivarna.truvalt.presentation.ui.vault.VaultItemEditScreen(
                        itemId = itemId,
                        itemType = null,
                        onNavigateBack = { tabNavController.popBackStack() },
                        onSaveComplete = { tabNavController.popBackStack() }
                    )
                }

                composable(Screen.Trash.route) {
                    com.ivarna.truvalt.presentation.ui.vault.TrashScreen(
                        onNavigateBack = { tabNavController.popBackStack() },
                        onNavigateToItem = { itemId ->
                            tabNavController.navigate(Screen.VaultItemDetail.createRoute(itemId))
                        }
                    )
                }
            }

            navigation(startDestination = Screen.Generator.route, route = "generator_graph") {
                composable(Screen.Generator.route) {
                    com.ivarna.truvalt.presentation.ui.generator.GeneratorScreen(
                        onNavigateBack = { }
                    )
                }
            }

            navigation(startDestination = Screen.Health.route, route = "health_graph") {
                composable(Screen.Health.route) {
                    com.ivarna.truvalt.presentation.ui.health.HealthScreen(
                        onNavigateBack = { },
                        onNavigateToItem = { itemId ->
                            tabNavController.navigate("vault_graph")
                            tabNavController.navigate(Screen.VaultItemDetail.createRoute(itemId))
                        }
                    )
                }
            }

            navigation(startDestination = Screen.Settings.route, route = "settings_graph") {
                composable(Screen.Settings.route) {
                    com.ivarna.truvalt.presentation.ui.settings.SettingsScreen(
                        onNavigateBack = { },
                        onNavigateToLogin = {
                            rootNavController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToSecuritySettings = {
                            tabNavController.navigate(Screen.SecuritySettings.route)
                        },
                        onNavigateToPinSetup = {
                            tabNavController.navigate(Screen.PinSetup.route)
                        }
                    )
                }

                composable(Screen.SecuritySettings.route) {
                    com.ivarna.truvalt.presentation.ui.settings.SecuritySettingsScreen(
                        onNavigateBack = { tabNavController.popBackStack() },
                        onNavigateToPinSetup = {
                            tabNavController.navigate(Screen.PinSetup.route)
                        }
                    )
                }

                composable(Screen.PinSetup.route) {
                    com.ivarna.truvalt.presentation.ui.auth.PinSetupScreen(
                        onComplete = {
                            tabNavController.popBackStack()
                        }
                    )
                }

                composable(Screen.Import.route) {
                    com.ivarna.truvalt.presentation.ui.import.ImportScreen(
                        onNavigateBack = { tabNavController.popBackStack() },
                        onImportComplete = { tabNavController.popBackStack() }
                    )
                }

                composable(Screen.Export.route) {
                    com.ivarna.truvalt.presentation.ui.settings.ExportScreen(
                        onNavigateBack = { tabNavController.popBackStack() }
                    )
                }
            }
        }
    }
}
