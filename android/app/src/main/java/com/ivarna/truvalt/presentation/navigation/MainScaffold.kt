package com.ivarna.truvalt.presentation.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
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
        BottomNavItem("generator_graph", "generator_graph", "Sharing", Icons.Default.Group),
        BottomNavItem("health_graph", "health_graph", "Security", Icons.Default.VerifiedUser),
        BottomNavItem("settings_graph", "settings_graph", "Settings", Icons.Default.Settings)
    )

    Scaffold(
        bottomBar = {
            Surface(
                color = Color(0xCCFCF8FE), // Matches #fcf8fe/80
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        // Note: padding from bottom for safe drawing should be handled by windowInsets padding values
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(top = 12.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == item.graph } == true
                        val color = if (isSelected) Color(0xFF5850BD) else Color(0xFF7C7984) // Primary vs Outline
                        val bgColor = if (isSelected) Color(0xFF5850BD).copy(alpha = 0.1f) else Color.Transparent

                        Column(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
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
                                .background(bgColor, RoundedCornerShape(16.dp))
                                .padding(horizontal = 20.dp, vertical = 6.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Icon(item.icon, contentDescription = item.label, tint = color, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = color
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = tabNavController,
            startDestination = "vault_graph",
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
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
                            rootNavController.navigate(Screen.PinSetup.route)
                        }
                    )
                }

                composable(Screen.SecuritySettings.route) {
                    com.ivarna.truvalt.presentation.ui.settings.SecuritySettingsScreen(
                        onNavigateBack = { tabNavController.popBackStack() },
                        onNavigateToPinSetup = {
                            rootNavController.navigate(Screen.PinSetup.route)
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
