package com.ivarna.truvalt.core.biometric

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class BiometricPromptManager(
    private val activity: AppCompatActivity
) {
    sealed class BiometricResult {
        object Success : BiometricResult()
        object Failed : BiometricResult()
        data class Error(val code: Int, val message: String) : BiometricResult()
        object NotEnrolled : BiometricResult()
        object HardwareUnavailable : BiometricResult()
        object FallbackRequested : BiometricResult()
    }

    private val resultChannel = Channel<BiometricResult>(Channel.CONFLATED)
    val results: Flow<BiometricResult> = resultChannel.receiveAsFlow()

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(activity)
        val strongResult = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val weakResult = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        return strongResult == BiometricManager.BIOMETRIC_SUCCESS || 
               weakResult == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showPrompt(title: String = "Unlock Truvalt", negativeText: String = "Use PIN") {
        val manager = BiometricManager.from(activity)
        
        // Check STRONG first, fallback to WEAK
        val authenticators = when {
            manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricManager.Authenticators.BIOMETRIC_STRONG
            manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS ->
                BiometricManager.Authenticators.BIOMETRIC_WEAK
            else -> {
                when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                    BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                        resultChannel.trySend(BiometricResult.NotEnrolled)
                        return
                    }
                    else -> {
                        resultChannel.trySend(BiometricResult.HardwareUnavailable)
                        return
                    }
                }
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("Verify your identity to access your vault")
            .setNegativeButtonText(negativeText)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(false)
            .build()

        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    resultChannel.trySend(BiometricResult.Success)
                }
                override fun onAuthenticationFailed() {
                    resultChannel.trySend(BiometricResult.Failed)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            resultChannel.trySend(BiometricResult.FallbackRequested)
                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            resultChannel.trySend(BiometricResult.NotEnrolled)
                        }
                        else -> {
                            resultChannel.trySend(BiometricResult.Error(errorCode, errString.toString()))
                        }
                    }
                }
            }
        )
        prompt.authenticate(promptInfo)
    }
}
