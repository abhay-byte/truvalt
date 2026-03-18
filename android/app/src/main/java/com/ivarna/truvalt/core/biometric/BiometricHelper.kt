package com.ivarna.truvalt.core.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class BiometricStatus {
    AVAILABLE,
    NONE_ENROLLED,
    UNAVAILABLE
}

@Singleton
class BiometricHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun canAuthenticate(): BiometricStatus {
        val manager = BiometricManager.from(context)
        // Try BIOMETRIC_STRONG first, fallback to BIOMETRIC_WEAK
        val strongResult = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        val weakResult = manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        
        return when {
            strongResult == BiometricManager.BIOMETRIC_SUCCESS || 
            weakResult == BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            strongResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ||
            weakResult == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
            else -> BiometricStatus.UNAVAILABLE
        }
    }
}
