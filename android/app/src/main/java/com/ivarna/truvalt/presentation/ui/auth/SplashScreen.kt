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
    UNLOCK_PASSWORD,
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
    
    LaunchedEffect(Unit) {
        visible = true
        delay(1500)
        
        val destination = when {
            isLocked && isBiometricEnabled -> SplashDestination.UNLOCK_BIOMETRIC
            isLocked && isPinEnabled -> SplashDestination.UNLOCK_PIN
            isLocked -> SplashDestination.UNLOCK_PASSWORD
            isFirstLaunch -> SplashDestination.ONBOARDING
            else -> SplashDestination.VAULT_HOME
        }
        
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
            }
        }
    }
}
