package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private fun calculateStrength(password: String): Int {
    if (password.isEmpty()) return 0
    var score = 0
    if (password.length >= 8) score++
    if (password.length >= 14) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    val common = listOf("password", "123456", "qwerty", "abc123", "letmein")
    if (common.any { password.lowercase().contains(it) }) score = minOf(score, 1)
    return minOf(score, 4)
}

private val strengthColors = listOf(
    Color(0xFFD32F2F), // red
    Color(0xFFF57C00), // orange
    Color(0xFFFBC02D), // yellow
    Color(0xFF388E3C)  // green
)

private val strengthLabels = listOf("Very Weak", "Weak", "Fair", "Strong", "Very Strong")

@Composable
fun PasswordStrengthBar(
    password: String,
    modifier: Modifier = Modifier
) {
    val strength = remember(password) { calculateStrength(password) }
    
    // Design System: Tertiary for "Weak", Primary for "Strong"
    // Interpolating between Tertiary and Primary based on strength
    val colorScheme = MaterialTheme.colorScheme
    val strengthColor = when {
        password.isEmpty() -> colorScheme.surfaceContainerHighest
        strength <= 1 -> colorScheme.tertiary
        strength == 2 -> colorScheme.tertiary.copy(alpha = 0.7f) // Intermediate
        else -> colorScheme.primary
    }
    
    val trackColor = colorScheme.surfaceContainerHighest

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (password.isEmpty()) 0f else (strength + 1) / 5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(strengthColor)
            )
        }
        
        if (password.isNotEmpty()) {
            Text(
                text = "Security Level: ${strengthLabels[strength]}",
                style = MaterialTheme.typography.labelSmall,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}
