package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private enum class CloudAuthDestination {
    LOGIN,
    REGISTER
}

@Composable
fun ServerSetupScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToVault: () -> Unit,
    viewModel: ServerSetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var serverUrl by remember { mutableStateOf("") }
    var useLocalOnly by remember { mutableStateOf(false) }
    var cloudAuthDestination by remember { mutableStateOf(CloudAuthDestination.REGISTER) }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            if (useLocalOnly) {
                onNavigateToVault()
            } else {
                when (cloudAuthDestination) {
                    CloudAuthDestination.LOGIN -> onNavigateToLogin()
                    CloudAuthDestination.REGISTER -> onNavigateToRegister()
                }
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Setup Server",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connect to a self-hosted server, or use Firebase cloud sync (no server URL needed) or local-only mode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Server Configuration",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = serverUrl,
                        onValueChange = { serverUrl = it },
                        label = { Text("Server URL") },
                        placeholder = { Text("https://your-server.com") },
                        leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        singleLine = true,
                        enabled = !useLocalOnly
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = useLocalOnly,
                            onCheckedChange = { useLocalOnly = it }
                        )
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Use local-only mode (no cloud sync)",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (!useLocalOnly) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Next step after saving",
                            style = MaterialTheme.typography.labelLarge
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { cloudAuthDestination = CloudAuthDestination.REGISTER },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Sign Up")
                            }

                            Button(
                                onClick = { cloudAuthDestination = CloudAuthDestination.LOGIN },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Login")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (cloudAuthDestination == CloudAuthDestination.REGISTER) {
                                "New device or new user: create an account after saving."
                            } else {
                                "Existing account: go straight to login after saving."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveServerConfig(serverUrl, useLocalOnly) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && (serverUrl.isNotBlank() || useLocalOnly)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Text(
                    text = if (useLocalOnly) {
                        "Continue"
                    } else if (cloudAuthDestination == CloudAuthDestination.REGISTER) {
                        "Save & Sign Up"
                    } else {
                        "Save & Login"
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!useLocalOnly) {
                TextButton(onClick = onNavigateToRegister) {
                    Text("Use Firebase Cloud → Create Account")
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(onClick = onNavigateToLogin) {
                    Text("Use Firebase Cloud → Sign In")
                }
            }
        }
    }
}
