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

data class MasterPasswordUnlockUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val unlockSuccess: Boolean = false
)

@HiltViewModel
class MasterPasswordUnlockViewModel @Inject constructor(
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val vaultRepository: VaultRepositoryImpl,
    private val appLockManager: AppLockManager,
    private val preferences: TruvaltPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(MasterPasswordUnlockUiState())
    val uiState: StateFlow<MasterPasswordUnlockUiState> = _uiState.asStateFlow()

    fun unlock(password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                // Derive vault key from password
                val vaultKey = cryptoManager.deriveKey(password)
                
                // Verify by trying to unwrap stored key
                val wrappedKey = preferences.getWrappedVaultKey()
                if (wrappedKey != null) {
                    try {
                        val unwrappedKey = vaultKeyManager.retrieveAndUnwrapKey(wrappedKey)
                        // Keys should match
                        if (!vaultKey.contentEquals(unwrappedKey)) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Incorrect password"
                            )
                            return@launch
                        }
                    } catch (e: Exception) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Incorrect password"
                        )
                        return@launch
                    }
                }
                
                // Set vault key in memory
                vaultKeyManager.setInMemoryKey(vaultKey)
                vaultRepository.setVaultKey(vaultKey)
                
                // Unlock app
                appLockManager.unlock()
                
                _uiState.value = _uiState.value.copy(isLoading = false, unlockSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to unlock vault"
                )
            }
        }
    }
}
