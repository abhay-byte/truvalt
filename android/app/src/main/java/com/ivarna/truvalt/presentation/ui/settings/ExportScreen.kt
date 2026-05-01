package com.ivarna.truvalt.presentation.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.core.utils.ImportExportService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val mimeType = when (uiState.selectedFormat) {
        ImportExportService.ExportFormat.TRUVALT_ENCRYPTED -> "application/octet-stream"
        ImportExportService.ExportFormat.JSON -> "application/json"
        ImportExportService.ExportFormat.CSV -> "text/csv"
    }

    val extension = when (uiState.selectedFormat) {
        ImportExportService.ExportFormat.TRUVALT_ENCRYPTED -> "truvalt"
        ImportExportService.ExportFormat.JSON -> "json"
        ImportExportService.ExportFormat.CSV -> "csv"
    }

    val suggestedFilename = "truvalt_export_${System.currentTimeMillis()}.$extension"

    val fileSaver = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(mimeType)
    ) { uri ->
        uri?.let { viewModel.exportToUri(context, it) }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Export Vault",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Stats summary
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatColumn(count = uiState.itemCount.toString(), label = "Items")
                    StatColumn(count = uiState.folderCount.toString(), label = "Folders")
                    StatColumn(count = uiState.tagCount.toString(), label = "Tags")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Format selection
            ExportSectionCard(title = "Format") {
                ExportFormatRow(
                    label = "Encrypted .truvalt",
                    subtitle = "Locked with your vault master key",
                    selected = uiState.selectedFormat == ImportExportService.ExportFormat.TRUVALT_ENCRYPTED,
                    onClick = { viewModel.setFormat(ImportExportService.ExportFormat.TRUVALT_ENCRYPTED) }
                )
                ExportFormatRow(
                    label = "Unencrypted JSON",
                    subtitle = "Readable JSON export",
                    selected = uiState.selectedFormat == ImportExportService.ExportFormat.JSON,
                    onClick = { viewModel.setFormat(ImportExportService.ExportFormat.JSON) }
                )
                ExportFormatRow(
                    label = "Unencrypted CSV",
                    subtitle = "Spreadsheet-compatible",
                    selected = uiState.selectedFormat == ImportExportService.ExportFormat.CSV,
                    onClick = { viewModel.setFormat(ImportExportService.ExportFormat.CSV) },
                    isLast = true
                )
            }

            Spacer(Modifier.height(24.dp))

            // Export progress / action
            if (uiState.isExporting) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Exporting vault...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { uiState.progress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else if (uiState.isComplete) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Export complete!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.resetComplete() }) {
                            Text("Export Again")
                        }
                    }
                }
            } else {
                Button(
                    onClick = { fileSaver.launch(suggestedFilename) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        "Export Vault",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatColumn(count: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ExportSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = androidx.compose.ui.unit.TextUnit(
                0.1f, androidx.compose.ui.unit.TextUnitType.Em
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
private fun ExportFormatRow(
    label: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
