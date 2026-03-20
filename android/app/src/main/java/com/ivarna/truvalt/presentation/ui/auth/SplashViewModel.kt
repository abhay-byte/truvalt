package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.biometric.BiometricHelper
import com.ivarna.truvalt.core.biometric.BiometricStatus
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.core.pin.PinStorage
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appLockManager: AppLockManager,
    private val preferences: TruvaltPreferences,
    private val biometricHelper: BiometricHelper,
    private val pinStorage: PinStorage
) : ViewModel() {

    val isLocked: StateFlow<Boolean> = appLockManager.isLocked
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isBiometricEnabled: StateFlow<Boolean> = preferences.isBiometricEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    // Check PIN status directly each time
    val isPinEnabled: StateFlow<Boolean> = MutableStateFlow(false).apply {
        value = pinStorage.isEnabled()
        println("SplashViewModel - PIN enabled check: ${pinStorage.isEnabled()}, hash: ${pinStorage.getHash()}")
    }.stateIn(viewModelScope, SharingStarted.Eagerly, pinStorage.isEnabled())

    val isFirstLaunch: StateFlow<Boolean> = preferences.isFirstLaunch
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    
    val hasMasterPassword: StateFlow<Boolean> = preferences.wrappedVaultKey
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    
    fun isBiometricAvailable(): Boolean {
        val result = isBiometricEnabled.value && biometricHelper.canAuthenticate() == BiometricStatus.AVAILABLE
        println("SplashViewModel - Biometric available: $result (enabled: ${isBiometricEnabled.value}, status: ${biometricHelper.canAuthenticate()})")
        return result
    }
    
    fun unlock() {
        appLockManager.unlock()
    }
}
