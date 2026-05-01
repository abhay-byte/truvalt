package com.ivarna.truvalt.presentation.ui.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivarna.truvalt.core.crypto.VaultKeyManager
import com.ivarna.truvalt.core.utils.ImportExportService
import com.ivarna.truvalt.data.local.dao.TagDao
import com.ivarna.truvalt.domain.model.VaultItemTag
import com.ivarna.truvalt.domain.repository.VaultRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import javax.inject.Inject

data class ExportUiState(
    val isExporting: Boolean = false,
    val isComplete: Boolean = false,
    val error: String? = null,
    val progress: Int = 0,
    val itemCount: Int = 0,
    val folderCount: Int = 0,
    val tagCount: Int = 0,
    val selectedFormat: ImportExportService.ExportFormat = ImportExportService.ExportFormat.TRUVALT_ENCRYPTED
)

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val vaultRepository: VaultRepository,
    private val tagDao: TagDao,
    private val importExportService: ImportExportService,
    private val vaultKeyManager: VaultKeyManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    init {
        loadCounts()
    }

    private fun loadCounts() {
        viewModelScope.launch {
            try {
                val items = vaultRepository.getAllItems().first()
                val folders = vaultRepository.getAllFolders().first()
                val tags = vaultRepository.getAllTags().first()
                _uiState.value = _uiState.value.copy(
                    itemCount = items.size,
                    folderCount = folders.size,
                    tagCount = tags.size
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to load vault data")
            }
        }
    }

    fun setFormat(format: ImportExportService.ExportFormat) {
        _uiState.value = _uiState.value.copy(selectedFormat = format)
    }

    fun exportToUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val state = _uiState.value

            // For encrypted export, vault must be unlocked
            if (state.selectedFormat == ImportExportService.ExportFormat.TRUVALT_ENCRYPTED) {
                if (vaultKeyManager.getInMemoryKey() == null) {
                    _uiState.value = state.copy(error = "Vault must be unlocked to export")
                    return@launch
                }
            }

            _uiState.value = state.copy(isExporting = true, progress = 10, error = null)

            try {
                val items = withContext(Dispatchers.IO) {
                    vaultRepository.getAllItems().first()
                }
                _uiState.value = _uiState.value.copy(progress = 30)

                val folders = withContext(Dispatchers.IO) {
                    vaultRepository.getAllFolders().first()
                }
                _uiState.value = _uiState.value.copy(progress = 40)

                val tags = withContext(Dispatchers.IO) {
                    vaultRepository.getAllTags().first()
                }
                _uiState.value = _uiState.value.copy(progress = 50)

                val itemTagMappings = withContext(Dispatchers.IO) {
                    tagDao.getAllItemTagMappings().map {
                        VaultItemTag(it.itemId, it.tagId)
                    }
                }
                _uiState.value = _uiState.value.copy(progress = 60)

                val exportData = ImportExportService.VaultExportData(
                    items = items,
                    folders = folders,
                    tags = tags,
                    itemTags = itemTagMappings
                )

                val output = when (state.selectedFormat) {
                    ImportExportService.ExportFormat.TRUVALT_ENCRYPTED -> {
                        val vaultKey = vaultKeyManager.getInMemoryKey()
                            ?: throw IllegalStateException("Vault key not available")
                        importExportService.exportToEncryptedTruvalt(exportData, vaultKey)
                    }
                    ImportExportService.ExportFormat.JSON -> {
                        importExportService.exportToJson(exportData)
                    }
                    ImportExportService.ExportFormat.CSV -> {
                        importExportService.exportToCsv(exportData)
                    }
                }
                _uiState.value = _uiState.value.copy(progress = 80)

                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        OutputStreamWriter(outputStream).use { writer ->
                            writer.write(output)
                        }
                    } ?: throw Exception("Could not open output stream")
                }

                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    isComplete = true,
                    progress = 100
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    error = e.message ?: "Export failed",
                    progress = 0
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetComplete() {
        _uiState.value = _uiState.value.copy(isComplete = false)
    }
}
