package com.ivarna.truvalt.presentation.ui.vault

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
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.foundation.clickable

private data class TypeStyle(val icon: ImageVector, val bgColor: Color, val fgColor: Color)

private fun getStyleForType(type: String): TypeStyle = when (type) {
    "login" -> TypeStyle(Icons.Default.Key, Color(0xFFE8F0FE), Color(0xFF1A73E8))
    "passkey" -> TypeStyle(Icons.Default.Fingerprint, Color(0xFFF1F5F9), Color(0xFF334155))
    "passphrase" -> TypeStyle(Icons.Default.ChatBubble, Color(0xFFF1F5F9), Color(0xFF334155))
    "secure_note" -> TypeStyle(Icons.Default.StickyNote2, Color(0xFFFFD2FC), Color(0xFF765377))
    "totp" -> TypeStyle(Icons.Default.Pin, Color(0xFFE8F0FE), Color(0xFF1A73E8))
    "security_code" -> TypeStyle(Icons.Default.Shield, Color(0xFFF3E8FF), Color(0xFF7E22CE)) // purple
    "credit_card" -> TypeStyle(Icons.Default.CreditCard, Color(0xFFFFF7ED), Color(0xFFEA580C))
    "identity" -> TypeStyle(Icons.Default.Person, Color(0xFFDCFCE7), Color(0xFF166534))
    else -> TypeStyle(Icons.Default.Key, Color(0xFFE8F0FE), Color(0xFF1A73E8))
}

@Composable
fun VaultItemCard(
    item: VaultItemUi,
    onClick: () -> Unit,
    onCopy: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val typeStyle = getStyleForType(item.type)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White // surface-container-lowest
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp), // p-5
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp), // w-12 h-12
                shape = RoundedCornerShape(12.dp),
                color = typeStyle.bgColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = typeStyle.icon,
                        contentDescription = null,
                        tint = typeStyle.fgColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name, 
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF33313A)
                )
                if (item.subtitle.isNotEmpty()) {
                    Text(
                        text = item.subtitle,
                        fontSize = 14.sp,
                        color = Color(0xFF605E68)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (item.isFavorite) {
                    IconButton(onClick = { /* Handle favorite click */ }) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Favorite",
                            tint = Color(0xFF605E68), // matching text-on-surface-variant text style
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                IconButton(onClick = onCopy) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF605E68), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
