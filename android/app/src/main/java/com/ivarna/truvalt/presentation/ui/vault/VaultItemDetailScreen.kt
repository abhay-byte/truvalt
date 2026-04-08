package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivarna.truvalt.presentation.ui.shared.TotpLivePreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultItemDetailScreen(
    itemId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    viewModel: VaultItemEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val loginData by viewModel.loginData.collectAsStateWithLifecycle()
    val passphraseData by viewModel.passphraseData.collectAsStateWithLifecycle()
    val secureNoteData by viewModel.secureNoteData.collectAsStateWithLifecycle()
    val creditCardData by viewModel.creditCardData.collectAsStateWithLifecycle()
    val identityData by viewModel.identityData.collectAsStateWithLifecycle()
    val passkeyData by viewModel.passkeyData.collectAsStateWithLifecycle()
    val securityCodeData by viewModel.securityCodeData.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()

    val palette = rememberVaultPalette()
    val clipboardManager = LocalClipboardManager.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    Scaffold(
        containerColor = palette.background,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Item Details", 
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) palette.brand else palette.muted
                        )
                    }
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = palette.background,
                    titleContentColor = palette.title,
                    navigationIconContentColor = palette.title,
                    actionIconContentColor = palette.title
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = palette.brand)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header section
                Column {
                    Text(
                        text = uiState.item?.name ?: "Unknown Item",
                        style = MaterialTheme.typography.displaySmall,
                        color = palette.title,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedType.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.muted
                    )
                }

                // Fields based on type
                when (selectedType) {
                    is com.ivarna.truvalt.domain.model.VaultItemType.Login -> {
                        if (loginData.url.isNotBlank()) {
                            DetailField("Website", loginData.url, Icons.Default.Language, palette) {
                                clipboardManager.setText(AnnotatedString(loginData.url))
                            }
                        }
                        if (loginData.username.isNotBlank()) {
                            DetailField("Username", loginData.username, Icons.Default.Person, palette) {
                                clipboardManager.setText(AnnotatedString(loginData.username))
                            }
                        }
                        if (loginData.password.isNotBlank()) {
                            PasswordField("Password", loginData.password, palette) {
                                clipboardManager.setText(AnnotatedString(loginData.password))
                            }
                        }
                        if (!loginData.totpSeed.isNullOrBlank()) {
                            TotpSection(loginData.totpSeed!!, palette)
                        }
                        if (loginData.notes.isNotBlank()) {
                            DetailField("Notes", loginData.notes, Icons.Default.Notes, palette) {
                                clipboardManager.setText(AnnotatedString(loginData.notes))
                            }
                        }
                    }
                    is com.ivarna.truvalt.domain.model.VaultItemType.Passphrase -> {
                        if (passphraseData.passphrase.isNotBlank()) {
                            PasswordField("Passphrase", passphraseData.passphrase, palette) {
                                clipboardManager.setText(AnnotatedString(passphraseData.passphrase))
                            }
                        }
                        if (passphraseData.notes.isNotBlank()) {
                            DetailField("Notes", passphraseData.notes, Icons.Default.Notes, palette) {
                                clipboardManager.setText(AnnotatedString(passphraseData.notes))
                            }
                        }
                    }
                    is com.ivarna.truvalt.domain.model.VaultItemType.SecureNote -> {
                        DetailField("Note Content", secureNoteData.content, Icons.Default.Notes, palette) {
                            clipboardManager.setText(AnnotatedString(secureNoteData.content))
                        }
                    }
                    is com.ivarna.truvalt.domain.model.VaultItemType.CreditCard -> {
                        if (creditCardData.cardholderName.isNotBlank()) {
                            DetailField("Cardholder Name", creditCardData.cardholderName, Icons.Default.Person, palette) {
                                clipboardManager.setText(AnnotatedString(creditCardData.cardholderName))
                            }
                        }
                        DetailField("Card Number", creditCardData.cardNumber, Icons.Default.Security, palette) {
                            clipboardManager.setText(AnnotatedString(creditCardData.cardNumber))
                        }
                        
                        val expiry = if (creditCardData.expiryMonth != null && creditCardData.expiryYear != null) {
                            "${creditCardData.expiryMonth}/${creditCardData.expiryYear}"
                        } else ""
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            if (expiry.isNotBlank()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    DetailField("Expiry", expiry, null, palette) {
                                        clipboardManager.setText(AnnotatedString(expiry))
                                    }
                                }
                            }
                            if (creditCardData.cvv.isNotBlank()) {
                                Box(modifier = Modifier.weight(1f)) {
                                    DetailField("CVV", creditCardData.cvv, null, palette) {
                                        clipboardManager.setText(AnnotatedString(creditCardData.cvv))
                                    }
                                }
                            }
                        }
                        if (creditCardData.notes.isNotBlank()) {
                            DetailField("Notes", creditCardData.notes, Icons.Default.Notes, palette) {
                                clipboardManager.setText(AnnotatedString(creditCardData.notes))
                            }
                        }
                    }
                    is com.ivarna.truvalt.domain.model.VaultItemType.Identity -> {
                        val fullName = listOf(identityData.firstName, identityData.lastName).filter { it.isNotBlank() }.joinToString(" ")
                        if (fullName.isNotBlank()) {
                            DetailField("Full Name", fullName, Icons.Default.Person, palette) {
                                clipboardManager.setText(AnnotatedString(fullName))
                            }
                        }
                        if (identityData.email.isNotBlank()) {
                            DetailField("Email", identityData.email, Icons.Default.Language, palette) {
                                clipboardManager.setText(AnnotatedString(identityData.email))
                            }
                        }
                        if (identityData.phone.isNotBlank()) {
                            DetailField("Phone", identityData.phone, Icons.Default.Language, palette) {
                                clipboardManager.setText(AnnotatedString(identityData.phone))
                            }
                        }
                        if (identityData.notes.isNotBlank()) {
                            DetailField("Notes", identityData.notes, Icons.Default.Notes, palette) {
                                clipboardManager.setText(AnnotatedString(identityData.notes))
                            }
                        }
                    }
                    else -> {
                        DetailField("Data", String(uiState.item?.encryptedData ?: byteArrayOf()), Icons.Default.Notes, palette) {
                            clipboardManager.setText(AnnotatedString(String(uiState.item?.encryptedData ?: byteArrayOf())))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Item?") },
            text = { Text("Are you sure you want to move this item to Trash?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteItem(itemId)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DetailField(
    label: String,
    value: String,
    icon: ImageVector?,
    palette: VaultHomePalette,
    onCopy: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = palette.muted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            color = palette.mutedSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    if (icon != null) {
                        Icon(icon, contentDescription = null, tint = palette.brand, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        color = palette.title
                    )
                }
                IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = palette.brand, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    palette: VaultHomePalette,
    onCopy: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = palette.muted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            color = palette.mutedSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (visible) value else "••••••••••••••••",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        letterSpacing = if (visible) 0.sp else 2.sp
                    ),
                    color = palette.title,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(onClick = { visible = !visible }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = palette.brand,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = palette.brand, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun TotpSection(secret: String, palette: VaultHomePalette) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "TWO-FACTOR AUTHENTICATION",
            style = MaterialTheme.typography.labelMedium,
            color = palette.muted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            color = palette.mutedSurface,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                TotpLivePreview(secret = secret)
            }
        }
    }
}
