package com.ivarna.truvalt.presentation.ui.auth

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class BiometricUnlockState {
    data object Authenticating : BiometricUnlockState()
    data object Success : BiometricUnlockState()
    data object FallbackToPIN : BiometricUnlockState()
    data class Error(val message: String) : BiometricUnlockState()
}

@HiltViewModel
class BiometricUnlockViewModel @Inject constructor(
    private val preferences: TruvaltPreferences,
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val vaultRepository: VaultRepositoryImpl,
    private val appLockManager: AppLockManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<BiometricUnlockState>(BiometricUnlockState.Authenticating)
    val uiState: StateFlow<BiometricUnlockState> = _uiState.asStateFlow()
    
    fun onBiometricSuccess() {
        viewModelScope.launch {
            val encodedKey = preferences.encryptedVaultKey.first()
            if (encodedKey == null) {
                _uiState.value = BiometricUnlockState.Error("No encrypted vault key found for biometric unlock.")
                return@launch
            }

            val encryptedKey = Base64.decode(encodedKey, Base64.DEFAULT)
            val vaultKey = cryptoManager.decryptWithKeystore(encryptedKey)
            vaultKeyManager.setInMemoryKey(vaultKey)
            vaultRepository.setVaultKey(vaultKey)
            preferences.setVaultUnlocked(true)
            appLockManager.unlock()
            _uiState.value = BiometricUnlockState.Success
        }
    }

    fun onBiometricFailed(message: String = "Authentication failed. Please try again.") {
        viewModelScope.launch {
            _uiState.value = BiometricUnlockState.Error(message)
        }
    }
    
    fun onFallbackToPIN() {
        viewModelScope.launch {
            _uiState.value = BiometricUnlockState.FallbackToPIN
        }
    }
}
