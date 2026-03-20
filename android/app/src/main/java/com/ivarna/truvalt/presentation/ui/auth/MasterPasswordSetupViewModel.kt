package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MasterPasswordSetupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class MasterPasswordSetupViewModel @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val vaultRepository: VaultRepositoryImpl,
    private val appLockManager: AppLockManager,
    private val preferences: TruvaltPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(MasterPasswordSetupUiState())
    val uiState: StateFlow<MasterPasswordSetupUiState> = _uiState.asStateFlow()

    fun createMasterPassword(password: String, confirmPassword: String) {
        if (password.length < 8) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 8 characters")
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(error = "Passwords do not match")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Generate vault key from master password
                val vaultKey = cryptoManager.deriveKey(password)
                
                // Store in memory
                vaultKeyManager.setInMemoryKey(vaultKey)
                vaultRepository.setVaultKey(vaultKey)
                
                // Wrap and store encrypted vault key
                val wrappedKey = vaultKeyManager.wrapAndStoreKey(vaultKey)
                preferences.setWrappedVaultKey(wrappedKey)
                
                // Mark first launch complete
                preferences.setFirstLaunch(false)
                
                // Unlock the app
                appLockManager.unlock()
                
                _uiState.value = _uiState.value.copy(isLoading = false, isComplete = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to create vault"
                )
            }
        }
    }
}
