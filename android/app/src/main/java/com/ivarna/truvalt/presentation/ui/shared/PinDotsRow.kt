package com.ivarna.truvalt.presentation.ui.shared

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun PinDotsRow(
    currentLength: Int,
    maxLength: Int? = null,
    hasError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }
    
    // Dynamic dot count:
    // - If maxLength is provided (unlock screen), use it
    // - Otherwise (setup screen), show at least 4 or current length if greater
    val displayDots = maxLength ?: maxOf(4, currentLength)
    
    LaunchedEffect(hasError) {
        if (hasError) {
            repeat(4) {
                shakeOffset.animateTo(10f, tween(50))
                shakeOffset.animateTo(-10f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }
    
    Row(
        modifier = modifier.offset(x = shakeOffset.value.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(displayDots) { index ->
            val isFilled = index < currentLength
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        color = when {
                            hasError -> MaterialTheme.colorScheme.error
                            isFilled -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                        },
                        shape = CircleShape
                    )
                    .scale(if (isFilled) 1.1f else 1f)
            )
        }
    }
}
