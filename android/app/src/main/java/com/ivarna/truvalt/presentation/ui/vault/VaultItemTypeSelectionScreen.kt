package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivarna.truvalt.domain.model.VaultItemType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultItemTypeSelectionScreen(
    onTypeSelected: (VaultItemType) -> Unit,
    onDismiss: () -> Unit
) {
    val types = VaultItemType.getAllTypes()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(types) { type ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onTypeSelected(type) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getIconForType(type),
                            contentDescription = type.displayName,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                type.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                getTypeDescription(type),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun getIconForType(type: VaultItemType): ImageVector {
    return when (type) {
        is VaultItemType.Login -> Icons.Default.VpnKey
        is VaultItemType.Passkey -> Icons.Default.PhoneAndroid
        is VaultItemType.Passphrase -> Icons.Default.TextFields
        is VaultItemType.SecureNote -> Icons.Default.Note
        is VaultItemType.SecurityCode -> Icons.Default.VerifiedUser
        is VaultItemType.CreditCard -> Icons.Default.CreditCard
        is VaultItemType.Identity -> Icons.Default.Person
        is VaultItemType.Custom -> Icons.Default.MoreHoriz
    }
}

fun getTypeDescription(type: VaultItemType): String = when (type) {
    is VaultItemType.Login -> "Username, password, and TOTP codes"
    is VaultItemType.Passkey -> "Passkey credential"
    is VaultItemType.Passphrase -> "Memorable phrase or sentence"
    is VaultItemType.SecureNote -> "Plain text secure note"
    is VaultItemType.SecurityCode -> "Recovery, backup, or security codes"
    is VaultItemType.CreditCard -> "Card number, CVV, expiry"
    is VaultItemType.Identity -> "Name, email, phone, address"
    is VaultItemType.Custom -> "Custom-defined item type"
}
