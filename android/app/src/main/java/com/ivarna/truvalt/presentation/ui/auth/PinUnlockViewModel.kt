package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.pin.PinHasher
import com.ivarna.truvalt.core.pin.PinStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinUnlockUiState(
    val currentInput: String = "",
    val error: String? = null,
    val failCount: Int = 0,
    val isLocked: Boolean = false,
    val unlockSuccess: Boolean = false
)

@HiltViewModel
class PinUnlockViewModel @Inject constructor(
    private val pinHasher: PinHasher,
    private val pinStorage: PinStorage,
    private val vaultKeyManager: VaultKeyManager
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PinUnlockUiState())
    val uiState: StateFlow<PinUnlockUiState> = _uiState.asStateFlow()
    
    companion object {
        private const val MAX_ATTEMPTS = 5
    }
    
    init {
        _uiState.value = _uiState.value.copy(failCount = pinStorage.getFailCount())
    }
    
    fun onDigitEntered(digit: String) {
        if (_uiState.value.isLocked) return
        
        val current = _uiState.value.currentInput
        if (current.length < 8) {
            val newInput = current + digit
            _uiState.value = _uiState.value.copy(
                currentInput = newInput,
                error = null
            )
            
            // Auto-submit when reaching 4-8 digits
            if (newInput.length >= 4) {
                onConfirm()
            }
        }
    }
    
    fun onBackspace() {
        val current = _uiState.value.currentInput
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                currentInput = current.dropLast(1),
                error = null
            )
        }
    }
    
    fun onConfirm() {
        if (_uiState.value.isLocked) return
        
        val pin = _uiState.value.currentInput
        if (pin.length < 4) {
            _uiState.value = _uiState.value.copy(
                error = "PIN must be at least 4 digits"
            )
            return
        }
        
        viewModelScope.launch {
            val salt = pinStorage.getSalt()
            val storedHash = pinStorage.getHash()
            
            if (salt == null || storedHash == null) {
                _uiState.value = _uiState.value.copy(
                    error = "PIN not configured",
                    currentInput = ""
                )
                return@launch
            }
            
            if (pinHasher.verifyPin(pin, salt, storedHash)) {
                pinStorage.resetFailCount()
                _uiState.value = _uiState.value.copy(
                    unlockSuccess = true,
                    error = null
                )
            } else {
                pinStorage.incrementFailCount()
                val newFailCount = pinStorage.getFailCount()
                
                if (newFailCount >= MAX_ATTEMPTS) {
                    vaultKeyManager.clearInMemoryKey()
                    _uiState.value = _uiState.value.copy(
                        isLocked = true,
                        error = "Too many failed attempts. Master password required.",
                        currentInput = "",
                        failCount = newFailCount
                    )
                } else {
                    val remaining = MAX_ATTEMPTS - newFailCount
                    _uiState.value = _uiState.value.copy(
                        error = "Incorrect PIN. $remaining attempts remaining.",
                        currentInput = "",
                        failCount = newFailCount
                    )
                }
            }
        }
    }
}
