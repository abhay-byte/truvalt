package com.ivarna.truvalt.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.ivarna.truvalt.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSecuritySettings: () -> Unit = {},
    onNavigateToPinSetup: () -> Unit = {},
    onNavigateToImport: () -> Unit = {},
    onNavigateToExport: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val autofillEnabled by rememberTruvaltAutofillEnabled()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLockDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            // Security Section
            SettingsSectionCard(title = "Security") {
                SettingsRowItem(
                    icon = Icons.Default.Security,
                    title = "Security Settings",
                    subtitle = if (autofillEnabled) {
                        "Biometric, PIN, autofill, and lock settings"
                    } else {
                        "Biometric, PIN, and lock settings"
                    },
                    onClick = onNavigateToSecuritySettings,
                    isLast = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Autofill Section
            SettingsSectionCard(title = "Autofill") {
                SettingsRowItem(
                    icon = Icons.Default.Password,
                    title = "Autofill Service",
                    subtitle = if (autofillEnabled) {
                        "Enabled for apps and websites"
                    } else {
                        "Turn on Truvalt as your autofill service"
                    },
                    trailing = {
                        TextButton(onClick = { openTruvaltAutofillSettings(context) }) {
                            Text(if (autofillEnabled) "Enabled" else "Enable")
                        }
                    },
                    onClick = { openTruvaltAutofillSettings(context) },
                    isLast = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Appearance Section
            SettingsSectionCard(title = "Appearance") {
                SettingsRowItem(
                    icon = Icons.Default.Brightness4,
                    title = "Theme",
                    subtitle = uiState.themeMode.replaceFirstChar { it.uppercase() },
                    onClick = { showThemeDialog = true }
                )
                SettingsRowItem(
                    icon = Icons.Default.Timer,
                    title = "Clipboard Timeout",
                    subtitle = "${uiState.clipboardTimeout} seconds",
                    onClick = {},
                    isLast = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Data Section
            SettingsSectionCard(title = "Data") {
                SettingsRowItem(
                    icon = Icons.Default.Upload,
                    title = "Import",
                    subtitle = "Import from other password managers",
                    onClick = onNavigateToImport
                )
                SettingsRowItem(
                    icon = Icons.Default.Download,
                    title = "Export",
                    subtitle = "Export vault to file",
                    onClick = onNavigateToExport,
                    isLast = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Account Section
            SettingsSectionCard(title = "Account") {
                SettingsRowItem(
                    icon = Icons.Default.Lock,
                    title = "Lock Vault",
                    subtitle = "Lock and return to unlock",
                    onClick = { showLockDialog = true },
                    isLast = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Danger Zone
            SettingsSectionCard(title = "Danger Zone") {
                SettingsRowItem(
                    icon = Icons.Default.Delete,
                    title = "Delete Vault",
                    subtitle = "Permanently delete all local data",
                    onClick = { showDeleteDialog = true },
                    dangerous = true,
                    isLast = true
                )
            }

            Spacer(Modifier.height(32.dp))

            // App version
            Text(
                text = "Truvalt v${context.getString(R.string.app_version)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                    .padding(horizontal = 28.dp, vertical = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))
        }
    }

    // --- Dialogs ---

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text("Theme", style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface)
            },
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
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showLockDialog) {
        AlertDialog(
            onDismissRequest = { showLockDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = { Text("Lock Vault", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Are you sure you want to lock your vault?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.lockVault()
                    showLockDialog = false
                    onNavigateBack()
                }) { Text("Lock") }
            },
            dismissButton = {
                TextButton(onClick = { showLockDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = { Text("Delete Vault", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("This will permanently delete all your data. This action cannot be undone.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteVault()
                    showDeleteDialog = false
                    onNavigateBack()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ─── Section Card ─────────────────────────────────────────────────────────────

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = androidx.compose.ui.unit.TextUnit(
                0.1f, androidx.compose.ui.unit.TextUnitType.Em
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

// ─── Row Item ─────────────────────────────────────────────────────────────────

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dangerous: Boolean = false,
    isLast: Boolean = false
) {
    val contentColor = if (dangerous) MaterialTheme.colorScheme.error
                       else MaterialTheme.colorScheme.onSurface
    val subtitleColor = if (dangerous) MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant
    val iconTint = if (dangerous) MaterialTheme.colorScheme.error
                   else MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (dangerous) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                            else MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconTint
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = contentColor)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = subtitleColor)
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null && !dangerous) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Legacy helpers kept for any external callsites ──────────────────────────

@Composable
fun SettingsSection(
    title: String,
    palette: com.ivarna.truvalt.presentation.ui.vault.VaultHomePalette,
    content: @Composable () -> Unit
) {
    SettingsSectionCard(title = title, content = content)
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    palette: com.ivarna.truvalt.presentation.ui.vault.VaultHomePalette,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dangerous: Boolean = false
) {
    SettingsRowItem(
        icon = icon,
        title = title,
        subtitle = subtitle,
        onClick = onClick,
        trailing = trailing,
        dangerous = dangerous
    )
}
