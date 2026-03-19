package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.domain.model.*
import com.ivarna.truvalt.presentation.ui.shared.PasswordGeneratorDialog
import com.ivarna.truvalt.presentation.ui.shared.QRScannerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultItemEditScreen(
    itemId: String?,
    itemType: String?,
    onNavigateBack: () -> Unit,
    onSaveComplete: () -> Unit,
    viewModel: VaultItemEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val itemName by viewModel.itemName.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(itemType) {
        itemType?.let { viewModel.setItemType(VaultItemType.fromId(it)) }
    }

    LaunchedEffect(itemId) {
        itemId?.let { viewModel.loadItem(it) }
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onSaveComplete()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (itemId != null) "Edit ${selectedType.displayName}" else "Add ${selectedType.displayName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            "Favorite",
                            tint = if (isFavorite) Color(0xFFE91E63) else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (itemId != null) {
                        IconButton(onClick = { viewModel.deleteItem(itemId) }) {
                            Icon(Icons.Default.Delete, "Delete")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        validationErrors = viewModel.validateForm()
                        if (validationErrors.isEmpty()) {
                            viewModel.saveItem()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                if (validationErrors.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            validationErrors.forEach { error ->
                                Text(
                                    "• $error",
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { viewModel.updateItemName(it) },
                    label = { Text("Item Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                when (selectedType) {
                    is VaultItemType.Login -> LoginItemFields(viewModel)
                    is VaultItemType.Passphrase -> PassphraseItemFields(viewModel)
                    is VaultItemType.SecureNote -> SecureNoteItemFields(viewModel)
                    is VaultItemType.SecurityCode -> SecurityCodeItemFields(viewModel)
                    is VaultItemType.CreditCard -> CreditCardItemFields(viewModel)
                    is VaultItemType.Identity -> IdentityItemFields(viewModel)
                    is VaultItemType.Passkey -> PasskeyItemFields(viewModel)
                    else -> {
                        Text(
                            "Additional fields for ${selectedType.displayName} coming soon",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun LoginItemFields(viewModel: VaultItemEditViewModel) {
    val loginData by viewModel.loginData.collectAsState()
    var showPassword by remember { mutableStateOf(false) }
    var showPasswordGenerator by remember { mutableStateOf(false) }
    var showQRScanner by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = loginData.url,
            onValueChange = { viewModel.updateLoginData(loginData.copy(url = it)) },
            label = { Text("Website URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Language, null) }
        )

        OutlinedTextField(
            value = loginData.username,
            onValueChange = { viewModel.updateLoginData(loginData.copy(username = it)) },
            label = { Text("Username / Email *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )

        OutlinedTextField(
            value = loginData.password,
            onValueChange = { viewModel.updateLoginData(loginData.copy(password = it)) },
            label = { Text("Password *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.VpnKey, null) },
            trailingIcon = {
                Row {
                    IconButton(onClick = { showPasswordGenerator = true }) {
                        Icon(Icons.Default.AutoAwesome, "Generate")
                    }
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null
                        )
                    }
                }
            }
        )

        OutlinedTextField(
            value = loginData.totpSeed ?: "",
            onValueChange = { viewModel.updateLoginData(loginData.copy(totpSeed = it.ifBlank { null })) },
            label = { Text("TOTP Seed (Base32)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { showQRScanner = true }) {
                    Icon(Icons.Default.QrCodeScanner, "Scan QR")
                }
            },
            leadingIcon = { Icon(Icons.Default.AccessTime, null) },
            supportingText = { Text("Optional: For 2FA/authenticator app codes") }
        )

        OutlinedTextField(
            value = loginData.notes,
            onValueChange = { viewModel.updateLoginData(loginData.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 3,
            maxLines = 5
        )
    }
    
    if (showPasswordGenerator) {
        PasswordGeneratorDialog(
            onDismiss = { showPasswordGenerator = false },
            onPasswordSelected = { password ->
                viewModel.updateLoginData(loginData.copy(password = password))
                showPasswordGenerator = false
            }
        )
    }
    
    if (showQRScanner) {
        QRScannerDialog(
            onDismiss = { showQRScanner = false },
            onQRCodeScanned = { secret ->
                viewModel.updateLoginData(loginData.copy(totpSeed = secret))
                showQRScanner = false
            }
        )
    }
}

@Composable
fun PassphraseItemFields(viewModel: VaultItemEditViewModel) {
    val passphraseData by viewModel.passphraseData.collectAsState()

    Column {
        OutlinedTextField(
            value = passphraseData.passphrase,
            onValueChange = { viewModel.updatePassphraseData(passphraseData.copy(passphrase = it)) },
            label = { Text("Passphrase *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 3,
            maxLines = 6,
            leadingIcon = { Icon(Icons.Default.TextFields, null) }
        )

        OutlinedTextField(
            value = passphraseData.notes,
            onValueChange = { viewModel.updatePassphraseData(passphraseData.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun SecureNoteItemFields(viewModel: VaultItemEditViewModel) {
    val secureNoteData by viewModel.secureNoteData.collectAsState()

    OutlinedTextField(
        value = secureNoteData.content,
        onValueChange = { viewModel.updateSecureNoteData(secureNoteData.copy(content = it)) },
        label = { Text("Note Content *") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        minLines = 5,
        maxLines = 10,
        leadingIcon = { Icon(Icons.Default.Note, null) }
    )
}

@Composable
fun SecurityCodeItemFields(viewModel: VaultItemEditViewModel) {
    val securityCodeData by viewModel.securityCodeData.collectAsState()

    Column {
        OutlinedTextField(
            value = securityCodeData.code,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(code = it)) },
            label = { Text("Security Code *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.VerifiedUser, null) }
        )

        OutlinedTextField(
            value = securityCodeData.codeType,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(codeType = it)) },
            label = { Text("Code Type") },
            placeholder = { Text("e.g., Recovery, Backup") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = securityCodeData.issuer,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(issuer = it)) },
            label = { Text("Issuer") },
            placeholder = { Text("e.g., Google, GitHub") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = securityCodeData.notes,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun CreditCardItemFields(viewModel: VaultItemEditViewModel) {
    val creditCardData by viewModel.creditCardData.collectAsState()
    var showCvv by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = creditCardData.cardNumber,
            onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(cardNumber = it)) },
            label = { Text("Card Number *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.CreditCard, null) }
        )

        OutlinedTextField(
            value = creditCardData.cardholderName,
            onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(cardholderName = it)) },
            label = { Text("Cardholder Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = creditCardData.expiryMonth?.toString() ?: "",
                onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(expiryMonth = it.toIntOrNull())) },
                label = { Text("Month") },
                placeholder = { Text("MM") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = creditCardData.expiryYear?.toString() ?: "",
                onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(expiryYear = it.toIntOrNull())) },
                label = { Text("Year") },
                placeholder = { Text("YYYY") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            OutlinedTextField(
                value = creditCardData.cvv,
                onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(cvv = it)) },
                label = { Text("CVV") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                visualTransformation = if (showCvv) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCvv = !showCvv }) {
                        Icon(
                            if (showCvv) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null
                        )
                    }
                }
            )
        }

        OutlinedTextField(
            value = creditCardData.notes,
            onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun IdentityItemFields(viewModel: VaultItemEditViewModel) {
    val identityData by viewModel.identityData.collectAsState()

    Column {
        OutlinedTextField(
            value = identityData.firstName,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(firstName = it)) },
            label = { Text("First Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )

        OutlinedTextField(
            value = identityData.lastName,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(lastName = it)) },
            label = { Text("Last Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = identityData.email,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(email = it)) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Email, null) }
        )

        OutlinedTextField(
            value = identityData.phone,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(phone = it)) },
            label = { Text("Phone") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Phone, null) }
        )

        OutlinedTextField(
            value = identityData.address,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(address = it)) },
            label = { Text("Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 3,
            maxLines = 5,
            leadingIcon = { Icon(Icons.Default.Home, null) }
        )

        OutlinedTextField(
            value = identityData.notes,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun PasskeyItemFields(viewModel: VaultItemEditViewModel) {
    val passkeyData by viewModel.passkeyData.collectAsState()

    Column {
        OutlinedTextField(
            value = passkeyData.credentialId,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(credentialId = it)) },
            label = { Text("Credential ID") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) }
        )

        OutlinedTextField(
            value = passkeyData.rpId,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(rpId = it)) },
            label = { Text("Relying Party ID") },
            placeholder = { Text("e.g., example.com") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = passkeyData.username,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(username = it)) },
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = passkeyData.notes,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(notes = it)) },
            label = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            minLines = 2,
            maxLines = 4
        )
    }
}
