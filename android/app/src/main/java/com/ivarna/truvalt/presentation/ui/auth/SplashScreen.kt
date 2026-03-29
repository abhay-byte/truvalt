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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ivarna.truvalt.R
import kotlinx.coroutines.delay

enum class SplashDestination {
    ONBOARDING,
    UNLOCK_BIOMETRIC,
    UNLOCK_PIN,
    UNLOCK_MASTER_PASSWORD,
    VAULT_HOME
}

@Composable
fun SplashScreen(
    onNavigationDecided: (SplashDestination) -> Unit,
    isFirstLaunch: Boolean,
    isLocked: Boolean,
    isBiometricEnabled: Boolean,
    isPinEnabled: Boolean,
    hasMasterPassword: Boolean
) {
    var visible by remember { mutableStateOf(false) }
    var debugInfo by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        visible = true
        delay(1500)
        
        // Debug info
        val info = """
            isLocked: $isLocked
            isBiometric: $isBiometricEnabled
            isPinEnabled: $isPinEnabled
            isFirstLaunch: $isFirstLaunch
            hasMasterPassword: $hasMasterPassword
        """.trimIndent()
        debugInfo = info
        println("SplashScreen - $info")
        
        val destination = when {
            // Priority 1: First launch onboarding
            isFirstLaunch -> {
                debugInfo += "\n\n→ ONBOARDING"
                println("Going to ONBOARDING")
                SplashDestination.ONBOARDING
            }
            // Priority 2: If locked and has biometric, use it
            isLocked && isBiometricEnabled -> {
                debugInfo += "\n\n→ BIOMETRIC"
                println("Going to BIOMETRIC")
                SplashDestination.UNLOCK_BIOMETRIC
            }
            // Priority 3: If locked and has PIN, use it
            isLocked && isPinEnabled -> {
                debugInfo += "\n\n→ PIN"
                println("Going to PIN")
                SplashDestination.UNLOCK_PIN
            }
            // Priority 4: If locked and has master password, unlock with it
            isLocked && hasMasterPassword -> {
                debugInfo += "\n\n→ MASTER PASSWORD"
                println("Going to MASTER PASSWORD")
                SplashDestination.UNLOCK_MASTER_PASSWORD
            }
            // Priority 5: If locked but no auth set up, go to onboarding
            isLocked -> {
                debugInfo += "\n\n→ ONBOARDING (no auth)"
                println("Going to ONBOARDING (no auth)")
                SplashDestination.ONBOARDING
            }
            // Priority 6: Already unlocked, go to vault
            else -> {
                debugInfo += "\n\n→ VAULT"
                println("Going to VAULT")
                SplashDestination.VAULT_HOME
            }
        }
        
        delay(2000) // Show debug info for 2 seconds
        onNavigationDecided(destination)
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
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(truvaltSurface)
            .background(radialGradient), // The background glow effect
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
                modifier = Modifier.fillMaxWidth()
            ) {
                // Outer circle for the icon
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF190968)) // surface-container from HTML
                        .border(1.dp, truvaltPrimary.copy(alpha = 0.1f), CircleShape)
                        .shadow(
                            elevation = 50.dp,
                            shape = CircleShape,
                            ambientColor = truvaltPrimary,
                            spotColor = truvaltPrimary
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Inner rotated square with gradient
                    Box(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(truvaltPrimary, truvaltPrimaryContainer)
                                )
                            )
                            .rotate(3f)
                            .shadow(24.dp, shape = RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.truvalt_icon),
                            contentDescription = "Truvalt Logo",
                            modifier = Modifier.size(56.dp) // Adjusted the inside icon size
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Truvalt",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = truvaltOnSurface,
                    letterSpacing = (-1.5).sp // tracking-tighter
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Your vault, zero knowledge",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = truvaltOnSurfaceVariant.copy(alpha = 0.8f),
                    letterSpacing = 0.5.sp // tracking-wide
                )
            }
        }

        // Bottom Loading Context
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(48.dp)) {
                    // Track
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = truvaltSurfaceContainerHighest,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                    // Progress
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = truvaltPrimary,
                                startAngle = 0f,
                                sweepAngle = 270f,
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
                    fontSize = 11.sp, // 0.6875rem
                    fontWeight = FontWeight.Bold,
                    color = truvaltOnSurfaceVariant.copy(alpha = 0.4f),
                    letterSpacing = 2.sp, // tracking-[0.15em]
                )
            }
        }
    }
}
