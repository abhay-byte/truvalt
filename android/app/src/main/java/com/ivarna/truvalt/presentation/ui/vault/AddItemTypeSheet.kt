package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivarna.truvalt.domain.model.VaultItemType

private data class ItemTypeOption(
    val type: VaultItemType,
    val icon: ImageVector,
    val label: String,
    val subtitle: String
)

private val itemTypeOptions = listOf(
    ItemTypeOption(VaultItemType.Login, Icons.Default.Key, "Login", "Username & password"),
    ItemTypeOption(VaultItemType.Passkey, Icons.Default.Fingerprint, "Passkey", "Biometric credential"),
    ItemTypeOption(VaultItemType.Passphrase, Icons.Default.ChatBubble, "Passphrase", "Memorable phrase"),
    ItemTypeOption(VaultItemType.SecureNote, Icons.Default.StickyNote2, "Secure Note", "Encrypted text"),
    ItemTypeOption(VaultItemType.SecurityCode, Icons.Default.Shield, "Security Code", "Recovery codes"),
    ItemTypeOption(VaultItemType.CreditCard, Icons.Default.CreditCard, "Credit Card", "Card details"),
    ItemTypeOption(VaultItemType.Identity, Icons.Default.Person, "Identity", "Personal info"),
    ItemTypeOption(VaultItemType.Login, Icons.Default.Pin, "2FA / TOTP", "Time-based OTP"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemTypeSheet(
    onTypeSelected: (VaultItemType) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Text(
            text = "What would you like to add?",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            items(itemTypeOptions) { option ->
                ElevatedCard(
                    onClick = { onTypeSelected(option.type) },
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(option.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(
                            option.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
