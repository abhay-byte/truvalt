package com.ivarna.truvalt.presentation.ui.health

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.domain.model.VaultItem
import com.ivarna.truvalt.domain.model.VaultItemType
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.security.MessageDigest
import javax.inject.Inject

data class HealthUiState(
    val isLoading: Boolean = false,
    val healthScore: Int = 100,
    val totalItems: Int = 0,
    val breachedCount: Int = 0,
    val weakCount: Int = 0,
    val reusedCount: Int = 0,
    val oldCount: Int = 0,
    val secureCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        analyzeVault()
    }

    private fun analyzeVault() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val items = vaultRepository.getAllItems().first()
                val passwordItems = items.filter { it.type == "login" }

                var weak = 0
                var reused = 0
                var old = 0
                var secure = 0

                val passwordCounts = mutableMapOf<String, Int>()
                val oneYearAgo = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)

                passwordItems.forEach { item ->
                    val data = String(item.encryptedData, Charsets.UTF_8)
                    val parts = data.split("|||")
                    val password = parts.getOrNull(2) ?: ""

                    if (password.isNotEmpty()) {
                        val uniqueChars = password.toSet().size
                        val length = password.length

                        when {
                            length < 8 || uniqueChars < 4 -> weak++
                            item.updatedAt < oneYearAgo -> old++
                            else -> {
                                passwordCounts[password] = (passwordCounts[password] ?: 0) + 1
                                if (passwordCounts[password] == 1) secure++
                            }
                        }
                    }
                }

                reused = passwordCounts.values.count { it > 1 }

                val total = passwordItems.size
                val score = if (total > 0) {
                    ((secure.toFloat() / total) * 100).toInt()
                } else {
                    100
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    healthScore = score,
                    totalItems = total,
                    weakCount = weak,
                    reusedCount = reused,
                    oldCount = old,
                    secureCount = secure
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun sha1hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
