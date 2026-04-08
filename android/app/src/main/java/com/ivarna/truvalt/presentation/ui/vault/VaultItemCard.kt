package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.truvalt.core.crypto.TotpGenerator

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
    val clipboardManager = LocalClipboardManager.current
    var totpCode by remember { mutableStateOf<String?>(null) }
    
    // Generate TOTP if seed exists
    if (!item.totpSeed.isNullOrBlank()) {
        totpCode = try {
            TotpGenerator.generate(item.totpSeed)
        } catch (e: Exception) { null }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = palette.cardSurface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
            
            // TOTP Code Display
            if (totpCode != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            palette.iconTileSurface.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = palette.brandStrong,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "2FA:",
                            color = palette.muted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${totpCode!!.substring(0, 3)} ${totpCode!!.substring(3)}",
                            color = palette.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    }
                    IconButton(
                        onClick = { clipboardManager.setText(AnnotatedString(totpCode!!)) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy 2FA",
                            tint = palette.brandStrong,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
