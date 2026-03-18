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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
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
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Truvalt",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = !isSearchActive }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        IconButton(onClick = { /* TODO: Filter bottom sheet */ }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Settings") },
                                leadingIcon = { Icon(Icons.Default.Settings, null) },
                                onClick = {
                                    onNavigateToSettings()
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Import") },
                                leadingIcon = { Icon(Icons.Default.Upload, null) },
                                onClick = {
                                    // TODO: Navigate to import
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export") },
                                leadingIcon = { Icon(Icons.Default.Download, null) },
                                onClick = {
                                    // TODO: Navigate to export
                                    showMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Lock Vault") },
                                leadingIcon = { Icon(Icons.Default.Lock, null) },
                                onClick = {
                                    onLockVault()
                                    showMenu = false
                                }
                            )
                        }
                    }
                )
                
                // Search bar
                AnimatedVisibility(visible = isSearchActive) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.setSearchQuery(it)
                        },
                        onSearch = { viewModel.setSearchQuery(searchQuery) },
                        active = false,
                        onActiveChange = {},
                        placeholder = { Text("Search vault…") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    viewModel.setSearchQuery("")
                                }) {
                                    Icon(Icons.Default.Close, "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {}
                }
                
                // Filter chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedTypeFilter == null,
                            onClick = {
                                selectedTypeFilter = null
                                viewModel.setFilter(null)
                            },
                            label = { Text("All") }
                        )
                    }
                    items(VaultItemType.getAllTypes()) { type ->
                        FilterChip(
                            selected = selectedTypeFilter == type,
                            onClick = {
                                selectedTypeFilter = type
                                viewModel.setFilter(type.id)
                            },
                            label = { Text(getTypeLabel(type)) },
                            leadingIcon = if (selectedTypeFilter == type) {
                                { Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddItemSheet = true },
                icon = { Icon(Icons.Rounded.Add, contentDescription = "Add item") },
                text = { Text("New Item") },
                expanded = !listState.isScrollInProgress
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
        is VaultItemType.Login -> "🔑 Logins"
        is VaultItemType.Passkey -> "🔐 Passkeys"
        is VaultItemType.Passphrase -> "💬 Passphrases"
        is VaultItemType.SecureNote -> "📝 Notes"
        is VaultItemType.SecurityCode -> "🛡 Security Codes"
        is VaultItemType.CreditCard -> "💳 Cards"
        is VaultItemType.Identity -> "👤 Identities"
        is VaultItemType.Custom -> "⚙️ Custom"
    }
}

data class VaultItemUi(
    val id: String,
    val name: String,
    val type: String,
    val subtitle: String = "",
    val isFavorite: Boolean = false
)
