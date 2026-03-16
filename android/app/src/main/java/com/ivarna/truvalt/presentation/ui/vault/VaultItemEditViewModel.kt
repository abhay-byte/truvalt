package com.ivarna.truvalt.presentation.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl
import com.ivarna.truvalt.domain.model.SyncStatus
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun loadItem(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val item = vaultRepository.getItemById(itemId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    item = item
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load item"
                )
            }
        }
    }

    fun saveLoginItem(
        id: String?,
        name: String,
        url: String,
        username: String,
        password: String,
        notes: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            try {
                val loginData = "$url|||${username}|||${password}|||${notes}"
                val encryptedData = loginData.toByteArray(Charsets.UTF_8)

                val item = VaultItem(
                    id = id ?: java.util.UUID.randomUUID().toString(),
                    type = VaultItemType.LOGIN,
                    name = name,
                    encryptedData = encryptedData,
                    syncStatus = SyncStatus.PENDING_UPLOAD,
                    updatedAt = System.currentTimeMillis()
                )

                vaultRepository.saveItem(item)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isSaved = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save item"
                )
            }
        }
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
