package com.ivarna.truvalt.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ivarna.truvalt.presentation.ui.auth.LoginScreen
import com.ivarna.truvalt.presentation.ui.auth.RegisterScreen
import com.ivarna.truvalt.presentation.ui.auth.ServerSetupScreen
import com.ivarna.truvalt.presentation.ui.auth.SplashScreen
import com.ivarna.truvalt.presentation.ui.settings.SettingsScreen
import com.ivarna.truvalt.presentation.ui.vault.VaultHomeScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object ServerSetup : Screen("server_setup")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object VaultHome : Screen("vault_home")
    data object VaultItemDetail : Screen("vault_item/{itemId}") {
        fun createRoute(itemId: String) = "vault_item/$itemId"
    }
    data object VaultItemCreate : Screen("vault_item/create?type={type}") {
        fun createRoute(type: String? = null) = if (type != null) "vault_item/create?type=$type" else "vault_item/create"
    }
    data object VaultItemEdit : Screen("vault_item/edit/{itemId}") {
        fun createRoute(itemId: String) = "vault_item/edit/$itemId"
    }
    data object Generator : Screen("generator")
    data object Health : Screen("health")
    data object Import : Screen("import")
    data object Export : Screen("export")
    data object Settings : Screen("settings")
    data object Trash : Screen("trash")
}

@Composable
fun TruvaltNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToServerSetup = {
                    navController.navigate(Screen.ServerSetup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToVault = {
                    navController.navigate(Screen.VaultHome.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ServerSetup.route) {
            ServerSetupScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.ServerSetup.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.ServerSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToVault = {
                    navController.navigate(Screen.VaultHome.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToVault = {
                    navController.navigate(Screen.VaultHome.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.VaultHome.route) {
            VaultHomeScreen(
                onNavigateToItemDetail = { itemId ->
                    navController.navigate(Screen.VaultItemDetail.createRoute(itemId))
                },
                onNavigateToItemCreate = { type ->
                    navController.navigate(Screen.VaultItemCreate.createRoute(type))
                },
                onNavigateToGenerator = {
                    navController.navigate(Screen.Generator.route)
                },
                onNavigateToHealth = {
                    navController.navigate(Screen.Health.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToTrash = {
                    navController.navigate(Screen.Trash.route)
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { navController.navigate(Screen.VaultItemEdit.createRoute(itemId)) }
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
                onNavigateBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
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
                onNavigateBack = { navController.popBackStack() },
                onSaveComplete = { navController.popBackStack() }
            )
        }

        composable(Screen.Generator.route) {
            com.ivarna.truvalt.presentation.ui.generator.GeneratorScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Health.route) {
            com.ivarna.truvalt.presentation.ui.health.HealthScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToItem = { itemId ->
                    navController.navigate(Screen.VaultItemDetail.createRoute(itemId))
                }
            )
        }

        composable(Screen.Import.route) {
            com.ivarna.truvalt.presentation.ui.import.ImportScreen(
                onNavigateBack = { navController.popBackStack() },
                onImportComplete = { navController.popBackStack() }
            )
        }

        composable(Screen.Export.route) {
            com.ivarna.truvalt.presentation.ui.settings.ExportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Trash.route) {
            com.ivarna.truvalt.presentation.ui.vault.TrashScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToItem = { itemId ->
                    navController.navigate(Screen.VaultItemDetail.createRoute(itemId))
                }
            )
        }
    }
}
