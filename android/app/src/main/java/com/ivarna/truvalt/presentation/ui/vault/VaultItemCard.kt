package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class ItemTypeStyle(
    val icon: ImageVector,
    val iconColor: Color
)

@Composable
private fun itemTypeStyle(type: String): ItemTypeStyle {
    val palette = rememberVaultPalette()
    return when (type) {
        "login" -> ItemTypeStyle(Icons.Default.AccountCircle, palette.brand)
        "passkey" -> ItemTypeStyle(Icons.Default.Key, palette.brandStrong)
        "passphrase" -> ItemTypeStyle(Icons.Default.VpnKey, palette.brandStrong)
        "secure_note" -> ItemTypeStyle(Icons.Default.Description, palette.healthAccent)
        "security_code" -> ItemTypeStyle(Icons.Default.Shield, palette.brandStrong)
        "credit_card" -> ItemTypeStyle(Icons.Default.CreditCard, palette.body)
        "identity" -> ItemTypeStyle(Icons.Default.Person, palette.brand)
        else -> ItemTypeStyle(Icons.Default.Cloud, palette.brand)
    }
}

@Composable
fun VaultItemCard(
    item: VaultItemUi,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = rememberVaultPalette()
    val typeStyle = itemTypeStyle(item.type)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = palette.cardSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(palette.iconTileSurface, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = typeStyle.icon,
                    contentDescription = null,
                    tint = typeStyle.iconColor,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        color = palette.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (item.isFavorite) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    palette.chipSelectedSurface.copy(alpha = 0.6f),
                                    RoundedCornerShape(999.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Fav",
                                color = palette.chipSelectedText,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
                Text(
                    text = item.subtitle.ifBlank { item.typeLabel },
                    color = palette.muted,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCopy) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = palette.body
                    )
                }
                    Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = palette.muted
                )
            }
        }
    }
}
