package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
    val activeColor = if (password.isEmpty()) MaterialTheme.colorScheme.surfaceVariant
                      else strengthColors[minOf(strength, strengthColors.lastIndex)]
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (password.isNotEmpty() && index < strength) activeColor else inactiveColor)
                )
            }
        }
        if (password.isNotEmpty()) {
            Text(
                text = strengthLabels[strength],
                style = MaterialTheme.typography.labelSmall,
                color = activeColor
            )
        }
    }
}
