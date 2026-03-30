package com.ivarna.truvalt.presentation.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.core.biometric.BiometricStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPinSetup: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val biometricStatus = remember { viewModel.biometricHelper.canAuthenticate() }
    val isPinEnabled = remember { viewModel.pinStorage.isEnabled() }
    var showAutoLockDialog by remember { mutableStateOf(false) }
    val biometricToggleEnabled =
        biometricStatus == BiometricStatus.AVAILABLE && uiState.canUseBiometricUnlock
    
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
                                    BiometricStatus.AVAILABLE -> {
                                        if (uiState.canUseBiometricUnlock) {
                                            "Use fingerprint or face"
                                        } else {
                                            "Unavailable until a keystore-backed vault key is set up"
                                        }
                                    }
                                    BiometricStatus.NONE_ENROLLED -> "No biometric credentials enrolled on this device"
                                    BiometricStatus.UNAVAILABLE -> "Not available on this device"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isBiometricEnabled,
                            onCheckedChange = { viewModel.setBiometricEnabled(it) },
                            enabled = biometricToggleEnabled
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
                                    viewModel.pinStorage.clear()
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
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Auto-lock",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showAutoLockDialog = true }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Auto-lock Timeout", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = uiState.autoLockLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        if (showAutoLockDialog) {
            AlertDialog(
                onDismissRequest = { showAutoLockDialog = false },
                title = { Text("Auto-lock Timeout") },
                text = {
                    Column {
                        listOf(
                            0L to "Immediately (when app is closed)",
                            60000L to "1 minute",
                            300000L to "5 minutes",
                            900000L to "15 minutes",
                            3600000L to "1 hour",
                            -1L to "Never"
                        ).forEach { (timeout, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.autoLockTimeout == timeout,
                                    onClick = {
                                        viewModel.setAutoLockTimeout(timeout)
                                        showAutoLockDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(label)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAutoLockDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
