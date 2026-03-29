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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Add Item",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {
            items(types) { type ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable { onTypeSelected(type) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Jewel-like Icon Box
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .size(48.dp)
                                .androidx.compose.foundation.background(
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                                    shape = RoundedCornerShape(14.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getIconForType(type),
                                contentDescription = type.displayName,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                            Text(
                                type.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                getTypeDescription(type),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
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
