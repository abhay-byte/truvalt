package com.ivarna.truvalt.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.core.biometric.BiometricHelper
import com.ivarna.truvalt.core.biometric.BiometricStatus
import com.ivarna.truvalt.core.pin.PinStorage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPinSetup: () -> Unit,
    biometricHelper: BiometricHelper = hiltViewModel<SettingsViewModel>().biometricHelper,
    pinStorage: PinStorage = hiltViewModel<SettingsViewModel>().pinStorage
) {
    val biometricStatus = remember { biometricHelper.canAuthenticate() }
    val isPinEnabled = remember { pinStorage.isEnabled() }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Security Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Unlock Methods",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Biometric Unlock", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = when (biometricStatus) {
                                    BiometricStatus.AVAILABLE -> "Use fingerprint or face"
                                    BiometricStatus.NONE_ENROLLED -> "No biometric credentials enrolled on this device"
                                    BiometricStatus.UNAVAILABLE -> "Not available on this device"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = false,
                            onCheckedChange = {},
                            enabled = biometricStatus == BiometricStatus.AVAILABLE
                        )
                    }
                }
            }
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PIN Lock", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = if (isPinEnabled) "PIN is enabled" else "Set a 4-8 digit PIN",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    onNavigateToPinSetup()
                                } else {
                                    pinStorage.clear()
                                }
                            }
                        )
                    }
                    
                    if (isPinEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onNavigateToPinSetup) {
                            Text("Change PIN")
                        }
                    }
                }
            }
        }
    }
}
