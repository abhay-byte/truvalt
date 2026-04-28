package com.ivarna.truvalt.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.data.repository.AuthRepositoryImpl
import com.ivarna.truvalt.domain.repository.AuthRepository
import com.ivarna.truvalt.domain.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DateFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AccountProfileUiState(
    val displayName: String,
    val email: String,
    val photoUrl: String? = null,
    val providerLabel: String = "Account",
    val uid: String,
    val emailVerified: Boolean = false,
    val createdAt: String? = null,
    val lastSignInAt: String? = null
)

data class SettingsUiState(
    val isBiometricEnabled: Boolean = false,
    val canUseBiometricUnlock: Boolean = false,
    val autoLockTimeout: Long = 300000L,
    val clipboardTimeout: Long = 30L,
    val themeMode: String = "system",
    val isLocalOnly: Boolean = false,
    val lastSyncTime: Long = 0L,
    val accountProfile: AccountProfileUiState? = null,
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

sealed interface DeleteAccountState {
    data object Idle : DeleteAccountState
    data object Loading : DeleteAccountState
    data object Success : DeleteAccountState
    data class Error(val message: String) : DeleteAccountState
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: TruvaltPreferences,
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository,
    private val firebaseAuth: FirebaseAuth,
    val biometricHelper: com.ivarna.truvalt.core.biometric.BiometricHelper,
    val pinStorage: com.ivarna.truvalt.core.pin.PinStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _deleteAccountState = MutableStateFlow<DeleteAccountState>(DeleteAccountState.Idle)
    val deleteAccountState: StateFlow<DeleteAccountState> = _deleteAccountState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            _uiState.value = SettingsUiState(
                isBiometricEnabled = preferences.isBiometricEnabled.first(),
                canUseBiometricUnlock = preferences.encryptedVaultKey.first() != null,
                autoLockTimeout = preferences.autoLockTimeout.first(),
                clipboardTimeout = preferences.clipboardTimeout.first(),
                themeMode = preferences.themeMode.first(),
                isLocalOnly = preferences.isLocalOnly.first(),
                lastSyncTime = preferences.lastSyncTime.first(),
                accountProfile = firebaseAuth.currentUser?.toAccountProfile(),
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

    fun logout() {
        viewModelScope.launch {
            authRepository.lockVault()
            firebaseAuth.signOut()
            preferences.setFirebaseIdToken(null)
            preferences.setFirebaseRefreshToken(null)
            preferences.setFirebaseUserId(null)
            preferences.setUserEmail(null)
            preferences.setAuthKeyHash(null)
            _uiState.value = _uiState.value.copy(accountProfile = null)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountState.value = DeleteAccountState.Loading
            val repo = authRepository as? AuthRepositoryImpl
                ?: run {
                    _deleteAccountState.value = DeleteAccountState.Error("Account deletion is only available in cloud mode.")
                    return@launch
                }
            repo.deleteAccount()
                .onSuccess {
                    _uiState.value = _uiState.value.copy(accountProfile = null)
                    _deleteAccountState.value = DeleteAccountState.Success
                }
                .onFailure { e ->
                    _deleteAccountState.value = DeleteAccountState.Error(e.message ?: "Account deletion failed. Please try again.")
                }
        }
    }

    fun resetDeleteAccountState() {
        _deleteAccountState.value = DeleteAccountState.Idle
    }

    fun deleteVault() {
        viewModelScope.launch {
            preferences.clearVaultData()
        }
    }

    private fun FirebaseUser.toAccountProfile(): AccountProfileUiState {
        val providerIds = providerData
            .mapNotNull { it.providerId }
            .filterNot { it == "firebase" }
            .distinct()

        val providerLabel = when {
            providerIds.contains("google.com") -> "Google account"
            providerIds.contains("password") -> "Email account"
            providerIds.isNotEmpty() -> providerIds.joinToString()
            else -> "Account"
        }

        fun formatTimestamp(timestamp: Long): String? {
            if (timestamp <= 0L) return null
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(timestamp)
        }

        val bestName = displayName
            ?.takeIf { it.isNotBlank() }
            ?: email?.substringBefore("@")
            ?: "Signed in"

        return AccountProfileUiState(
            displayName = bestName,
            email = email ?: "No email available",
            photoUrl = photoUrl?.toString(),
            providerLabel = providerLabel,
            uid = uid,
            emailVerified = isEmailVerified,
            createdAt = formatTimestamp(metadata?.creationTimestamp ?: 0L),
            lastSignInAt = formatTimestamp(metadata?.lastSignInTimestamp ?: 0L)
        )
    }
}
