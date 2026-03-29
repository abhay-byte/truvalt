package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.domain.model.VaultItemType

@OptIn(ExperimentalMaterial3Api::class)
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
    var showMenu by remember { mutableStateOf(false) }
    var showAddItemSheet by remember { mutableStateOf(false) }
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<VaultItemType?>(null) }
    val listState = rememberLazyListState()


    Scaffold(
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background) // surface
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = com.ivarna.truvalt.R.drawable.truvalt_icon),
                            contentDescription = "Truvalt Logo",
                            modifier = Modifier.size(28.dp),
                            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = "TRUVALT",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(onClick = { /* Sync action */ }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = MaterialTheme.colorScheme.onBackground)
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                // Editorial Header
                Text(
                    text = "My Vault",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground, // AuthOnSurface
                    letterSpacing = (-1).sp
                )
                Text(
                    text = "${uiState.items.size} secure entries found in your sanctuary.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // AuthOnSurfaceVariant
                )
                
                Spacer(Modifier.height(24.dp))
                
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        viewModel.setSearchQuery(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    placeholder = { Text("Search vault...", color = Color(0xFF7C7984)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF7C7984)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, // surface-container-highest
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true
                )
                
                Spacer(Modifier.height(16.dp))
                
                // Filter Chips List
                // We'll map the UI types to mimic the html
                val uiFilterTypes = listOf("All" to null, "Logins" to "login", "Passkeys" to "passkey", "Notes" to "secure_note", "Cards" to "credit_card")
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(uiFilterTypes.size) { index ->
                        val (label, filterId) = uiFilterTypes[index]
                        val isSelected = (selectedTypeFilter?.id == filterId) || (filterId == null && selectedTypeFilter == null)
                        
                        Surface(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, // primary vs surface-container-low
                            modifier = Modifier.clickable {
                                if (filterId == null) {
                                    selectedTypeFilter = null
                                    viewModel.setFilter(null)
                                } else {
                                    val type = VaultItemType.getAllTypes().find { it.id == filterId }
                                    selectedTypeFilter = type
                                    viewModel.setFilter(filterId)
                                }
                            }
                        ) {
                            Text(
                                text = label,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant, // on-primary vs on-surface-variant
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddItemSheet = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = "Add item", tint = MaterialTheme.colorScheme.onPrimary) },
                text = { Text("Add item", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary) },
                expanded = !listState.isScrollInProgress,
                containerColor = MaterialTheme.colorScheme.primary, // Primary
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            )
        }
    ) { padding ->
        if (uiState.items.isEmpty()) {
            EmptyVaultState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                hasFilter = selectedTypeFilter != null || searchQuery.isNotEmpty(),
                onAddItem = { showAddItemSheet = true },
                onClearFilters = {
                    selectedTypeFilter = null
                    searchQuery = ""
                    viewModel.setFilter(null)
                    viewModel.setSearchQuery("")
                }
            )
        } else {
            LazyColumn(
                state = listState,
                contentPadding = padding,
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "${uiState.items.size} items",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                items(uiState.items, key = { it.id }) { item ->
                    VaultItemCard(
                        item = item,
                        onClick = { onNavigateToItemDetail(item.id) },
                        onCopy = {
                            // TODO: Copy to clipboard
                        },
                        onMoreClick = {
                            // TODO: Show more menu
                        }
                    )
                }
            }
        }
    }
    
    // Add Item Type Sheet
    if (showAddItemSheet) {
        AddItemTypeSheet(
            onTypeSelected = { type ->
                showAddItemSheet = false
                onNavigateToItemCreate(type.id)
            },
            onDismiss = { showAddItemSheet = false }
        )
    }
}

@Composable
fun EmptyVaultState(
    modifier: Modifier = Modifier,
    hasFilter: Boolean = false,
    onAddItem: () -> Unit,
    onClearFilters: () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            if (hasFilter) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No results",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Try a different filter or search term",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onClearFilters) {
                    Text("Clear filters")
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your vault is empty",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap + to add your first item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onAddItem) {
                    Icon(Icons.Rounded.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add your first item")
                }
            }
        }
    }
}

fun getTypeLabel(type: VaultItemType): String {
    return when (type) {
        is VaultItemType.Login -> "Logins"
        is VaultItemType.Passkey -> "Passkeys"
        is VaultItemType.Passphrase -> "Passphrases"
        is VaultItemType.SecureNote -> "Notes"
        is VaultItemType.SecurityCode -> "Security Codes"
        is VaultItemType.CreditCard -> "Cards"
        is VaultItemType.Identity -> "Identities"
        is VaultItemType.Custom -> "Custom"
    }
}

data class VaultItemUi(
    val id: String,
    val name: String,
    val type: String,
    val subtitle: String = "",
    val isFavorite: Boolean = false
)
