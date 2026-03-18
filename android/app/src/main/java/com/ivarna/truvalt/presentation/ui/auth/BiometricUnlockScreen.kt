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
import com.ivarna.truvalt.core.biometric.BiometricPromptManager

@Composable
fun BiometricUnlockScreen(
    onUnlockSuccess: () -> Unit,
    onFallbackToPin: () -> Unit,
) {
    val activity = LocalContext.current as AppCompatActivity
    val biometricManager = remember { BiometricPromptManager(activity) }
    
    LaunchedEffect(Unit) {
        biometricManager.results.collect { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.Success -> onUnlockSuccess()
                is BiometricPromptManager.BiometricResult.FallbackRequested -> onFallbackToPin()
                is BiometricPromptManager.BiometricResult.NotEnrolled -> onFallbackToPin()
                is BiometricPromptManager.BiometricResult.HardwareUnavailable -> onFallbackToPin()
                else -> { /* Failed — prompt re-shows automatically */ }
            }
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
                "Touch the sensor to unlock",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
