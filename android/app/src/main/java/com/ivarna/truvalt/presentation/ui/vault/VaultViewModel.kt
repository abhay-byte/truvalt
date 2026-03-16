package com.ivarna.truvalt.presentation.ui.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VaultUiState(
    val isLoading: Boolean = false,
    val items: List<VaultItemUi> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val filter: String? = null
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            vaultRepository.getAllItems().collect { items ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = items.map { it.toUi() }
                )
            }
        }
    }

    fun setFilter(filter: String?) {
        _uiState.value = _uiState.value.copy(filter = filter)
        viewModelScope.launch {
            when (filter) {
                null -> vaultRepository.getAllItems().collect { items ->
                    _uiState.value = _uiState.value.copy(items = items.map { it.toUi() })
                }
                "favorites" -> vaultRepository.getFavoriteItems().collect { items ->
                    _uiState.value = _uiState.value.copy(items = items.map { it.toUi() })
                }
                else -> vaultRepository.getItemsByType(VaultItemType.valueOf(filter)).collect { items ->
                    _uiState.value = _uiState.value.copy(items = items.map { it.toUi() })
                }
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        if (query.isNotBlank()) {
            viewModelScope.launch {
                vaultRepository.searchItems(query).collect { items ->
                    _uiState.value = _uiState.value.copy(items = items.map { it.toUi() })
                }
            }
        } else {
            loadItems()
        }
    }

    private fun VaultItem.toUi(): VaultItemUi {
        return VaultItemUi(
            id = id,
            name = name,
            type = type.name,
            subtitle = "", // Would need to decrypt to get username
            isFavorite = favorite
        )
    }
}
