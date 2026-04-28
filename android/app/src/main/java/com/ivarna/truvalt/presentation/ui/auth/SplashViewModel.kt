package com.ivarna.truvalt.presentation.ui.auth

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.core.pin.PinStorage
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import com.ivarna.truvalt.domain.repository.SyncRepository
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
    private val firebaseAuth: FirebaseAuth,
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val vaultRepository: VaultRepositoryImpl,
    private val syncRepository: SyncRepository,
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

        val isLocalOnly = preferences.isLocalOnly.first()
        val isBiometricEnabled = preferences.isBiometricEnabled.first()
        val isPinEnabled = pinStorage.isEnabled()
        val hasWrappedLocalVault = preferences.wrappedVaultKey.first() != null
        val hasEncryptedVaultKey = preferences.encryptedVaultKey.first() != null
        val hasFirebaseSession = firebaseAuth.currentUser != null
        val hasKnownAccount = preferences.userEmail.first() != null || preferences.firebaseUserId.first() != null

        if (hasEncryptedVaultKey) {
            if (isBiometricEnabled) return SplashDestination.UNLOCK_BIOMETRIC
            if (isPinEnabled) return SplashDestination.UNLOCK_PIN
        }

        if (hasFirebaseSession && restoreCloudVaultIfPossible()) {
            return SplashDestination.VAULT_HOME
        }

        if (isLocalOnly && hasWrappedLocalVault) {
            return SplashDestination.UNLOCK_MASTER_PASSWORD
        }

        if (hasFirebaseSession || hasKnownAccount || hasEncryptedVaultKey) {
            return SplashDestination.LOGIN
        }

        return SplashDestination.LOGIN
    }

    private suspend fun restoreCloudVaultIfPossible(): Boolean {
        val encodedKey = preferences.encryptedVaultKey.first() ?: return false
        return try {
            val encryptedKey = Base64.decode(encodedKey, Base64.DEFAULT)
            val vaultKey = cryptoManager.decryptWithKeystore(encryptedKey)
            vaultKeyManager.setInMemoryKey(vaultKey)
            vaultRepository.setVaultKey(vaultKey)
            preferences.setVaultUnlocked(true)
            syncRepository.sync()
            appLockManager.unlock()
            true
        } catch (_: Exception) {
            false
        }
    }
}
