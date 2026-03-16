package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun VaultItemDetailScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Item Detail: $itemId")
    }
}

@Composable
fun VaultItemEditScreen(
    itemId: String?,
    itemType: String?,
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(if (itemId != null) "Edit Item" else "Create Item")
    }
}

@Composable
fun TrashScreen(
    onNavigateBack: () -> Unit,
    onNavigateToItem: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Trash")
    }
}
