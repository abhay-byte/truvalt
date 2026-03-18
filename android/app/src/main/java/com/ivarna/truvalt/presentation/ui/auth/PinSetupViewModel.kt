package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.pin.PinHasher
import com.ivarna.truvalt.core.pin.PinStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class PinSetupStep {
    ENTER_PIN,
    CONFIRM_PIN
}

data class PinSetupUiState(
    val step: PinSetupStep = PinSetupStep.ENTER_PIN,
    val currentInput: String = "",
    val error: String? = null,
    val isComplete: Boolean = false
)

@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val pinHasher: PinHasher,
    private val pinStorage: PinStorage
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()
    
    private var firstPin: String = ""
    
    fun onDigitEntered(digit: String) {
        val current = _uiState.value.currentInput
        if (current.length < 8) {
            _uiState.value = _uiState.value.copy(
                currentInput = current + digit,
                error = null
            )
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
        val current = _uiState.value.currentInput
        
        if (current.length < 4) {
            _uiState.value = _uiState.value.copy(
                error = "PIN must be at least 4 digits"
            )
            return
        }
        
        when (_uiState.value.step) {
            PinSetupStep.ENTER_PIN -> {
                firstPin = current
                _uiState.value = PinSetupUiState(
                    step = PinSetupStep.CONFIRM_PIN,
                    currentInput = "",
                    error = null
                )
            }
            PinSetupStep.CONFIRM_PIN -> {
                if (current == firstPin) {
                    savePinAndComplete(current)
                } else {
                    _uiState.value = PinSetupUiState(
                        step = PinSetupStep.ENTER_PIN,
                        currentInput = "",
                        error = "PINs do not match"
                    )
                    firstPin = ""
                }
            }
        }
    }
    
    private fun savePinAndComplete(pin: String) {
        viewModelScope.launch {
            val salt = pinHasher.generateSalt()
            val hash = pinHasher.hashPin(pin, salt)
            pinStorage.saveHash(hash, salt)
            _uiState.value = _uiState.value.copy(isComplete = true)
        }
    }
}
