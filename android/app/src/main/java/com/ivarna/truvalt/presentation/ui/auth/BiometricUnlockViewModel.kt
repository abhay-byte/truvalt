package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val vaultKeyManager: VaultKeyManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<BiometricUnlockState>(BiometricUnlockState.Authenticating)
    val uiState: StateFlow<BiometricUnlockState> = _uiState.asStateFlow()
    
    fun onBiometricSuccess() {
        viewModelScope.launch {
            _uiState.value = BiometricUnlockState.Success
        }
    }
    
    fun onBiometricFailed() {
        viewModelScope.launch {
            _uiState.value = BiometricUnlockState.Error("Authentication failed. Please try again.")
        }
    }
    
    fun onFallbackToPIN() {
        viewModelScope.launch {
            _uiState.value = BiometricUnlockState.FallbackToPIN
        }
    }
}
