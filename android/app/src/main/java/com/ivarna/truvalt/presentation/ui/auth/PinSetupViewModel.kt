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
    val pin: String = "",
    val confirmedPin: String = "",
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val maxPinLength: Int = 8,
    val isComplete: Boolean = false
)

@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val pinHasher: PinHasher,
    private val pinStorage: PinStorage
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()
    
    fun onDigitEntered(digit: Int) {
        val state = _uiState.value
        if (state.pin.length >= state.maxPinLength) return
        
        val newPin = state.pin + digit.toString()
        
        if (state.step == PinSetupStep.ENTER_PIN) {
            if (newPin.length == state.maxPinLength) {
                advanceToConfirm(newPin)
            } else {
                _uiState.value = state.copy(pin = newPin, hasError = false)
            }
        } else if (state.step == PinSetupStep.CONFIRM_PIN) {
            val updated = state.pin + digit.toString()
            if (updated.length == state.confirmedPin.length) {
                verifyAndSave(updated)
            } else {
                _uiState.value = state.copy(pin = updated, hasError = false)
            }
        }
    }
    
    fun onBackspace() {
        val state = _uiState.value
        if (state.pin.isNotEmpty()) {
            _uiState.value = state.copy(pin = state.pin.dropLast(1), hasError = false)
        }
    }
    
    fun onConfirmStep() {
        val state = _uiState.value
        if (state.step == PinSetupStep.ENTER_PIN && state.pin.length >= 4) {
            advanceToConfirm(state.pin)
        }
    }
    
    private fun advanceToConfirm(pin: String) {
        _uiState.value = _uiState.value.copy(
            step = PinSetupStep.CONFIRM_PIN,
            confirmedPin = pin,
            pin = "",
            hasError = false
        )
    }
    
    private fun verifyAndSave(confirmEntry: String) {
        val state = _uiState.value
        if (confirmEntry == state.confirmedPin) {
            viewModelScope.launch {
                val salt = pinHasher.generateSalt()
                val hash = pinHasher.hashPin(confirmEntry, salt)
                pinStorage.saveHash(hash, salt)
                _uiState.value = state.copy(isComplete = true)
            }
        } else {
            _uiState.value = PinSetupUiState(
                hasError = true,
                errorMessage = "PINs do not match. Try again.",
                step = PinSetupStep.ENTER_PIN,
                pin = "",
                confirmedPin = ""
            )
        }
    }
}
