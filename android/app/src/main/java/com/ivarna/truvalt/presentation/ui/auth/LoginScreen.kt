package com.ivarna.truvalt.presentation.ui.auth

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToVault: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onNavigateToVault()
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = AuthBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Log In", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AuthOnSurface) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AuthBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Brand Header
            BrandIconHeader()
            
            Spacer(Modifier.height(24.dp))

            // Header
            Text(
                text = "Welcome back",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AuthOnSurface,
                letterSpacing = (-1).sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Sign in to access your secure vault.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = AuthOnSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            // Form Fields
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                placeholder = "name@example.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Master password",
                placeholder = "••••••••••••",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(32.dp))

            // Primary Action
            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AuthPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = AuthPrimary,
                    disabledContentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text(
                        text = "Log In", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Google Sign-In
            RedesignedGoogleSignInButton(
                isLoading = uiState.isLoading,
                onClick = {
                    val activity = context.findActivity()
                    if (activity != null) {
                        scope.launch {
                            launchGoogleSignIn(
                                context = activity,
                                onToken = { token -> viewModel.signInWithGoogle(token) },
                                onError = { msg -> scope.launch { snackbarHostState.showSnackbar(msg) } }
                            )
                        }
                    }
                }
            )

            Spacer(Modifier.height(40.dp))

            // Security Status Card
            SecurityStatusCard(
                title = "Secure Layer Active",
                description = "Zero-knowledge encryption is currently protecting your session data.",
                icon = Icons.Default.Shield
            )

            Spacer(Modifier.height(40.dp))

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    fontSize = 14.sp,
                    color = AuthOnSurfaceVariant
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text("Sign up", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AuthPrimary)
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(
                onClick = {
                    viewModel.setupOfflineMode()
                    onNavigateToVault()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudOff,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = AuthOutlineVariant
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Continue offline",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AuthOutlineVariant,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(48.dp))
        }
    }
}
