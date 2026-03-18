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
        return manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showPrompt(title: String = "Unlock Truvalt", negativeText: String = "Use PIN") {
        val manager = BiometricManager.from(activity)
        when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.NotEnrolled)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("Verify your identity to access your vault")
            .setNegativeButtonText(negativeText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
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
