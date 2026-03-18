package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivarna.truvalt.presentation.ui.shared.PinDotsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinUnlockScreen(
    onUnlockSuccess: () -> Unit,
    onForgotPin: () -> Unit = {},
    viewModel: PinUnlockViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.unlockSuccess) {
        if (uiState.unlockSuccess) {
            onUnlockSuccess()
        }
    }
    
    LaunchedEffect(uiState.isLocked) {
        if (uiState.isLocked) {
            onForgotPin()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unlock Vault") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter your PIN",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PinDotsRow(
                currentLength = uiState.currentInput.length,
                maxLength = 8,
                hasError = uiState.error != null
            )
            
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            NumericKeypad(
                onDigitClick = { viewModel.onDigitEntered(it.toString()) },
                onBackspace = { viewModel.onBackspace() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = onForgotPin) {
                Text("Forgot PIN? Use master password")
            }
        }
    }
}
