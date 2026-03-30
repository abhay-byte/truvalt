package com.ivarna.truvalt.presentation.ui.auth

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivarna.truvalt.core.biometric.BiometricPromptManager

@Composable
fun BiometricUnlockScreen(
    onUnlockSuccess: () -> Unit,
    onFallbackToPin: () -> Unit,
    viewModel: BiometricUnlockViewModel = hiltViewModel()
) {
    val activity = LocalContext.current as AppCompatActivity
    val biometricManager = remember { BiometricPromptManager(activity) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        biometricManager.results.collect { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.Success -> viewModel.onBiometricSuccess()
                is BiometricPromptManager.BiometricResult.Failed -> viewModel.onBiometricFailed()
                is BiometricPromptManager.BiometricResult.FallbackRequested -> viewModel.onFallbackToPIN()
                is BiometricPromptManager.BiometricResult.NotEnrolled -> viewModel.onFallbackToPIN()
                is BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                    viewModel.onBiometricFailed("Biometric hardware is unavailable.")
                }
                is BiometricPromptManager.BiometricResult.Error -> {
                    viewModel.onBiometricFailed(result.message)
                }
            }
        }
    }
    
    LaunchedEffect(uiState) {
        when (uiState) {
            BiometricUnlockState.Success -> onUnlockSuccess()
            BiometricUnlockState.FallbackToPIN -> onFallbackToPin()
            else -> Unit
        }
    }

    LaunchedEffect(Unit) {
        biometricManager.showPrompt()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                when (val state = uiState) {
                    is BiometricUnlockState.Error -> state.message
                    else -> "Touch the sensor to unlock"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = when (uiState) {
                    is BiometricUnlockState.Error -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = { biometricManager.showPrompt() }) {
                Text("Try again")
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onFallbackToPin) {
                Text("Use PIN instead")
            }
        }
    }
}
