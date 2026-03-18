package com.ivarna.truvalt.presentation.ui.settings

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

data class SettingsUiState(
    val isBiometricEnabled: Boolean = false,
    val autoLockTimeout: Long = 300000L,
    val clipboardTimeout: Long = 30L,
    val themeMode: String = "system",
    val isLocalOnly: Boolean = false,
    val serverUrl: String? = null,
    val lastSyncTime: Long = 0L,
    val isLoading: Boolean = false
) {
    val autoLockLabel: String
        get() = when (autoLockTimeout) {
            0L -> "Immediately"
            60000L -> "1 minute"
            300000L -> "5 minutes"
            900000L -> "15 minutes"
            3600000L -> "1 hour"
            -1L -> "Never"
            else -> "5 minutes"
        }
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: TruvaltPreferences,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository,
    val biometricHelper: com.ivarna.truvalt.core.biometric.BiometricHelper,
    val pinStorage: com.ivarna.truvalt.core.pin.PinStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            _uiState.value = SettingsUiState(
                isBiometricEnabled = preferences.isBiometricEnabled.first(),
                autoLockTimeout = preferences.autoLockTimeout.first(),
                clipboardTimeout = preferences.clipboardTimeout.first(),
                themeMode = preferences.themeMode.first(),
                isLocalOnly = preferences.isLocalOnly.first(),
                serverUrl = preferences.serverUrl.first(),
                lastSyncTime = preferences.lastSyncTime.first(),
                isLoading = false
            )
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setBiometricEnabled(enabled)
            _uiState.value = _uiState.value.copy(isBiometricEnabled = enabled)
        }
    }

    fun setAutoLockTimeout(timeout: Long) {
        viewModelScope.launch {
            preferences.setAutoLockTimeout(timeout)
            _uiState.value = _uiState.value.copy(autoLockTimeout = timeout)
        }
    }

    fun setClipboardTimeout(timeout: Long) {
        viewModelScope.launch {
            preferences.setClipboardTimeout(timeout)
            _uiState.value = _uiState.value.copy(clipboardTimeout = timeout)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            preferences.setThemeMode(theme)
            _uiState.value = _uiState.value.copy(themeMode = theme)
        }
    }

    fun setLocalOnly(localOnly: Boolean) {
        viewModelScope.launch {
            syncRepository.setLocalOnly(localOnly)
            _uiState.value = _uiState.value.copy(isLocalOnly = localOnly)
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            syncRepository.sync()
            _uiState.value = _uiState.value.copy(lastSyncTime = System.currentTimeMillis())
        }
    }

    fun lockVault() {
        viewModelScope.launch {
            authRepository.lockVault()
        }
    }

    fun deleteVault() {
        viewModelScope.launch {
            preferences.clearVaultData()
        }
    }
}
