package com.ivarna.truvalt.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ivarna.truvalt.presentation.ui.auth.*
import com.ivarna.truvalt.presentation.ui.settings.SettingsScreen
import com.ivarna.truvalt.presentation.ui.vault.VaultHomeScreen
import com.ivarna.truvalt.presentation.ui.vault.VaultItemTypeSelectionScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object ServerSetup : Screen("server_setup")
    data object MasterPasswordSetup : Screen("master_password_setup")
    data object MasterPasswordUnlock : Screen("master_password_unlock")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object BiometricUnlock : Screen("biometric_unlock")
    data object PinUnlock : Screen("pin_unlock")
    data object PinSetup : Screen("pin_setup")
    data object Main : Screen("main")
    data object VaultHome : Screen("vault_home")
    data object VaultItemTypeSelection : Screen("vault_item/type_selection")
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
    data object SecuritySettings : Screen("security_settings")
    data object Trash : Screen("trash")
}

@Composable
fun TruvaltNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLocked by viewModel.isLocked.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val isPinEnabled by viewModel.isPinEnabled.collectAsState()
    val isFirstLaunch by viewModel.isFirstLaunch.collectAsState()
    val hasMasterPassword by viewModel.hasMasterPassword.collectAsState()
    
    // Check biometric availability dynamically
    val isBiometricAvailable = remember(isLocked) {
        viewModel.isBiometricAvailable()
    }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            com.ivarna.truvalt.presentation.ui.auth.SplashScreen(
                onNavigationDecided = { destination ->
                    val route = when (destination) {
                        SplashDestination.ONBOARDING -> Screen.Onboarding.route
                        SplashDestination.UNLOCK_BIOMETRIC -> Screen.BiometricUnlock.route
                        SplashDestination.UNLOCK_PIN -> Screen.PinUnlock.route
                        SplashDestination.UNLOCK_MASTER_PASSWORD -> Screen.MasterPasswordUnlock.route
                        SplashDestination.VAULT_HOME -> Screen.Main.route
                    }
                    navController.navigate(route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                isFirstLaunch = isFirstLaunch,
                isLocked = isLocked,
                isBiometricEnabled = isBiometricAvailable,
                isPinEnabled = isPinEnabled,
                hasMasterPassword = hasMasterPassword
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.ServerSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Screen.ServerSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BiometricUnlock.route) {
            BiometricUnlockScreen(
                onUnlockSuccess = {
                    viewModel.unlock()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onFallbackToPin = {
                    navController.navigate(Screen.PinUnlock.route) {
                        popUpTo(Screen.BiometricUnlock.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinUnlock.route) {
            PinUnlockScreen(
                onUnlockSuccess = {
                    viewModel.unlock()
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onForgotPin = {
                    navController.navigate(Screen.MasterPasswordUnlock.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinSetup.route) {
            PinSetupScreen(
                onComplete = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
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
                },
                onNavigateToVault = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MasterPasswordSetup.route) {
            MasterPasswordSetupScreen(
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MasterPasswordUnlock.route) {
            MasterPasswordUnlockScreen(
                onUnlockSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
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
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToVault = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScaffold(navController)
        }
    }
}
