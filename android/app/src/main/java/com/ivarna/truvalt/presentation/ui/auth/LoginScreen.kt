package com.ivarna.truvalt.presentation.ui.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

/** Walks up the context wrapper chain to find the host [Activity], or null. */
internal fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

private const val WEB_CLIENT_ID =
    "682840965721-dglou6qc1rua7n27cudtk9chqrtpspgs.apps.googleusercontent.com"

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
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) onNavigateToVault()
    }
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(48.dp))

            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Sign in to access your vault",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // ── Google Sign-In button ───────────────────────────────────────
            GoogleSignInButton(
                isLoading = uiState.isLoading,
                onClick = {
                    val activity = context.findActivity()
                    if (activity == null) {
                        scope.launch { snackbarHostState.showSnackbar("Cannot launch Google Sign-In: no Activity") }
                        return@GoogleSignInButton
                    }
                    scope.launch {
                        launchGoogleSignIn(
                            context = activity,
                            onToken = { token -> viewModel.signInWithGoogle(token) },
                            onError = { msg ->
                                scope.launch { snackbarHostState.showSnackbar(msg) }
                            }
                        )
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // ── OR divider ──────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                Text(
                    text = "  or continue with email  ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            // ── Email / Password fields ─────────────────────────────────────
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Master Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { viewModel.login(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                AnimatedVisibility(visible = uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (uiState.isLoading) "Signing in..." else "Sign In")
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register")
            }
            TextButton(onClick = {
                viewModel.setupOfflineMode()
                onNavigateToVault()
            }) {
                Text("Continue in Offline Mode")
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Google Sign-In button component ──────────────────────────────────────────

@Composable
fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        enabled = !isLoading,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        } else {
            // Use a text-based Google "G" since we don't have the asset
            Text(
                text = "G",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4285F4)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "Continue with Google",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ── Google Credential Manager launcher ───────────────────────────────────────
// Uses GetSignInWithGoogleOption which always shows the account picker (never NoCredentialException)

suspend fun launchGoogleSignIn(
    context: Activity,
    onToken: (String) -> Unit,
    onError: (String) -> Unit
) {
    val credentialManager = CredentialManager.create(context)

    // Nonce to prevent replay attacks
    val rawNonce = UUID.randomUUID().toString()
    val nonce = MessageDigest.getInstance("SHA-256")
        .digest(rawNonce.toByteArray())
        .joinToString("") { "%02x".format(it) }

    // GetSignInWithGoogleOption always shows the Google account chooser —
    // it does NOT filter by previously authorized accounts (unlike GetGoogleIdOption).
    val signInOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID)
        .setNonce(nonce)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(signInOption)
        .build()

    try {
        val result = credentialManager.getCredential(context, request)
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
            onToken(googleCred.idToken)
        } else {
            onError("Unexpected credential type: ${credential.type}")
        }
    } catch (_: GetCredentialCancellationException) {
        // Silent — user cancelled
    } catch (e: GetCredentialException) {
        android.util.Log.e("GoogleSignIn", "${e::class.simpleName}: ${e.message}")
        onError("Google Sign-In failed: ${e.message ?: e::class.simpleName}")
    } catch (e: Exception) {
        android.util.Log.e("GoogleSignIn", "Unexpected: ${e.message}")
        onError("Google Sign-In error: ${e.message}")
    }
}
