package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.repository.AuthRepository
import com.ivarna.truvalt.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVaultUnlocked: Boolean = false,
    val hasVault: Boolean = false,
    val isLocalOnly: Boolean = false,
    val serverUrl: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository,
    private val preferences: TruvaltPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun checkAuthState(
        onHasVault: () -> Unit,
        onNoVault: () -> Unit,
        onHasAccount: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val hasVault = authRepository.hasVault()
            val isVaultUnlocked = authRepository.isVaultUnlocked()
            val isLocalOnly = syncRepository.isLocalOnly()
            val serverUrl = syncRepository.getServerUrl()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                hasVault = hasVault,
                isVaultUnlocked = isVaultUnlocked,
                isLocalOnly = isLocalOnly,
                serverUrl = serverUrl
            )

            when {
                isVaultUnlocked -> onHasVault()
                isLocalOnly -> onHasVault()
                hasVault -> onHasAccount()
                else -> onNoVault()
            }
        }
    }

    fun unlockWithBiometric() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val encryptedKey = authRepository.getEncryptedVaultKey()
                if (encryptedKey != null) {
                    val vaultKey = authRepository.getEncryptedVaultKey()
                    if (vaultKey != null) {
                        authRepository.unlockVault(vaultKey)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isVaultUnlocked = true
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No vault key found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
