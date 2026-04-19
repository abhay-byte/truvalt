package com.ivarna.truvalt.autofill

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AutofillAuthActivity : AppCompatActivity() {

    @Inject
    lateinit var vaultKeyManager: VaultKeyManager

    @Inject
    lateinit var preferences: TruvaltPreferences

    @Inject
    lateinit var cryptoManager: CryptoManager

    private var authError by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val packageName = intent.getStringExtra("package_name") ?: return finish()

        setContent {
            val isBiometricEnabled by preferences.isBiometricEnabled.collectAsState(initial = false)

            LaunchedEffect(isBiometricEnabled) {
                if (vaultKeyManager.hasKey()) {
                    finishWithSuccess()
                } else if (restoreVaultKey()) {
                    finishWithSuccess()
                } else if (isBiometricEnabled) {
                    showBiometricPrompt()
                } else {
                    authError = "Open Truvalt and unlock the vault first"
                }
            }

            Scaffold { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Unlock Truvalt",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Authenticate to autofill your credentials",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    authError?.let { error ->
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { showBiometricPrompt() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Authenticate")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { finish() }) {
                        Text("Cancel")
                    }
                }
            }
        }
    }

    private fun showBiometricPrompt() {
        val manager = BiometricManager.from(this)
        val authenticators = when {
            manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            else -> BiometricManager.Authenticators.BIOMETRIC_WEAK
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock Truvalt")
            .setSubtitle("Authenticate to autofill credentials")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(authenticators)
            .build()

        val biometricPrompt = BiometricPrompt(
            this,
            ContextCompat.getMainExecutor(this),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    lifecycleScope.launch {
                        if (restoreVaultKey()) {
                            finishWithSuccess()
                        } else {
                            authError = "Unable to restore vault key"
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    // Keep trying
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        // Show error in UI
                    }
                }
            }
        )

        biometricPrompt.authenticate(promptInfo)
    }

    private suspend fun restoreVaultKey(): Boolean {
        val encodedKey = preferences.encryptedVaultKey.first() ?: return false
        val encryptedKey = android.util.Base64.decode(encodedKey, android.util.Base64.DEFAULT)
        val vaultKey = cryptoManager.decryptWithKeystore(encryptedKey)
        vaultKeyManager.setInMemoryKey(vaultKey)
        preferences.setVaultUnlocked(true)
        return true
    }

    private fun finishWithSuccess() {
        setResult(RESULT_OK)
        finish()
    }
}
