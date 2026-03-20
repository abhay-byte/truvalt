package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    isPinEnabled: Boolean
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
        """.trimIndent()
        debugInfo = info
        println("SplashScreen - $info")
        
        val destination = when {
            // Priority 1: If locked and has biometric, use it
            isLocked && isBiometricEnabled -> {
                debugInfo += "\n\n→ BIOMETRIC"
                println("Going to BIOMETRIC")
                SplashDestination.UNLOCK_BIOMETRIC
            }
            // Priority 2: If locked and has PIN, use it
            isLocked && isPinEnabled -> {
                debugInfo += "\n\n→ PIN"
                println("Going to PIN")
                SplashDestination.UNLOCK_PIN
            }
            // Priority 3: If locked but no auth, need master password
            isLocked -> {
                debugInfo += "\n\n→ MASTER PASSWORD"
                println("Going to MASTER PASSWORD")
                SplashDestination.UNLOCK_MASTER_PASSWORD
            }
            // Priority 4: First launch onboarding
            isFirstLaunch -> {
                debugInfo += "\n\n→ ONBOARDING"
                println("Going to ONBOARDING")
                SplashDestination.ONBOARDING
            }
            // Priority 5: Already unlocked, go to vault
            else -> {
                debugInfo += "\n\n→ VAULT"
                println("Going to VAULT")
                SplashDestination.VAULT_HOME
            }
        }
        
        delay(2000) // Show debug info for 2 seconds
        onNavigationDecided(destination)
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(600)) + scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(600)
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Truvalt",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Your vault, your rules.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (debugInfo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = debugInfo,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(16.dp),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
