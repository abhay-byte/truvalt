package com.ivarna.truvalt.presentation.ui.auth

import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BiometricUnlockScreen(
    onUnlockSuccess: () -> Unit,
    onFallbackToPIN: () -> Unit,
    viewModel: BiometricUnlockViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val executor = remember { ContextCompat.getMainExecutor(context) }
    val biometricPrompt = remember {
        BiometricPrompt(
            activity!!,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    viewModel.onBiometricSuccess()
                }
                
                override fun onAuthenticationFailed() {
                    viewModel.onBiometricFailed()
                }
                
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_CANCELED,
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            viewModel.onFallbackToPIN()
                        }
                        else -> {
                            viewModel.onBiometricFailed()
                        }
                    }
                }
            }
        )
    }
    
    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Truvalt")
            .setSubtitle("Use your biometric to unlock your vault")
            .setNegativeButtonText("Use PIN instead")
            .build()
    }
    
    LaunchedEffect(Unit) {
        biometricPrompt.authenticate(promptInfo)
    }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            is BiometricUnlockState.Success -> onUnlockSuccess()
            is BiometricUnlockState.FallbackToPIN -> onFallbackToPIN()
            else -> {}
        }
    }
    
    BackHandler {
        // Prevent back navigation during biometric auth
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (val state = uiState) {
            is BiometricUnlockState.Authenticating -> {
                CircularProgressIndicator()
            }
            is BiometricUnlockState.Error -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { biometricPrompt.authenticate(promptInfo) }) {
                        Text("Try Again")
                    }
                }
            }
            else -> {}
        }
    }
}
