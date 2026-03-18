package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivarna.truvalt.presentation.ui.shared.PinDotsRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSetupScreen(
    onComplete: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: PinSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    LaunchedEffect(uiState.isComplete) {
        if (uiState.isComplete) {
            onComplete()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (uiState.step) {
                            PinSetupStep.ENTER_PIN -> "Set a PIN"
                            PinSetupStep.CONFIRM_PIN -> "Confirm PIN"
                        }
                    )
                }
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
                text = when (uiState.step) {
                    PinSetupStep.ENTER_PIN -> "Enter a 4-8 digit PIN"
                    PinSetupStep.CONFIRM_PIN -> "Re-enter your PIN"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PinDotsRow(
                currentLength = uiState.pin.length,
                maxLength = uiState.maxPinLength,
                hasError = uiState.hasError
            )
            
            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            NumericKeypad(
                onDigitClick = { viewModel.onDigitEntered(it) },
                onBackspace = { viewModel.onBackspace() },
                showConfirm = uiState.step == PinSetupStep.ENTER_PIN && uiState.pin.length >= 4,
                onConfirm = { viewModel.onConfirmStep() }
            )
        }
    }
}

@Composable
fun NumericKeypad(
    onDigitClick: (Int) -> Unit,
    onBackspace: () -> Unit,
    showConfirm: Boolean = false,
    onConfirm: () -> Unit = {}
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val rows = listOf(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
        )
        
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { digit ->
                    FilledTonalButton(
                        onClick = { onDigitClick(digit) },
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape
                    ) {
                        Text(
                            text = digit.toString(),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = onBackspace,
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Backspace,
                    contentDescription = "Backspace",
                    modifier = Modifier.size(32.dp)
                )
            }
            
            FilledTonalButton(
                onClick = { onDigitClick(0) },
                modifier = Modifier.size(72.dp),
                shape = CircleShape
            ) {
                Text(
                    text = "0",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (showConfirm) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.size(72.dp),
                    shape = CircleShape
                ) {
                    Text("✓", style = MaterialTheme.typography.headlineMedium)
                }
            } else {
                Spacer(Modifier.size(72.dp))
            }
        }
    }
}
