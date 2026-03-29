package com.ivarna.truvalt.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServerSetupUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class ServerSetupViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val preferences: TruvaltPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServerSetupUiState())
    val uiState: StateFlow<ServerSetupUiState> = _uiState.asStateFlow()

    /**
     * @param serverUrl  Self-hosted backend URL (blank if using Firebase Cloud).
     * @param useLocalOnly  True for no-sync offline mode.
     * @param useFirebaseCloud  True when the user tapped the Firebase shortcut buttons.
     *   Skips URL validation, sets cloud mode, and marks first-launch as done.
     */
    fun saveServerConfig(
        serverUrl: String,
        useLocalOnly: Boolean,
        useFirebaseCloud: Boolean = false
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                when {
                    useLocalOnly -> {
                        syncRepository.setLocalOnly(true)
                    }
                    useFirebaseCloud -> {
                        // No server URL needed — Firebase handles everything
                        syncRepository.setLocalOnly(false)
                        syncRepository.setServerUrl("")
                    }
                    else -> {
                        if (serverUrl.isBlank()) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Please enter a server URL"
                            )
                            return@launch
                        }
                        syncRepository.setServerUrl(serverUrl)
                        syncRepository.setLocalOnly(false)
                    }
                }
                // Mark onboarding as done so Splash never loops back here
                preferences.setFirstLaunch(false)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to save server config"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
