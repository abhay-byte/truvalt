package com.ivarna.truvalt.presentation.ui.vault

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.domain.model.*
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class VaultItemEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null,
    val item: VaultItem? = null
)

@HiltViewModel
class VaultItemEditViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultItemEditUiState())
    val uiState: StateFlow<VaultItemEditUiState> = _uiState.asStateFlow()

    private val _selectedType = MutableStateFlow<VaultItemType>(VaultItemType.Login)
    val selectedType: StateFlow<VaultItemType> = _selectedType.asStateFlow()

    private val _itemName = MutableStateFlow("")
    val itemName: StateFlow<String> = _itemName.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private val _loginData = MutableStateFlow(LoginItemData())
    val loginData: StateFlow<LoginItemData> = _loginData.asStateFlow()

    private val _passphraseData = MutableStateFlow(PassphraseItemData())
    val passphraseData: StateFlow<PassphraseItemData> = _passphraseData.asStateFlow()

    private val _secureNoteData = MutableStateFlow(SecureNoteItemData())
    val secureNoteData: StateFlow<SecureNoteItemData> = _secureNoteData.asStateFlow()

    private val _securityCodeData = MutableStateFlow(SecurityCodeItemData())
    val securityCodeData: StateFlow<SecurityCodeItemData> = _securityCodeData.asStateFlow()

    private val _creditCardData = MutableStateFlow(CreditCardItemData())
    val creditCardData: StateFlow<CreditCardItemData> = _creditCardData.asStateFlow()

    private val _identityData = MutableStateFlow(IdentityItemData())
    val identityData: StateFlow<IdentityItemData> = _identityData.asStateFlow()

    private val _passkeyData = MutableStateFlow(PasskeyItemData())
    val passkeyData: StateFlow<PasskeyItemData> = _passkeyData.asStateFlow()

    fun setItemType(type: VaultItemType) {
        _selectedType.value = type
    }

    fun updateItemName(name: String) {
        _itemName.value = name
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun updateLoginData(data: LoginItemData) {
        _loginData.value = data
    }

    fun updatePassphraseData(data: PassphraseItemData) {
        _passphraseData.value = data
    }

    fun updateSecureNoteData(data: SecureNoteItemData) {
        _secureNoteData.value = data
    }

    fun updateSecurityCodeData(data: SecurityCodeItemData) {
        _securityCodeData.value = data
    }

    fun updateCreditCardData(data: CreditCardItemData) {
        _creditCardData.value = data
    }

    fun updateIdentityData(data: IdentityItemData) {
        _identityData.value = data
    }

    fun updatePasskeyData(data: PasskeyItemData) {
        _passkeyData.value = data
    }

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val item = vaultRepository.getItemById(itemId)
                if (item != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        item = item
                    )
                    _itemName.value = item.name
                    _isFavorite.value = item.favorite
                    _selectedType.value = VaultItemType.fromId(item.type)
                    
                    // Decode encrypted data based on type
                    val dataString = String(item.encryptedData, Charsets.UTF_8)
                    when (_selectedType.value) {
                        is VaultItemType.Login -> _loginData.value = Json.decodeFromString(dataString)
                        is VaultItemType.Passphrase -> _passphraseData.value = Json.decodeFromString(dataString)
                        is VaultItemType.SecureNote -> _secureNoteData.value = Json.decodeFromString(dataString)
                        is VaultItemType.SecurityCode -> _securityCodeData.value = Json.decodeFromString(dataString)
                        is VaultItemType.CreditCard -> _creditCardData.value = Json.decodeFromString(dataString)
                        is VaultItemType.Identity -> _identityData.value = Json.decodeFromString(dataString)
                        is VaultItemType.Passkey -> _passkeyData.value = Json.decodeFromString(dataString)
                        else -> {}
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Item not found"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load item"
                )
            }
        }
    }

    fun saveItem() {
        viewModelScope.launch {
            Log.d("VaultItemEdit", "=== SAVE ITEM START ===")
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                Log.d("VaultItemEdit", "Item type: ${_selectedType.value}")
                Log.d("VaultItemEdit", "Item name: ${_itemName.value}")
                
                val encryptedData = when (_selectedType.value) {
                    is VaultItemType.Login -> Json.encodeToString(_loginData.value)
                    is VaultItemType.Passphrase -> Json.encodeToString(_passphraseData.value)
                    is VaultItemType.SecureNote -> Json.encodeToString(_secureNoteData.value)
                    is VaultItemType.SecurityCode -> Json.encodeToString(_securityCodeData.value)
                    is VaultItemType.CreditCard -> Json.encodeToString(_creditCardData.value)
                    is VaultItemType.Identity -> Json.encodeToString(_identityData.value)
                    is VaultItemType.Passkey -> Json.encodeToString(_passkeyData.value)
                    is VaultItemType.Custom -> "{}"
                }.toByteArray(Charsets.UTF_8)

                Log.d("VaultItemEdit", "Encrypted data size: ${encryptedData.size} bytes")

                val item = VaultItem(
                    id = _uiState.value.item?.id ?: java.util.UUID.randomUUID().toString(),
                    type = _selectedType.value.id,
                    name = _itemName.value.trim(),
                    encryptedData = encryptedData,
                    favorite = _isFavorite.value,
                    syncStatus = SyncStatus.PENDING_UPLOAD,
                    updatedAt = System.currentTimeMillis()
                )

                Log.d("VaultItemEdit", "Calling vaultRepository.saveItem()...")
                vaultRepository.saveItem(item)
                Log.d("VaultItemEdit", "Save successful!")
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                Log.e("VaultItemEdit", "=== SAVE FAILED ===")
                Log.e("VaultItemEdit", "Error type: ${e.javaClass.simpleName}")
                Log.e("VaultItemEdit", "Error message: ${e.message}")
                Log.e("VaultItemEdit", "Stack trace:", e)
                
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save item"
                )
            }
        }
    }

    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        if (_itemName.value.isBlank()) errors.add("Item name is required")
        
        when (_selectedType.value) {
            is VaultItemType.Login -> {
                if (_loginData.value.username.isBlank()) errors.add("Username is required")
                if (_loginData.value.password.isBlank()) errors.add("Password is required")
            }
            is VaultItemType.Passphrase -> {
                if (_passphraseData.value.passphrase.isBlank()) errors.add("Passphrase is required")
            }
            is VaultItemType.SecureNote -> {
                if (_secureNoteData.value.content.isBlank()) errors.add("Note content is required")
            }
            is VaultItemType.SecurityCode -> {
                if (_securityCodeData.value.code.isBlank()) errors.add("Security code is required")
            }
            is VaultItemType.CreditCard -> {
                if (_creditCardData.value.cardNumber.isBlank()) errors.add("Card number is required")
            }
            is VaultItemType.Identity -> {
                if (_identityData.value.firstName.isBlank() && _identityData.value.lastName.isBlank()) {
                    errors.add("At least first or last name is required")
                }
            }
            else -> {}
        }
        return errors
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            try {
                vaultRepository.softDeleteItem(itemId)
                _uiState.value = _uiState.value.copy(isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to delete item"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
