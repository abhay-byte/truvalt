package com.ivarna.truvalt.presentation.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.truvalt.core.crypto.TotpGenerator
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun TotpLivePreview(secret: String, period: Int = 30) {
    var totpCode by remember { mutableStateOf("------") }
    var secondsRemaining by remember { mutableIntStateOf(period) }

    LaunchedEffect(secret) {
        while (isActive) {
            val epochSeconds = System.currentTimeMillis() / 1000L
            val currentPeriod = epochSeconds / period
            val secondsElapsed = (epochSeconds % period).toInt()
            secondsRemaining = period - secondsElapsed

            totpCode = try {
                TotpGenerator.generate(secret, period.toLong())
            } catch (e: Exception) {
                "------"
            }

            delay(1000L)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        val displayCode = if (totpCode.length == 6) {
            "${totpCode.substring(0, 3)} ${totpCode.substring(3, 6)}"
        } else totpCode

        Text(
            text = displayCode,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.Monospace,
                letterSpacing = 8.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val progress = secondsRemaining.toFloat() / period.toFloat()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = when {
                secondsRemaining > 10 -> MaterialTheme.colorScheme.primary
                secondsRemaining > 5 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.error
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Refreshes in ${secondsRemaining}s",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
