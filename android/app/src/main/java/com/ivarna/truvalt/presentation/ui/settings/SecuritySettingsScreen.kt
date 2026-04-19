package com.ivarna.truvalt.presentation.ui.settings

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.core.biometric.BiometricStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecuritySettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPinSetup: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val biometricStatus = remember { viewModel.biometricHelper.canAuthenticate() }
    val isPinEnabled = remember { viewModel.pinStorage.isEnabled() }
    val autofillEnabled by rememberTruvaltAutofillEnabled()
    var showAutoLockDialog by remember { mutableStateOf(false) }
    val biometricToggleEnabled =
        biometricStatus == BiometricStatus.AVAILABLE && uiState.canUseBiometricUnlock

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Security",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
            // Biometric Trigger Section (Hero element)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                // Biometric Trigger: A large, central surface-container-lowest circle with a subtle primary glow
                Box(
                    modifier = Modifier
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle primary glow (8% opacity)
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .blur(30.dp)
                    )

                    Surface(
                        onClick = { 
                            if (biometricToggleEnabled) {
                                viewModel.setBiometricEnabled(!uiState.isBiometricEnabled)
                            }
                        },
                        enabled = biometricToggleEnabled,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        shadowElevation = 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = if (uiState.isBiometricEnabled) MaterialTheme.colorScheme.primary 
                                       else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                            )
                        }
                    }
                    
                    // Status indicator dot
                    if (uiState.isBiometricEnabled) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(bottom = 20.dp, end = 20.dp)
                                .size(14.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                    }
                }
            }

            // Unlock Methods Section
            SecuritySectionCard(title = "Unlock Methods") {
                SecurityRowItem(
                    icon = Icons.Default.Password,
                    title = "Autofill",
                    subtitle = if (autofillEnabled) {
                        "Enabled for apps and websites"
                    } else {
                        "Select Truvalt in system autofill settings"
                    },
                    trailing = {
                        TextButton(onClick = { openTruvaltAutofillSettings(context) }) {
                            Text(if (autofillEnabled) "Enabled" else "Enable")
                        }
                    },
                    onClick = { openTruvaltAutofillSettings(context) }
                )
                SecurityRowItem(
                    icon = Icons.Default.Fingerprint,
                    title = "Biometric Unlock",
                    subtitle = when (biometricStatus) {
                        BiometricStatus.AVAILABLE -> {
                            if (uiState.canUseBiometricUnlock) "Use fingerprint or face"
                            else "Requires keystore-backed key"
                        }
                        BiometricStatus.NONE_ENROLLED -> "No biometrics enrolled"
                        BiometricStatus.UNAVAILABLE -> "Not available"
                    },
                    trailing = {
                        Switch(
                            checked = uiState.isBiometricEnabled,
                            onCheckedChange = { viewModel.setBiometricEnabled(it) },
                            enabled = biometricToggleEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                )
                SecurityRowItem(
                    icon = Icons.Default.Lock,
                    title = "PIN Lock",
                    subtitle = if (isPinEnabled) "PIN is enabled" else "Set a 4-8 digit PIN",
                    trailing = {
                        Switch(
                            checked = isPinEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) onNavigateToPinSetup()
                                else viewModel.pinStorage.clear()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    },
                    onClick = if (isPinEnabled) onNavigateToPinSetup else null,
                    isLast = true
                )
            }

            Spacer(Modifier.height(24.dp))

            // Auto-lock Section
            SecuritySectionCard(title = "Auto-lock") {
                SecurityRowItem(
                    icon = Icons.Default.Timer,
                    title = "Auto-lock Timeout",
                    subtitle = uiState.autoLockLabel,
                    onClick = { showAutoLockDialog = true },
                    isLast = true
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        if (showAutoLockDialog) {
            AlertDialog(
                onDismissRequest = { showAutoLockDialog = false },
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                shape = RoundedCornerShape(28.dp),
                title = { 
                    Text(
                        "Auto-lock Timeout",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                text = {
                    Column {
                        listOf(
                            0L to "Immediately",
                            60000L to "1 minute",
                            300000L to "5 minutes",
                            900000L to "15 minutes",
                            3600000L to "1 hour",
                            -1L to "Never"
                        ).forEach { (timeout, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.setAutoLockTimeout(timeout)
                                        showAutoLockDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = uiState.autoLockTimeout == timeout,
                                    onClick = {
                                        viewModel.setAutoLockTimeout(timeout)
                                        showAutoLockDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
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

@Composable
private fun SecuritySectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun SecurityRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    isLast: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(20.dp)
            )
        }
    }
    
    if (!isLast) {
        // Divider replaced by padding in editorial style, or very subtle color shift
        // But the cards here hold items, so we just let them stack if we had multiple cards.
        // Within a card, DESIGN.md says "Forbid Dividers".
        // Instead use white space or alternate background.
        // We'll stick to white space (padding).
    }
}
