package com.ivarna.truvalt.presentation.ui.auth

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.core.pin.PinStorage
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appLockManager: AppLockManager,
    private val preferences: TruvaltPreferences,
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val vaultRepository: VaultRepositoryImpl,
    private val pinStorage: PinStorage
) : ViewModel() {

    private val _startupDestination = MutableStateFlow<SplashDestination?>(null)
    val startupDestination: StateFlow<SplashDestination?> = _startupDestination
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            _startupDestination.value = resolveStartupDestination()
        }
    }
    
    fun unlock() {
        appLockManager.unlock()
    }

    private suspend fun resolveStartupDestination(): SplashDestination {
        val isFirstLaunch = preferences.isFirstLaunch.first()
        if (isFirstLaunch) return SplashDestination.ONBOARDING

        val isBiometricEnabled = preferences.isBiometricEnabled.first()
        val isPinEnabled = pinStorage.isEnabled()
        val hasWrappedLocalVault = preferences.wrappedVaultKey.first() != null
        val hasEncryptedVaultKey = preferences.encryptedVaultKey.first() != null

        if (hasEncryptedVaultKey || hasWrappedLocalVault) {
            if (isBiometricEnabled) return SplashDestination.UNLOCK_BIOMETRIC
            if (isPinEnabled) return SplashDestination.UNLOCK_PIN
            return SplashDestination.UNLOCK_MASTER_PASSWORD
        }

        return SplashDestination.MASTER_PASSWORD_SETUP
    }
}
