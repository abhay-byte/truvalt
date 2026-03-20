package com.ivarna.truvalt.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val vaultRepository: VaultRepository,
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "=== LOGIN START ===")
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val result = authRepository.unlockWithPassword(email, password)
            
            result.fold(
                onSuccess = {
                    Log.d("LoginViewModel", "Auth successful, deriving keys...")
                    // Set vault key after successful login
                    val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
                    Log.d("LoginViewModel", "Keys derived - vaultKey size: ${derivedKeys.vaultKey.size}")
                    
                    Log.d("LoginViewModel", "Setting vault key in VaultKeyManager...")
                    vaultKeyManager.setInMemoryKey(derivedKeys.vaultKey)
                    
                    Log.d("LoginViewModel", "Setting vault key in VaultRepository...")
                    (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(derivedKeys.vaultKey)
                    
                    Log.d("LoginViewModel", "Unlocking AppLockManager...")
                    appLockManager.unlock()
                    
                    Log.d("LoginViewModel", "=== LOGIN COMPLETE ===")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true
                    )
                },
                onFailure = { e ->
                    Log.e("LoginViewModel", "Login failed: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun setupOfflineMode() {
        Log.d("LoginViewModel", "=== SETUP OFFLINE MODE ===")
        // Generate a temporary vault key for offline mode
        val offlineKey = ByteArray(32) { 0 } // Simple zero key for offline
        Log.d("LoginViewModel", "Generated offline key: ${offlineKey.size} bytes")
        
        vaultKeyManager.setInMemoryKey(offlineKey)
        (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(offlineKey)
        appLockManager.unlock()
        
        Log.d("LoginViewModel", "=== OFFLINE MODE READY ===")
    }
}
