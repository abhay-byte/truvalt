package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.R

private data class VaultFilterOption(
    val id: String?,
    val label: String
)

@Composable
fun VaultHomeScreen(
    onNavigateToItemDetail: (String) -> Unit,
    onNavigateToItemCreate: (String?) -> Unit,
    onNavigateToTypeSelection: () -> Unit = {},
    onNavigateToGenerator: () -> Unit,
    onNavigateToHealth: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTrash: () -> Unit,
    onLockVault: () -> Unit = {},
    viewModel: VaultViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val palette = rememberVaultPalette()
    val filterOptions = listOf(
        VaultFilterOption(id = null, label = "All"),
        VaultFilterOption(id = "favorites", label = "Favorites"),
        VaultFilterOption(id = "login", label = "Logins"),
        VaultFilterOption(id = "secure_note", label = "Notes"),
        VaultFilterOption(id = "credit_card", label = "Cards")
    )

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            GradientAddButton(
                palette = palette,
                onClick = { onNavigateToTypeSelection() }
            )
        }
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
            Box(
                modifier = Modifier
                    .size(320.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 36.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(palette.heroGlow, Color.Transparent)
                        ),
                        shape = CircleShape
                    )
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    VaultHeader(palette = palette)
                }

                item {
                    VaultSearchField(
                        value = uiState.searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        palette = palette
                    )
                }

                item {
                    FilterChipRow(
                        palette = palette,
                        selectedFilter = uiState.filter,
                        options = filterOptions,
                        onSelect = viewModel::setFilter
                    )
                }

                item {
                    SectionHeader(
                        palette = palette,
                        title = "Recent Access",
                        count = uiState.items.size
                    )
                }

                if (uiState.isLoading) {
                    item {
                        LoadingCard(palette = palette)
                    }
                } else if (uiState.items.isEmpty()) {
                    item {
                        EmptyVaultState(
                            palette = palette,
                            hasFilter = uiState.filter != null || uiState.searchQuery.isNotBlank(),
                            onAddItem = onNavigateToTypeSelection,
                            onClearFilters = {
                                viewModel.setFilter(null)
                                viewModel.setSearchQuery("")
                            }
                        )
                    }
                } else {
                    items(uiState.items, key = { it.id }) { item ->
                        VaultItemCard(
                            item = item,
                            onClick = { onNavigateToItemDetail(item.id) },
                            onCopy = { }
                        )
                    }
                }

                item {
                    HealthSummaryCard(
                        palette = palette,
                        summary = uiState.health,
                        onClick = onNavigateToHealth
                    )
                }
            }
        }
    }
}

@Composable
private fun VaultHeader(palette: VaultHomePalette) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = palette.headerSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.mutedSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.truvalt_icon),
                        contentDescription = "Truvalt",
                        modifier = Modifier.size(30.dp)
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "TRUVALT",
                        color = palette.brand,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = "Your secure vault, beautifully organized",
                        color = palette.muted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            IconButton(
                onClick = { },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(palette.mutedSurface)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync",
                    tint = palette.brand
                )
            }
        }
    }
}

@Composable
private fun VaultSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    palette: VaultHomePalette
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        placeholder = {
            Text(
                text = "Search your vault...",
                color = palette.muted
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = palette.muted
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = palette.searchSurface,
            unfocusedContainerColor = palette.searchSurface,
            disabledContainerColor = palette.searchSurface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            cursorColor = palette.brand,
            focusedTextColor = palette.title,
            unfocusedTextColor = palette.title
        )
    )
}

@Composable
private fun FilterChipRow(
    palette: VaultHomePalette,
    selectedFilter: String?,
    options: List<VaultFilterOption>,
    onSelect: (String?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(options) { option ->
            val selected = option.id == selectedFilter || (option.id == null && selectedFilter == null)
            Surface(
                modifier = Modifier.clickable { onSelect(option.id) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) palette.chipSelectedSurface else palette.chipIdleSurface
            ) {
                Text(
                    text = option.label,
                    color = if (selected) palette.chipSelectedText else palette.chipIdleText,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 11.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    palette: VaultHomePalette,
    title: String,
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            color = palette.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$count ITEMS",
            color = palette.muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun HealthSummaryCard(
    palette: VaultHomePalette,
    summary: VaultHealthSummary,
    onClick: () -> Unit
) {
    val summaryText = when {
        summary.analyzedCount == 0 -> "Add login items to unlock live password health insights."
        summary.reusedCount > 0 && summary.weakCount > 0 ->
            "${summary.weakCount} weak and ${summary.reusedCount} reused passwords found."
        summary.reusedCount > 0 -> "${summary.reusedCount} reused password groups found."
        summary.weakCount > 0 -> "${summary.weakCount} weak passwords need attention."
        summary.oldCount > 0 -> "${summary.oldCount} passwords are older than 180 days."
        else -> "No weak or reused passwords detected in your vault."
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        color = palette.cardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Health Score",
                        color = palette.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = summaryText,
                        color = palette.body,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.size(20.dp))
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { summary.score / 100f },
                        modifier = Modifier.size(82.dp),
                        color = palette.healthAccent,
                        trackColor = palette.healthRing,
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = summary.score.toString(),
                        color = palette.healthAccent,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            HorizontalDivider(color = palette.cardBorder)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "${summary.secureCount} secure of ${summary.analyzedCount} logins analyzed",
                    color = palette.muted,
                    style = MaterialTheme.typography.bodySmall
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(palette.healthTrack)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(summary.score / 100f)
                            .height(10.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(palette.healthAccent, palette.brandStrong)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientAddButton(
    palette: VaultHomePalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(palette.fabGradientStart, palette.fabGradientEnd)
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add item",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun LoadingCard(palette: VaultHomePalette) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = palette.cardSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = palette.brand,
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.size(14.dp))
            Text(
                text = "Loading your vault...",
                color = palette.muted
            )
        }
    }
}

@Composable
fun EmptyVaultState(
    palette: VaultHomePalette,
    hasFilter: Boolean,
    onAddItem: () -> Unit,
    onClearFilters: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = palette.cardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(palette.mutedSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (hasFilter) Icons.Default.SearchOff else Icons.Default.Lock,
                    contentDescription = null,
                    tint = palette.brand,
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                text = if (hasFilter) "No matching entries" else "Your vault is waiting",
                color = palette.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (hasFilter) {
                    "Try a different search or chip filter to surface the right item."
                } else {
                    "Start adding logins, notes, and cards to make this space your secure command center."
                },
                color = palette.body,
                style = MaterialTheme.typography.bodyLarge
            )

            if (hasFilter) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear filters", color = palette.brand)
                }
            } else {
                Button(onClick = onAddItem) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Add your first item")
                }
            }
        }
    }
}

data class VaultItemUi(
    val id: String,
    val name: String,
    val type: String,
    val typeLabel: String,
    val subtitle: String = "",
    val isFavorite: Boolean = false
)
