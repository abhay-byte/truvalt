package com.ivarna.truvalt.presentation.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.CryptoManager
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.lock.AppLockManager
import com.ivarna.truvalt.data.repository.AuthRepositoryImpl
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import com.ivarna.truvalt.domain.repository.AuthRepository
import com.ivarna.truvalt.domain.repository.SyncRepository
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
    private val syncRepository: SyncRepository,
    private val cryptoManager: CryptoManager,
    private val vaultKeyManager: VaultKeyManager,
    private val appLockManager: AppLockManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = authRepository.unlockWithPassword(email, password)
            result.fold(
                onSuccess = {
                    val derivedKeys = cryptoManager.deriveKeyFromPassword(password, email)
                    vaultKeyManager.setInMemoryKey(derivedKeys.vaultKey)
                    (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(derivedKeys.vaultKey)
                    syncAfterUnlock("password login")
                    appLockManager.unlock()
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                },
                onFailure = { e ->
                    Log.e("LoginViewModel", "Login failed: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Login failed")
                }
            )
        }
    }

    fun signInWithGoogle(googleIdToken: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val authImpl = authRepository as? AuthRepositoryImpl
            if (authImpl == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Auth not available")
                return@launch
            }
            val result = authImpl.signInWithGoogle(googleIdToken)
            result.fold(
                onSuccess = {
                    // Vault key was set inside signInWithGoogle — just wire it up here
                    authImpl.getMasterKey()?.let { mk ->
                        vaultKeyManager.setInMemoryKey(mk)
                        (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(mk)
                    }
                    syncAfterUnlock("Google sign-in")
                    appLockManager.unlock()
                    _uiState.value = _uiState.value.copy(isLoading = false, isLoggedIn = true)
                },
                onFailure = { e ->
                    Log.e("LoginViewModel", "Google sign-in failed: ${e.message}", e)
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Google sign-in failed")
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun setupOfflineMode() {
        val offlineKey = ByteArray(32) { 0 }
        vaultKeyManager.setInMemoryKey(offlineKey)
        (vaultRepository as? VaultRepositoryImpl)?.setVaultKey(offlineKey)
        appLockManager.unlock()
    }

    private suspend fun syncAfterUnlock(reason: String) {
        val result = runCatching { syncRepository.sync() }
            .getOrElse { error ->
                Log.w("LoginViewModel", "Sync crashed after $reason", error)
                return
            }

        result.exceptionOrNull()?.let { error ->
            Log.w("LoginViewModel", "Sync skipped after $reason: ${error.message}")
        }
    }
}
