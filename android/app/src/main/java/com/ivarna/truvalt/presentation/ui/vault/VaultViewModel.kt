package com.ivarna.truvalt.presentation.ui.vault

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.data.local.SeedDataInserter
import com.ivarna.truvalt.data.preferences.TruvaltPreferences
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val vaultRepository: VaultRepository,
    private val seedDataInserter: SeedDataInserter,
    private val preferences: TruvaltPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val isFirstLaunch = preferences.isFirstLaunch.first()
            if (isFirstLaunch) {
                Log.d("VaultViewModel", "First launch detected, inserting seed data")
                seedDataInserter.insertSeedData()
                preferences.setFirstLaunch(false)
            }
            loadItems()
        }
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
                else -> vaultRepository.getItemsByType(filter).collect { items ->
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
            type = type,
            subtitle = "",
            isFavorite = favorite
        )
    }
}
