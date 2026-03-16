package com.ivarna.truvalt.presentation.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection(title = "Security") {
                SettingsItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Unlock",
                    subtitle = if (uiState.isBiometricEnabled) "Enabled" else "Disabled",
                    trailing = {
                        Switch(
                            checked = uiState.isBiometricEnabled,
                            onCheckedChange = { viewModel.setBiometricEnabled(it) }
                        )
                    }
                )

                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "Auto-lock",
                    subtitle = uiState.autoLockLabel,
                    onClick = { }
                )

                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "Clipboard Timeout",
                    subtitle = "${uiState.clipboardTimeout} seconds",
                    onClick = { }
                )
            }

            SettingsSection(title = "Appearance") {
                SettingsItem(
                    icon = Icons.Default.Brightness4,
                    title = "Theme",
                    subtitle = uiState.themeMode.replaceFirstChar { it.uppercase() },
                    onClick = { showThemeDialog = true }
                )
            }

            SettingsSection(title = "Sync") {
                SettingsItem(
                    icon = if (uiState.isLocalOnly) Icons.Default.CloudOff else Icons.Default.Cloud,
                    title = "Local-only Mode",
                    subtitle = if (uiState.isLocalOnly) "Vault stored locally only" else "Sync enabled",
                    trailing = {
                        Switch(
                            checked = uiState.isLocalOnly,
                            onCheckedChange = { viewModel.setLocalOnly(it) }
                        )
                    }
                )

                if (!uiState.isLocalOnly) {
                    SettingsItem(
                        icon = Icons.Default.Sync,
                        title = "Server URL",
                        subtitle = uiState.serverUrl ?: "Not configured",
                        onClick = { }
                    )

                    SettingsItem(
                        icon = Icons.Default.Sync,
                        title = "Last Synced",
                        subtitle = if (uiState.lastSyncTime > 0) {
                            "Just now"
                        } else {
                            "Never"
                        },
                        onClick = { viewModel.syncNow() }
                    )
                }
            }

            SettingsSection(title = "Data") {
                SettingsItem(
                    icon = Icons.Default.Upload,
                    title = "Import",
                    subtitle = "Import from other password managers",
                    onClick = { }
                )

                SettingsItem(
                    icon = Icons.Default.Download,
                    title = "Export",
                    subtitle = "Export vault to file",
                    onClick = { }
                )
            }

            SettingsSection(title = "Danger Zone") {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Delete Vault",
                    subtitle = "Permanently delete all data",
                    onClick = { showDeleteDialog = true },
                    dangerous = true
                )
            }

            SettingsSection(title = "Account") {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Lock Vault",
                    subtitle = "Lock and return to login",
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("Theme") },
            text = {
                Column {
                    listOf("system", "light", "dark", "amoled").forEach { theme ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setTheme(theme)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = uiState.themeMode == theme,
                                onClick = {
                                    viewModel.setTheme(theme)
                                    showThemeDialog = false
                                }
                            )
                            Text(
                                text = theme.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Lock Vault") },
            text = { Text("Are you sure you want to lock your vault?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.lockVault()
                    showLogoutDialog = false
                    onNavigateToLogin()
                }) {
                    Text("Lock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Vault") },
            text = { Text("This will permanently delete all your data. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteVault()
                    showDeleteDialog = false
                    onNavigateToLogin()
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
        Divider()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dangerous: Boolean = false
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                color = if (dangerous) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                color = if (dangerous) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (dangerous) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = trailing,
        modifier = Modifier.clickable(enabled = onClick != null) { onClick?.invoke() }
    )
}
