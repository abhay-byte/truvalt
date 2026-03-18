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
        return when (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
            else -> BiometricStatus.UNAVAILABLE
        }
    }
}
