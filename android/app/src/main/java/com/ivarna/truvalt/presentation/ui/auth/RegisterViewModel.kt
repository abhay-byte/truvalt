package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import com.ivarna.truvalt.domain.repository.AuthRepository
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vaultRepository: VaultRepository,
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = authRepository.createVault(email, password)
            
            result.fold(
                onSuccess = {
                    // Set vault key after successful registration
                    val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
                    vaultKeyManager.setInMemoryKey(derivedKeys.vaultKey)
                    (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(derivedKeys.vaultKey)
                    appLockManager.unlock()
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistered = true
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun setupOfflineMode() {
        Log.d("RegisterViewModel", "=== SETUP OFFLINE MODE ===")
        val offlineKey = ByteArray(32) { 0 }
        vaultKeyManager.setInMemoryKey(offlineKey)
        (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(offlineKey)
        appLockManager.unlock()
        Log.d("RegisterViewModel", "=== OFFLINE MODE READY ===")
    }
}
