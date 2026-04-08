package com.ivarna.truvalt.presentation.ui.vault

import android.net.Uri
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class VaultUiState(
    val isLoading: Boolean = false,
    val items: List<VaultItemUi> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val filter: String? = null,
    val totalItemCount: Int = 0,
    val health: VaultHealthSummary = VaultHealthSummary()
)

data class VaultHealthSummary(
    val score: Int = 100,
    val analyzedCount: Int = 0,
    val weakCount: Int = 0,
    val reusedCount: Int = 0,
    val oldCount: Int = 0,
    val secureCount: Int = 0
)

@HiltViewModel
class VaultViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    private val seedDataInserter: SeedDataInserter,
    private val preferences: TruvaltPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    private var allItems: List<VaultItem> = emptyList()

    init {
        viewModelScope.launch {
            val isFirstLaunch = preferences.isFirstLaunch.first()
            if (isFirstLaunch) {
                Log.d("VaultViewModel", "First launch detected, inserting seed data")
                seedDataInserter.insertSeedData()
                preferences.setFirstLaunch(false)
            }
            observeItems()
        }
    }

    private suspend fun observeItems() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        vaultRepository.getAllItems().collectLatest { items ->
            allItems = items.sortedByDescending { it.updatedAt }
            publishUi()
        }
    }

    fun setFilter(filter: String?) {
        _uiState.value = _uiState.value.copy(filter = filter)
        publishUi()
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        publishUi()
    }

    private fun publishUi() {
        val query = _uiState.value.searchQuery.trim()
        val filter = _uiState.value.filter
        val mappedItems = allItems.map { it.toUi() }
        val filteredItems = mappedItems.filter { item ->
            matchesFilter(item, filter) && matchesQuery(item, query)
        }

        _uiState.value = _uiState.value.copy(
            isLoading = false,
            items = filteredItems,
            totalItemCount = allItems.size,
            health = buildHealthSummary(allItems),
            error = null
        )
    }

    private fun matchesFilter(item: VaultItemUi, filter: String?): Boolean {
        return when (filter) {
            null -> true
            "favorites" -> item.isFavorite
            else -> item.type == filter
        }
    }

    private fun matchesQuery(item: VaultItemUi, query: String): Boolean {
        if (query.isBlank()) return true
        val loweredQuery = query.lowercase()
        return listOf(item.name, item.subtitle, item.typeLabel, item.username, item.url)
            .filter { it.isNotBlank() }
            .any { it.lowercase().contains(loweredQuery) }
    }

    private fun buildHealthSummary(items: List<VaultItem>): VaultHealthSummary {
        val loginItems = items.filter { it.type == VaultItemType.Login.id }
        if (loginItems.isEmpty()) {
            return VaultHealthSummary()
        }

        var weak = 0
        var old = 0
        val passwordCounts = mutableMapOf<String, Int>()
        val passwordAgeLimit = System.currentTimeMillis() - (180L * 24 * 60 * 60 * 1000)

        loginItems.forEach { item ->
            val password = extractPassword(item).trim()
            if (password.isEmpty()) return@forEach

            val uniqueChars = password.toSet().size
            when {
                password.length < 8 || uniqueChars < 4 -> weak++
                item.updatedAt < passwordAgeLimit -> old++
                else -> passwordCounts[password] = (passwordCounts[password] ?: 0) + 1
            }
        }

        val analyzedCount = loginItems.count { extractPassword(it).isNotBlank() }
        val reusedCount = passwordCounts.values.count { it > 1 }
        val secureCount = passwordCounts.values.count { it == 1 }
        val score = if (analyzedCount > 0) {
            ((secureCount.toFloat() / analyzedCount.toFloat()) * 100f).toInt()
        } else {
            100
        }

        return VaultHealthSummary(
            score = score.coerceIn(0, 100),
            analyzedCount = analyzedCount,
            weakCount = weak,
            reusedCount = reusedCount,
            oldCount = old,
            secureCount = secureCount
        )
    }

    private fun extractPassword(item: VaultItem): String {
        val json = item.payloadJson()
        return firstNonBlank(
            json?.optString("password").orEmpty(),
            item.payloadString().split("|||").getOrNull(2).orEmpty()
        )
    }

    private fun VaultItem.toUi(): VaultItemUi {
        val payload = payloadJson()
        val typeLabel = VaultItemType.fromId(type).displayName
        val legacyParts = payloadString().split("|||")
        
        return VaultItemUi(
            id = id,
            name = name,
            type = type,
            typeLabel = typeLabel,
            username = firstNonBlank(
                payload.stringFor("username", "email"),
                legacyParts.getOrNull(1).orEmpty()
            ),
            url = firstNonBlank(
                payload.stringFor("url"),
                legacyParts.getOrNull(0).orEmpty()
            ),
            subtitle = buildSubtitle(type, payload, payloadString()),
            isFavorite = favorite,
            totpSeed = payload?.optString("totpSeed")?.takeIf { it.isNotBlank() }
        )
    }

    private fun buildSubtitle(type: String, payload: JSONObject?, rawPayload: String): String {
        val legacyParts = rawPayload.split("|||")
        return when (type) {
            VaultItemType.Login.id -> firstNonBlank(
                payload.stringFor("username", "email"),
                legacyParts.getOrNull(1).orEmpty(),
                payload.stringFor("url").let(::hostFromUrl),
                legacyParts.getOrNull(0).orEmpty().let(::hostFromUrl),
                payload.stringFor("notes")
            )
            VaultItemType.Passkey.id -> firstNonBlank(
                payload.stringFor("username"),
                payload.stringFor("service"),
                payload.stringFor("rpId", "rp_id"),
                payload.stringFor("notes")
            )
            VaultItemType.Passphrase.id -> firstNonBlank(
                payload.stringFor("context"),
                payload.stringFor("passphrase", "phrase").let(::wordCountLabel),
                payload.stringFor("notes")
            )
            VaultItemType.SecureNote.id -> firstNonBlank(
                payload.stringFor("content").let(::compactPreview)
            )
            VaultItemType.SecurityCode.id -> firstNonBlank(
                payload.stringFor("issuer", "account", "codeType", "type"),
                payload.stringFor("notes")
            )
            VaultItemType.CreditCard.id -> firstNonBlank(
                maskCardNumber(payload.stringFor("cardNumber", "number")),
                payload.stringFor("cardholderName", "name"),
                payload.stringFor("notes")
            )
            VaultItemType.Identity.id -> firstNonBlank(
                payload.stringFor("email"),
                payload.stringFor("phone"),
                payload.fullName(),
                payload.stringFor("notes")
            )
            else -> firstNonBlank(
                payload.stringFor("notes"),
                compactPreview(rawPayload)
            )
        }
    }

    private fun VaultItem.payloadString(): String = String(encryptedData, Charsets.UTF_8)

    private fun VaultItem.payloadJson(): JSONObject? {
        return runCatching { JSONObject(payloadString()) }.getOrNull()
    }

    private fun JSONObject?.stringFor(vararg keys: String): String {
        if (this == null) return ""
        return keys.firstNotNullOfOrNull { key ->
            optString(key).trim().takeIf { it.isNotBlank() }
        }.orEmpty()
    }

    private fun JSONObject?.fullName(): String {
        if (this == null) return ""
        val first = optString("firstName").trim()
        val last = optString("lastName").trim()
        return firstNonBlank(
            listOf(first, last).filter { it.isNotBlank() }.joinToString(" ").trim(),
            optString("name").trim()
        )
    }

    private fun firstNonBlank(vararg values: String): String {
        return values.firstOrNull { it.isNotBlank() }.orEmpty()
    }

    private fun compactPreview(text: String, maxLength: Int = 42): String {
        val normalized = text
            .replace("\n", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (normalized.length <= maxLength) return normalized
        return normalized.take(maxLength - 1).trimEnd() + "…"
    }

    private fun wordCountLabel(passphrase: String): String {
        if (passphrase.isBlank()) return ""
        val words = passphrase
            .trim()
            .split(Regex("[\\s-]+"))
            .count { it.isNotBlank() }
        return if (words > 0) "$words word passphrase" else ""
    }

    private fun maskCardNumber(cardNumber: String): String {
        val digits = cardNumber.filter { it.isDigit() }
        if (digits.length < 4) return ""
        return "\u2022\u2022\u2022\u2022 ${digits.takeLast(4)}"
    }

    private fun hostFromUrl(url: String): String {
        if (url.isBlank()) return ""
        return runCatching {
            Uri.parse(url).host?.removePrefix("www.")?.takeIf { it.isNotBlank() }
        }.getOrNull().orEmpty()
    }
}
