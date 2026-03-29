package com.ivarna.truvalt.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.ivarna.truvalt.presentation.ui.shared.TruvaltTopAppBar
import com.ivarna.truvalt.presentation.ui.vault.VaultHomePalette
import com.ivarna.truvalt.presentation.ui.vault.rememberVaultPalette

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
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLockDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    val palette = rememberVaultPalette()
    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }
    val profileFallback = firebaseUser?.displayName?.firstOrNull()?.uppercase()
        ?: firebaseUser?.email?.firstOrNull()?.uppercase() ?: "T"

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(palette.background, palette.backgroundAccent)
                    )
                )
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                TruvaltTopAppBar(
                    title = "Settings",
                    palette = palette,
                    photoUrl = firebaseUser?.photoUrl?.toString(),
                    profileFallback = profileFallback
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
            uiState.accountProfile?.let { account ->
                SignedInAccountCard(
                    profile = account,
                    palette = palette,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    onClick = { showAccountDialog = true }
                )
            }

            SettingsSection(title = "Security", palette = palette) {
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Security Settings",
                    subtitle = "Biometric, PIN, and lock settings",
                    palette = palette,
                    onClick = onNavigateToSecuritySettings
                )

                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "Auto-lock",
                    subtitle = uiState.autoLockLabel,
                    palette = palette,
                    onClick = { }
                )

                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = "Clipboard Timeout",
                    subtitle = "${uiState.clipboardTimeout} seconds",
                    palette = palette,
                    onClick = { }
                )
            }

            SettingsSection(title = "Appearance", palette = palette) {
                SettingsItem(
                    icon = Icons.Default.Brightness4,
                    title = "Theme",
                    subtitle = uiState.themeMode.replaceFirstChar { it.uppercase() },
                    palette = palette,
                    onClick = { showThemeDialog = true }
                )
            }

            SettingsSection(title = "Sync", palette = palette) {
                SettingsItem(
                    icon = if (uiState.isLocalOnly) Icons.Default.CloudOff else Icons.Default.Cloud,
                    title = "Local-only Mode",
                    subtitle = if (uiState.isLocalOnly) "Vault stored locally only" else "Sync enabled",
                    palette = palette,
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
                        palette = palette,
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
                        palette = palette,
                        onClick = { viewModel.syncNow() }
                    )
                }
            }

            SettingsSection(title = "Data", palette = palette) {
                SettingsItem(
                    icon = Icons.Default.Upload,
                    title = "Import",
                    subtitle = "Import from other password managers",
                    palette = palette,
                    onClick = { }
                )

                SettingsItem(
                    icon = Icons.Default.Download,
                    title = "Export",
                    subtitle = "Export vault to file",
                    palette = palette,
                    onClick = { }
                )
            }

            SettingsSection(title = "Danger Zone", palette = palette) {
                SettingsItem(
                    icon = Icons.Default.Delete,
                    title = "Delete Vault",
                    subtitle = "Permanently delete all data",
                    onClick = { showDeleteDialog = true },
                    dangerous = true,
                    palette = palette
                )
            }

            SettingsSection(title = "Account", palette = palette) {
                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Lock Vault",
                    subtitle = "Lock and return to login",
                    palette = palette,
                    onClick = { showLockDialog = true }
                )

                SettingsItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Sign Out",
                    subtitle = "Sign out from Firebase on this device",
                    palette = palette,
                    onClick = { showLogoutDialog = true }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
        }
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

    if (showLockDialog) {
        AlertDialog(
            onDismissRequest = { showLockDialog = false },
            title = { Text("Lock Vault") },
            text = { Text("Are you sure you want to lock your vault?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.lockVault()
                    showLockDialog = false
                    onNavigateToLogin()
                }) {
                    Text("Lock")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLockDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out of your Firebase account on this device?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout()
                    showLogoutDialog = false
                    onNavigateToLogin()
                }) {
                    Text("Sign Out")
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

    if (showAccountDialog) {
        uiState.accountProfile?.let { account ->
            AlertDialog(
                onDismissRequest = { showAccountDialog = false },
                title = { Text("Signed-in Account") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                    TextButton(onClick = { showAccountDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
private fun SignedInAccountCard(
    profile: AccountProfileUiState,
    palette: VaultHomePalette,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = palette.cardSurface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(profile = profile)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(
                    text = profile.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.title,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = profile.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.muted
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (profile.emailVerified) Icons.Default.Verified else Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (profile.emailVerified) palette.brand else palette.muted
                    )
                    Text(
                        text = profile.providerLabel,
                        style = MaterialTheme.typography.bodySmall,
                        color = palette.muted,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = palette.muted
            )
        }
    }
}

@Composable
private fun ProfileAvatar(profile: AccountProfileUiState) {
    val fallbackLetter = profile.displayName.firstOrNull()?.uppercase() ?: "U"

    if (!profile.photoUrl.isNullOrBlank()) {
        AsyncImage(
            model = profile.photoUrl,
            contentDescription = "${profile.displayName} profile photo",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Row(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = fallbackLetter,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

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
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    palette: VaultHomePalette,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = palette.brand,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )
        content()
        HorizontalDivider(color = palette.cardBorder, modifier = Modifier.padding(horizontal = 24.dp))
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    palette: VaultHomePalette,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    dangerous: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (dangerous) MaterialTheme.colorScheme.error else palette.muted
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (dangerous) MaterialTheme.colorScheme.error else palette.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                color = if (dangerous) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else palette.muted,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (trailing != null) {
            trailing()
        }
    }
}
