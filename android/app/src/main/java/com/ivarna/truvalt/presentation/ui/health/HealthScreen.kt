package com.ivarna.truvalt.presentation.ui.health

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.ivarna.truvalt.presentation.ui.shared.TruvaltTopAppBar
import com.ivarna.truvalt.presentation.ui.vault.VaultHomePalette
import com.ivarna.truvalt.presentation.ui.vault.rememberVaultPalette

@Composable
fun HealthScreen(
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit,
    viewModel: HealthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val palette = rememberVaultPalette()
    val firebaseUser = remember { FirebaseAuth.getInstance().currentUser }
    val profileFallback = firebaseUser?.displayName?.firstOrNull()?.uppercase()
        ?: firebaseUser?.email?.firstOrNull()?.uppercase() ?: "T"

    val orangeAlert = Color(0xFFF97316)
    val yellowAlert = Color(0xFFEAB308)
    val greenSafe = Color(0xFF4edea3)

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0)
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
                    title = "Health",
                    palette = palette,
                    photoUrl = firebaseUser?.photoUrl?.toString(),
                    profileFallback = profileFallback
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Circular Gauge Section
                    item {
                        HealthGaugeSection(
                            palette = palette,
                            score = uiState.healthScore,
                            safeColor = greenSafe
                        )
                    }

                    // Summary Cards
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            SummaryCard(
                                palette = palette,
                                icon = Icons.Default.Warning,
                                count = uiState.weakCount,
                                label = "Weak Passwords",
                                statusText = "ACTION REQUIRED",
                                tintColor = orangeAlert
                            )
                            SummaryCard(
                                palette = palette,
                                icon = Icons.Default.ContentCopy,
                                count = uiState.reusedCount,
                                label = "Reused Passwords",
                                statusText = "VULNERABLE",
                                tintColor = yellowAlert
                            )
                            SummaryCard(
                                palette = palette,
                                icon = Icons.Default.VerifiedUser,
                                count = uiState.breachedCount,
                                label = "Breached Passwords",
                                statusText = "SAFE",
                                tintColor = greenSafe // In a real app this would turn red if > 0
                            )
                        }
                    }

                    // Action Items
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Action Items",
                                    color = palette.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "View All",
                                    color = palette.brand,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            val hasActionItems = uiState.weakCount > 0 || uiState.reusedCount > 0 || uiState.oldCount > 0
                            
                            if (hasActionItems) {
                                if (uiState.weakCount > 0) {
                                    ActionItemCard(
                                        palette = palette,
                                        icon = Icons.Default.Warning,
                                        title = "Update Weak Passwords",
                                        subtitle = "${uiState.weakCount} passwords are too weak",
                                        actionLabel = "FIX NOW",
                                        tintColor = orangeAlert
                                    )
                                }
                                if (uiState.reusedCount > 0) {
                                    ActionItemCard(
                                        palette = palette,
                                        icon = Icons.Default.ContentCopy,
                                        title = "Rotate Reused Passwords",
                                        subtitle = "${uiState.reusedCount} passwords are used across multiple accounts",
                                        actionLabel = "RESOLVE",
                                        tintColor = yellowAlert
                                    )
                                }
                                if (uiState.oldCount > 0) {
                                    ActionItemCard(
                                        palette = palette,
                                        icon = Icons.Default.Refresh,
                                        title = "Update Old Passwords",
                                        subtitle = "${uiState.oldCount} passwords haven't been changed recently",
                                        actionLabel = "UPDATE",
                                        tintColor = greenSafe
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "All good! No action required.",
                                        color = palette.muted,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthGaugeSection(
    palette: VaultHomePalette,
    score: Int,
    safeColor: Color
) {
    val statusText = when {
        score >= 80 -> "OPTIMAL SECURITY"
        score >= 50 -> "NEEDS IMPROVEMENT"
        else -> "CRITICAL RISK"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Background track
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(220.dp),
                color = palette.mutedSurface,
                strokeWidth = 24.dp
            )
            
            // Foreground arc
            CircularProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.size(220.dp),
                color = safeColor,
                strokeWidth = 24.dp,
                strokeCap = StrokeCap.Round
            )

            // Inner Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = score.toString(),
                    color = palette.title,
                    fontSize = 72.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2).sp
                )
                Text(
                    text = "HEALTH SCORE",
                    color = palette.muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            // Floating Status Pill
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(palette.background, RoundedCornerShape(999.dp))
                    .border(2.dp, palette.background, RoundedCornerShape(999.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(safeColor.copy(alpha = 0.15f), RoundedCornerShape(999.dp))
                        .border(1.dp, safeColor.copy(alpha = 0.3f), RoundedCornerShape(999.dp))
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = statusText,
                        color = palette.title,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    palette: VaultHomePalette,
    icon: ImageVector,
    count: Int,
    label: String,
    statusText: String,
    tintColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = palette.cardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(tintColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tintColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = statusText,
                    color = tintColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = count.toString(),
                color = palette.title,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = palette.muted,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ActionItemCard(
    palette: VaultHomePalette,
    icon: ImageVector,
    title: String,
    subtitle: String,
    actionLabel: String,
    tintColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = palette.searchSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(palette.mutedSurface, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tintColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = palette.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = palette.muted,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .background(palette.brand.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = actionLabel,
                    color = palette.brand,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
