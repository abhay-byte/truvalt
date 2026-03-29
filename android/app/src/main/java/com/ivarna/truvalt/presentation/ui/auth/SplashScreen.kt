package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.truvalt.R
import kotlinx.coroutines.delay

enum class SplashDestination {
    ONBOARDING,
    SERVER_SETUP,
    LOGIN,
    UNLOCK_BIOMETRIC,
    UNLOCK_PIN,
    UNLOCK_MASTER_PASSWORD,
    VAULT_HOME
}

@Composable
fun SplashScreen(
    onNavigationDecided: (SplashDestination) -> Unit,
    destination: SplashDestination?
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(destination) {
        visible = true
        if (destination != null) {
            delay(1500)
            onNavigationDecided(destination)
        }
    }

    val truvaltSurface = Color(0xFF0E0050)
    val truvaltPrimary = Color(0xFFC5C0FF)
    val truvaltPrimaryContainer = Color(0xFF534AB7)
    val truvaltSurfaceContainerHighest = Color(0xFF30277C)
    val truvaltOnSurface = Color(0xFFE4DFFF)
    val truvaltOnSurfaceVariant = Color(0xFFC8C4D5)
    
    // Background Radial Gradient
    val radialGradient = Brush.radialGradient(
        colors = listOf(
            Color(0x26534AB7), // 15% opacity of #534ab7
            Color(0x000E0050)  // 0% opacity of #0e0050
        ),
        radius = 1200f
    )

    // Animation for spinner
    val infiniteTransition = rememberInfiniteTransition(label = "spinner")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val loaderPulse by infiniteTransition.animateFloat(
        initialValue = 0.86f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loaderPulse"
    )
    val loaderSweep by infiniteTransition.animateFloat(
        initialValue = 120f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loaderSweep"
    )
    val dotsPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "dotsPhase"
    )
    val logoFloat by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoFloat"
    )
    val logoTilt by infiniteTransition.animateFloat(
        initialValue = 1.5f,
        targetValue = 5.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoTilt"
    )
    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(truvaltSurface)
            .background(radialGradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(1000, easing = EaseOutExpo)) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(1000, easing = EaseOutBack)
            ),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(132.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF190968))
                        .border(1.dp, truvaltPrimary.copy(alpha = 0.1f), CircleShape)
                        .shadow(
                            elevation = 50.dp,
                            shape = CircleShape,
                            ambientColor = truvaltPrimary,
                            spotColor = truvaltPrimary
                        )
                        .graphicsLayer {
                            translationY = logoFloat
                            scaleX = logoPulse
                            scaleY = logoPulse
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        truvaltPrimary.copy(alpha = 0.16f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .blur(8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(98.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(truvaltPrimary, truvaltPrimaryContainer)
                                )
                            )
                            .shadow(24.dp, shape = RoundedCornerShape(30.dp))
                            .graphicsLayer {
                                rotationZ = logoTilt
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.truvalt_icon),
                            contentDescription = "Truvalt Logo",
                            modifier = Modifier.size(68.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Truvalt",
                    fontSize = 50.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = truvaltOnSurface,
                    letterSpacing = (-1.8).sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Your vault, zero knowledge",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = truvaltOnSurfaceVariant.copy(alpha = 0.8f),
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Bottom Loading Context
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 56.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = truvaltSurfaceContainerHighest,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = loaderPulse
                                scaleY = loaderPulse
                            }
                            .rotate(rotation)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = truvaltPrimary,
                                startAngle = 0f,
                                sweepAngle = loaderSweep,
                                useCenter = false,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "ESTABLISHING SECURE LINK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = truvaltOnSurfaceVariant.copy(alpha = 0.4f),
                    letterSpacing = 2.sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        val phase = (dotsPhase + index * 0.18f) % 1f
                        val alpha = 0.24f + (1f - kotlin.math.abs(phase - 0.5f) * 2f) * 0.66f
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(truvaltPrimary.copy(alpha = alpha))
                        )
                    }
                }
            }
        }
    }
}
