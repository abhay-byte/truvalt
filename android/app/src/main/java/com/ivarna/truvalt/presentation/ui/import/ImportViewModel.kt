package com.ivarna.truvalt.presentation.ui.import

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.utils.ImportExportService
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

data class ImportUiState(
    val isImporting: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null,
    val progress: Int = 0,
    val previewItems: List<Pair<String, Boolean>> = emptyList()
)

@HiltViewModel
class ImportViewModel @Inject constructor(
    private val importExportService: ImportExportService,
    private val vaultRepository: VaultRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    private var pendingItems: List<com.ivarna.truvalt.domain.model.VaultItem> = emptyList()

    fun importFile(context: Context, uri: Uri, format: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true, progress = 10)

            try {
                val content = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            reader.readText()
                        }
                    } ?: throw Exception("Could not read file")
                }

                _uiState.value = _uiState.value.copy(progress = 30)

                val importFormat = when (format) {
                    "bitwarden_json" -> ImportExportService.ImportFormat.BITWARDEN_JSON
                    "lastpass_csv" -> ImportExportService.ImportFormat.LASTPASS_CSV
                    "chrome_csv" -> ImportExportService.ImportFormat.CHROME_CSV
                    "firefox_csv" -> ImportExportService.ImportFormat.FIREFOX_CSV
                    "generic_csv" -> ImportExportService.ImportFormat.GENERIC_CSV
                    else -> throw Exception("Unknown format")
                }

                val result = importExportService.importData(content, importFormat)

                when (result) {
                    is ImportExportService.ImportResult.Success -> {
                        pendingItems = result.items
                        _uiState.value = _uiState.value.copy(
                            isImporting = false,
                            progress = 0,
                            previewItems = result.items.map { it.name to true } +
                                    result.errors.map { it to false }
                        )
                    }
                    is ImportExportService.ImportResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isImporting = false,
                            error = result.message,
                            progress = 0
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    error = e.message ?: "Import failed",
                    progress = 0
                )
            }
        }
    }

    fun confirmImport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isImporting = true, progress = 50)

            try {
                pendingItems.forEachIndexed { index, item ->
                    vaultRepository.saveItem(item)
                    _uiState.value = _uiState.value.copy(
                        progress = 50 + ((index + 1) * 50 / pendingItems.size)
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    isComplete = true,
                    progress = 100
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isImporting = false,
                    error = e.message ?: "Import failed",
                    progress = 0
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
