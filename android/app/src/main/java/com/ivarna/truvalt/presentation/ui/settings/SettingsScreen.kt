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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSecuritySettings: () -> Unit = {},
    onNavigateToPinSetup: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val deleteAccountState by viewModel.deleteAccountState.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLockDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var showDeleteAccountStep1 by remember { mutableStateOf(false) }
    var showDeleteAccountStep2 by remember { mutableStateOf(false) }
    var deleteConfirmText by remember { mutableStateOf("") }

    // Navigate away on successful account deletion
    LaunchedEffect(deleteAccountState) {
        if (deleteAccountState is DeleteAccountState.Success) {
            viewModel.resetDeleteAccountState()
            onNavigateToLogin()
        }
    }

    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }

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
            // Account Card
            uiState.accountProfile?.let { account ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .clickable { showAccountDialog = true },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(0.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile avatar
                        if (!account.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = account.photoUrl,
                                contentDescription = "${account.displayName} photo",
                                modifier = Modifier.size(56.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = account.displayName.firstOrNull()?.uppercase() ?: "T",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                account.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                account.email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Row(
                                modifier = Modifier.padding(top = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (account.emailVerified) Icons.Default.Verified else Icons.Default.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp),
                                    tint = if (account.emailVerified) MaterialTheme.colorScheme.primary
                                           else MaterialTheme.colorScheme.outline
                                )
                                Text(
                                    account.providerLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Security Section
            SettingsSectionCard(title = "Security") {
                SettingsRowItem(
                    icon = Icons.Default.Security,
                    title = "Security Settings",
                    subtitle = "Biometric, PIN, and lock settings",
                    onClick = onNavigateToSecuritySettings,
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

            // Sync Section
            SettingsSectionCard(title = "Sync") {
                SettingsRowItem(
                    icon = if (uiState.isLocalOnly) Icons.Default.CloudOff else Icons.Default.Cloud,
                    title = "Local-only Mode",
                    subtitle = if (uiState.isLocalOnly) "Vault stored locally only" else "Sync enabled",
                    trailing = {
                        Switch(
                            checked = uiState.isLocalOnly,
                            onCheckedChange = { viewModel.setLocalOnly(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    isLast = uiState.isLocalOnly
                )
                if (!uiState.isLocalOnly) {
                    SettingsRowItem(
                        icon = Icons.Default.Sync,
                        title = "Server URL",
                        subtitle = uiState.serverUrl ?: "Not configured",
                        onClick = {}
                    )
                    SettingsRowItem(
                        icon = Icons.Default.Sync,
                        title = "Last Synced",
                        subtitle = if (uiState.lastSyncTime > 0) "Just now" else "Never",
                        onClick = { viewModel.syncNow() },
                        isLast = true
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Data Section
            SettingsSectionCard(title = "Data") {
                SettingsRowItem(
                    icon = Icons.Default.Upload,
                    title = "Import",
                    subtitle = "Import from other password managers",
                    onClick = {}
                )
                SettingsRowItem(
                    icon = Icons.Default.Download,
                    title = "Export",
                    subtitle = "Export vault to file",
                    onClick = {},
                    isLast = true
                )
            }

            Spacer(Modifier.height(16.dp))

            // Account Section
            SettingsSectionCard(title = "Account") {
                SettingsRowItem(
                    icon = Icons.Default.Lock,
                    title = "Lock Vault",
                    subtitle = "Lock and return to login",
                    onClick = { showLockDialog = true }
                )
                SettingsRowItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Sign Out",
                    subtitle = "Sign out of your account on this device",
                    onClick = { showLogoutDialog = true },
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
                    isLast = uiState.isLocalOnly
                )
                // Delete Account — only visible in cloud mode
                if (!uiState.isLocalOnly && uiState.accountProfile != null) {
                    SettingsRowItem(
                        icon = Icons.Default.PersonRemove,
                        title = "Delete Account",
                        subtitle = "Permanently delete account and all cloud data",
                        onClick = { showDeleteAccountStep1 = true },
                        dangerous = true,
                        isLast = true
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
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
                    onNavigateToLogin()
                }) { Text("Lock") }
            },
            dismissButton = {
                TextButton(onClick = { showLockDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = { Text("Sign Out", style = MaterialTheme.typography.headlineSmall) },
            text = { Text("Are you sure you want to sign out of your account on this device?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout()
                    showLogoutDialog = false
                    onNavigateToLogin()
                }) { Text("Sign Out") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
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
                    onNavigateToLogin()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showAccountDialog) {
        uiState.accountProfile?.let { account ->
            AlertDialog(
                onDismissRequest = { showAccountDialog = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(28.dp),
                title = { Text("Signed-in Account", style = MaterialTheme.typography.headlineSmall) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        AccountDetailRow(label = "Name", value = account.displayName)
                        AccountDetailRow(label = "Email", value = account.email)
                        AccountDetailRow(label = "Provider", value = account.providerLabel)
                        AccountDetailRow(
                            label = "Email verified",
                            value = if (account.emailVerified) "Yes" else "No"
                        )
                        AccountDetailRow(label = "User ID", value = account.uid)
                        account.createdAt?.let { AccountDetailRow(label = "Created", value = it) }
                        account.lastSignInAt?.let { AccountDetailRow(label = "Last sign-in", value = it) }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAccountDialog = false }) { Text("Close") }
                }
            )
        }
    }

    // ── Delete Account Step 1: Warning ────────────────────────────────────────
    if (showDeleteAccountStep1) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountStep1 = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Delete Account?",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "This will permanently delete:",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    listOf(
                        "All vault items (passwords, notes, cards)",
                        "All folders and tags",
                        "Your Firebase account and credentials",
                        "All cloud-synced data"
                    ).forEach { item ->
                        Text(
                            text = "• $item",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        "\nThis action is irreversible and cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteAccountStep1 = false
                    deleteConfirmText = ""
                    showDeleteAccountStep2 = true
                }) {
                    Text("Continue", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountStep1 = false }) { Text("Cancel") }
            }
        )
    }

    // ── Delete Account Step 2: Type DELETE confirmation ───────────────────────
    if (showDeleteAccountStep2) {
        AlertDialog(
            onDismissRequest = {
                showDeleteAccountStep2 = false
                deleteConfirmText = ""
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = {
                Text(
                    "Confirm Deletion",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Type DELETE to confirm account deletion:",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = deleteConfirmText,
                        onValueChange = { deleteConfirmText = it },
                        placeholder = { Text("DELETE") },
                        singleLine = true,
                        isError = deleteConfirmText.isNotEmpty() && deleteConfirmText != "DELETE",
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (deleteAccountState is DeleteAccountState.Loading) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountStep2 = false
                        viewModel.deleteAccount()
                    },
                    enabled = deleteConfirmText == "DELETE" && deleteAccountState !is DeleteAccountState.Loading
                ) {
                    Text(
                        "Delete My Account",
                        color = if (deleteConfirmText == "DELETE") MaterialTheme.colorScheme.error
                               else MaterialTheme.colorScheme.outline
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteAccountStep2 = false
                    deleteConfirmText = ""
                }) { Text("Cancel") }
            }
        )
    }

    // ── Delete Account Error Dialog ───────────────────────────────────────────
    if (deleteAccountState is DeleteAccountState.Error) {
        val errorMsg = (deleteAccountState as DeleteAccountState.Error).message
        AlertDialog(
            onDismissRequest = { viewModel.resetDeleteAccountState() },
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = RoundedCornerShape(28.dp),
            title = { Text("Deletion Failed", style = MaterialTheme.typography.headlineSmall) },
            text = {
                Text(
                    errorMsg,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.resetDeleteAccountState() }) { Text("OK") }
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

// ─── Account Detail Row ───────────────────────────────────────────────────────

@Composable
private fun AccountDetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
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
