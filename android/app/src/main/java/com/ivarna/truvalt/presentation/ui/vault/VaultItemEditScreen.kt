package com.ivarna.truvalt.presentation.ui.vault

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivarna.truvalt.domain.model.*
import com.ivarna.truvalt.presentation.ui.shared.PasswordGeneratorDialog
import com.ivarna.truvalt.presentation.ui.shared.QRScannerDialog

@Composable
fun SanctuaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) colorScheme.error else colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 2.dp, bottom = 6.dp),
            letterSpacing = 1.1.sp
        )
        
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
                .border(
                    width = 2.dp,
                    color = if (isFocused) colorScheme.primary.copy(alpha = 0.4f) else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                ),
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            isError = isError,
            supportingText = supportingText,
            shape = RoundedCornerShape(2.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colorScheme.surfaceContainerHighest,
                unfocusedContainerColor = colorScheme.surfaceContainerHighest,
                errorContainerColor = colorScheme.surfaceContainerHighest,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface,
                focusedLabelColor = colorScheme.primary,
                unfocusedLabelColor = colorScheme.onSurfaceVariant
            )
        )
    }
}

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
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 12.dp, end = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Row {
                        IconButton(onClick = { viewModel.toggleFavorite() }) {
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                "Favorite",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (itemId != null) {
                            IconButton(onClick = { viewModel.deleteItem(itemId) }) {
                                Icon(
                                    Icons.Default.DeleteOutline, 
                                    "Delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = if (itemId != null) "Edit Entry" else "New Entry",
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-1).sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Text(
                    text = selectedType.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Cancel", 
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    val signatureGradient = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                    
                    Button(
                        onClick = {
                            validationErrors = viewModel.validateForm()
                            if (validationErrors.isEmpty()) {
                                viewModel.saveItem()
                            }
                        },
                        modifier = Modifier
                            .weight(1.6f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(signatureGradient),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.38f)
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isSaving
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                "Secure Save", 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
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
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                if (validationErrors.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Please correct the following:",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        SanctuaryTextField(
                            value = itemName,
                            onValueChange = { viewModel.updateItemName(it) },
                            label = "Item Name",
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

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
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
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

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SanctuaryTextField(
            value = loginData.url,
            onValueChange = { viewModel.updateLoginData(loginData.copy(url = it)) },
            label = "Website URL",
            leadingIcon = { Icon(Icons.Default.Language, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = loginData.username,
            onValueChange = { viewModel.updateLoginData(loginData.copy(username = it)) },
            label = "Username / Email",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) }
        )

        Column {
            SanctuaryTextField(
                value = loginData.password,
                onValueChange = { viewModel.updateLoginData(loginData.copy(password = it)) },
                label = "Password",
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    Row {
                        IconButton(onClick = { showPasswordGenerator = true }) {
                            Icon(Icons.Default.AutoAwesome, "Generate", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
            
            if (loginData.password.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                PasswordStrengthBar(password = loginData.password)
            }
        }

        SanctuaryTextField(
            value = loginData.totpSeed ?: "",
            onValueChange = { viewModel.updateLoginData(loginData.copy(totpSeed = it.ifBlank { null })) },
            label = "TOTP Seed",
            trailingIcon = {
                IconButton(onClick = { showQRScanner = true }) {
                    Icon(Icons.Default.QrCodeScanner, "Scan QR", tint = MaterialTheme.colorScheme.primary)
                }
            },
            leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary) },
            supportingText = { 
                Text(
                    "Optional: For 2FA/authenticator app codes",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                ) 
            }
        )

        SanctuaryTextField(
            value = loginData.notes,
            onValueChange = { viewModel.updateLoginData(loginData.copy(notes = it)) },
            label = "Notes",
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

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SanctuaryTextField(
            value = passphraseData.passphrase,
            onValueChange = { viewModel.updatePassphraseData(passphraseData.copy(passphrase = it)) },
            label = "Passphrase",
            minLines = 3,
            maxLines = 6,
            leadingIcon = { Icon(Icons.Default.TextFields, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = passphraseData.notes,
            onValueChange = { viewModel.updatePassphraseData(passphraseData.copy(notes = it)) },
            label = "Notes",
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun SecureNoteItemFields(viewModel: VaultItemEditViewModel) {
    val secureNoteData by viewModel.secureNoteData.collectAsState()

    SanctuaryTextField(
        value = secureNoteData.content,
        onValueChange = { viewModel.updateSecureNoteData(secureNoteData.copy(content = it)) },
        label = "Note Content",
        minLines = 8,
        maxLines = 15,
        leadingIcon = { Icon(Icons.Default.Note, null, tint = MaterialTheme.colorScheme.primary) }
    )
}

@Composable
fun SecurityCodeItemFields(viewModel: VaultItemEditViewModel) {
    val securityCodeData by viewModel.securityCodeData.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SanctuaryTextField(
            value = securityCodeData.code,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(code = it)) },
            label = "Security Code",
            leadingIcon = { Icon(Icons.Default.VerifiedUser, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = securityCodeData.codeType,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(codeType = it)) },
            label = "Code Type"
        )

        SanctuaryTextField(
            value = securityCodeData.issuer,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(issuer = it)) },
            label = "Issuer"
        )

        SanctuaryTextField(
            value = securityCodeData.notes,
            onValueChange = { viewModel.updateSecurityCodeData(securityCodeData.copy(notes = it)) },
            label = "Notes",
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun CreditCardItemFields(viewModel: VaultItemEditViewModel) {
    val creditCardData by viewModel.creditCardData.collectAsState()
    var showCvv by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SanctuaryTextField(
            value = creditCardData.cardNumber,
            onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(cardNumber = it)) },
            label = "Card Number",
            leadingIcon = { Icon(Icons.Default.CreditCard, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = creditCardData.cardholderName,
            onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(cardholderName = it)) },
            label = "Cardholder Name"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SanctuaryTextField(
                value = creditCardData.expiryMonth?.toString() ?: "",
                onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(expiryMonth = it.toIntOrNull())) },
                label = "Month",
                modifier = Modifier.weight(1f)
            )

            SanctuaryTextField(
                value = creditCardData.expiryYear?.toString() ?: "",
                onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(expiryYear = it.toIntOrNull())) },
                label = "Year",
                modifier = Modifier.weight(1f)
            )

            SanctuaryTextField(
                value = creditCardData.cvv,
                onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(cvv = it)) },
                label = "CVV",
                modifier = Modifier.weight(1.2f),
                visualTransformation = if (showCvv) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { showCvv = !showCvv }) {
                        Icon(
                            if (showCvv) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }

        SanctuaryTextField(
            value = creditCardData.notes,
            onValueChange = { viewModel.updateCreditCardData(creditCardData.copy(notes = it)) },
            label = "Notes",
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun IdentityItemFields(viewModel: VaultItemEditViewModel) {
    val identityData by viewModel.identityData.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SanctuaryTextField(
            value = identityData.firstName,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(firstName = it)) },
            label = "First Name",
            leadingIcon = { Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = identityData.lastName,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(lastName = it)) },
            label = "Last Name"
        )

        SanctuaryTextField(
            value = identityData.email,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(email = it)) },
            label = "Email",
            leadingIcon = { Icon(Icons.Default.Email, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = identityData.phone,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(phone = it)) },
            label = "Phone",
            leadingIcon = { Icon(Icons.Default.Phone, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = identityData.address,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(address = it)) },
            label = "Address",
            minLines = 3,
            maxLines = 5,
            leadingIcon = { Icon(Icons.Default.Home, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = identityData.notes,
            onValueChange = { viewModel.updateIdentityData(identityData.copy(notes = it)) },
            label = "Notes",
            minLines = 2,
            maxLines = 4
        )
    }
}

@Composable
fun PasskeyItemFields(viewModel: VaultItemEditViewModel) {
    val passkeyData by viewModel.passkeyData.collectAsState()

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        SanctuaryTextField(
            value = passkeyData.credentialId,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(credentialId = it)) },
            label = "Credential ID",
            leadingIcon = { Icon(Icons.Default.PhoneAndroid, null, tint = MaterialTheme.colorScheme.primary) }
        )

        SanctuaryTextField(
            value = passkeyData.rpId,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(rpId = it)) },
            label = "Relying Party ID"
        )

        SanctuaryTextField(
            value = passkeyData.username,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(username = it)) },
            label = "Username"
        )

        SanctuaryTextField(
            value = passkeyData.notes,
            onValueChange = { viewModel.updatePasskeyData(passkeyData.copy(notes = it)) },
            label = "Notes",
            minLines = 2,
            maxLines = 4
        )
    }
}
