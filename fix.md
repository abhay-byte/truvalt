# 🔐 TRUVALT — Vault Item System Implementation Prompt

**Target Model:** Claude Sonnet 4.6  
**Framework:** Kotlin + Jetpack Compose + Material Design 3  
**Architecture:** Clean Architecture + MVVM  
**Completion Target:** Full vault item creation/edit screens with type system

---

## PART 1: App Configuration Updates

### 1.1 Package & App Name
- **App Package Name:** `com.ivarna.truvalt`
- **App Display Name:** "Truvalt"
- Update `AndroidManifest.xml` and all theme files
- All hardcoded "CipherKeep" strings → "Truvalt"

### 1.2 Gradle Configuration
```gradle
android {
    namespace = "com.ivarna.truvalt"
    compileSdk = 36
    
    defaultConfig {
        applicationId = "com.ivarna.truvalt"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }
}
```

---

## PART 2: Vault Item Type System

### 2.1 Data Model (Clean Architecture)

Create these sealed classes in `domain/model/VaultItemType.kt`:

```kotlin
sealed class VaultItemType(val id: String, val displayName: String) {
    object Login : VaultItemType("login", "Login")
    object Passkey : VaultItemType("passkey", "Passkey")
    object Passphrase : VaultItemType("passphrase", "Passphrase")
    object SecureNote : VaultItemType("secure_note", "Secure Note")
    object SecurityCode : VaultItemType("security_code", "Security/Recovery Code")
    object CreditCard : VaultItemType("credit_card", "Credit Card")
    object Identity : VaultItemType("identity", "Identity")
    object Custom : VaultItemType("custom", "Custom")

    companion object {
        fun fromId(id: String): VaultItemType = when (id) {
            "login" -> Login
            "passkey" -> Passkey
            "passphrase" -> Passphrase
            "secure_note" -> SecureNote
            "security_code" -> SecurityCode
            "credit_card" -> CreditCard
            "identity" -> Identity
            else -> Custom
        }

        fun getAllTypes() = listOf(
            Login, Passkey, Passphrase, SecureNote,
            SecurityCode, CreditCard, Identity, Custom
        )
    }
}
```

### 2.2 Vault Item Entity

Create `domain/model/VaultItem.kt`:

```kotlin
@Entity(tableName = "vault_items")
data class VaultItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemType: String, // VaultItemType.id
    val name: String,
    val description: String = "",
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val folderId: Long? = null,
    val encryptedData: String = "", // AES-256-GCM encrypted JSON
    
    // Login-specific (nullable, used only if itemType == "login")
    val loginUrl: String? = null,
    val loginUsername: String? = null,
    val loginPassword: String? = null,
    val loginTotpSeed: String? = null, // Base32-encoded TOTP seed
    
    // Passphrase-specific
    val passphraseValue: String? = null,
    
    // Passkey-specific
    val passkeyCredentialId: String? = null,
    val passkeyPublicKeyJson: String? = null,
    
    // Security Code-specific
    val securityCodeValue: String? = null,
    val securityCodeType: String? = null, // "recovery", "backup", etc.
    
    // Credit Card-specific
    val cardNumber: String? = null,
    val cardholderName: String? = null,
    val cardExpiryMonth: Int? = null,
    val cardExpiryYear: Int? = null,
    val cardCvv: String? = null,
    
    // Identity-specific
    val identityFirstName: String? = null,
    val identityLastName: String? = null,
    val identityEmail: String? = null,
    val identityPhone: String? = null,
    val identityAddress: String? = null,
    
    // Tags (comma-separated or separate table — use JSON for now)
    val tagsJson: String = "[]", // ["tag1", "tag2"]
    
    // Sync fields
    val syncStatus: String = "local", // "local", "synced", "pending"
    val remoteId: String? = null,
    val lastSyncAt: Long? = null
)
```

### 2.3 ViewModel for Vault Item Creation/Edit

Create `presentation/viewmodel/VaultItemViewModel.kt`:

```kotlin
@HiltViewModel
class VaultItemViewModel @Inject constructor(
    private val vaultItemRepository: VaultItemRepository,
    private val encryptionService: EncryptionService
) : ViewModel() {

    private val _uiState = MutableStateFlow<VaultItemUiState>(VaultItemUiState.Idle)
    val uiState: StateFlow<VaultItemUiState> = _uiState.asStateFlow()

    private val _selectedType = MutableStateFlow<VaultItemType>(VaultItemType.Login)
    val selectedType: StateFlow<VaultItemType> = _selectedType.asStateFlow()

    // Form state
    private val _itemName = MutableStateFlow("")
    val itemName: StateFlow<String> = _itemName.asStateFlow()

    private val _itemDescription = MutableStateFlow("")
    val itemDescription: StateFlow<String> = _itemDescription.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    // Login-specific
    private val _loginUrl = MutableStateFlow("")
    val loginUrl: StateFlow<String> = _loginUrl.asStateFlow()

    private val _loginUsername = MutableStateFlow("")
    val loginUsername: StateFlow<String> = _loginUsername.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _loginTotpSeed = MutableStateFlow("")
    val loginTotpSeed: StateFlow<String> = _loginTotpSeed.asStateFlow()

    private val _showLoginPassword = MutableStateFlow(false)
    val showLoginPassword: StateFlow<Boolean> = _showLoginPassword.asStateFlow()

    // Passphrase-specific
    private val _passphraseValue = MutableStateFlow("")
    val passphraseValue: StateFlow<String> = _passphraseValue.asStateFlow()

    // Tags & Folder
    private val _selectedTags = MutableStateFlow<List<String>>(emptyList())
    val selectedTags: StateFlow<List<String>> = _selectedTags.asStateFlow()

    private val _selectedFolderId = MutableStateFlow<Long?>(null)
    val selectedFolderId: StateFlow<Long?> = _selectedFolderId.asStateFlow()

    fun setItemType(type: VaultItemType) {
        _selectedType.value = type
    }

    fun updateItemName(name: String) {
        _itemName.value = name
    }

    fun updateItemDescription(description: String) {
        _itemDescription.value = description
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun updateLoginUrl(url: String) {
        _loginUrl.value = url
    }

    fun updateLoginUsername(username: String) {
        _loginUsername.value = username
    }

    fun updateLoginPassword(password: String) {
        _loginPassword.value = password
    }

    fun toggleShowPassword() {
        _showLoginPassword.value = !_showLoginPassword.value
    }

    fun updateLoginTotpSeed(seed: String) {
        _loginTotpSeed.value = seed
    }

    fun updatePassphraseValue(value: String) {
        _passphraseValue.value = value
    }

    fun addTag(tag: String) {
        _selectedTags.value = (_selectedTags.value + tag).distinct()
    }

    fun removeTag(tag: String) {
        _selectedTags.value = _selectedTags.value - tag
    }

    fun setFolderId(folderId: Long?) {
        _selectedFolderId.value = folderId
    }

    fun saveVaultItem() {
        viewModelScope.launch {
            _uiState.value = VaultItemUiState.Saving
            try {
                val item = VaultItem(
                    itemType = _selectedType.value.id,
                    name = _itemName.value.trim(),
                    description = _itemDescription.value.trim(),
                    isFavorite = _isFavorite.value,
                    folderId = _selectedFolderId.value,
                    tagsJson = Json.encodeToString(_selectedTags.value),
                    // Type-specific fields
                    loginUrl = if (_selectedType.value is VaultItemType.Login) _loginUrl.value else null,
                    loginUsername = if (_selectedType.value is VaultItemType.Login) _loginUsername.value else null,
                    loginPassword = if (_selectedType.value is VaultItemType.Login) _loginPassword.value else null,
                    loginTotpSeed = if (_selectedType.value is VaultItemType.Login) _loginTotpSeed.value else null,
                    passphraseValue = if (_selectedType.value is VaultItemType.Passphrase) _passphraseValue.value else null,
                    syncStatus = "local"
                )

                // Encrypt sensitive data before storing
                val encryptedItem = item.copy(
                    encryptedData = encryptionService.encryptVaultItem(item)
                )

                vaultItemRepository.insertVaultItem(encryptedItem)
                _uiState.value = VaultItemUiState.Success
            } catch (e: Exception) {
                _uiState.value = VaultItemUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun validateForm(): List<String> {
        val errors = mutableListOf<String>()
        if (_itemName.value.isBlank()) errors.add("Item name is required")
        when (_selectedType.value) {
            is VaultItemType.Login -> {
                if (_loginUsername.value.isBlank()) errors.add("Username is required")
            }
            is VaultItemType.Passphrase -> {
                if (_passphraseValue.value.isBlank()) errors.add("Passphrase is required")
            }
            else -> {}
        }
        return errors
    }
}

sealed class VaultItemUiState {
    object Idle : VaultItemUiState()
    object Saving : VaultItemUiState()
    object Success : VaultItemUiState()
    data class Error(val message: String) : VaultItemUiState()
}
```

---

## PART 3: Screens & Navigation

### 3.1 Type Selection Screen

Create `presentation/screen/VaultItemTypeSelectionScreen.kt`:

```kotlin
@Composable
fun VaultItemTypeSelectionScreen(
    onTypeSelected: (VaultItemType) -> Unit,
    onDismiss: () -> Unit
) {
    val types = VaultItemType.getAllTypes()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Item") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(types) { type ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { onTypeSelected(type) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = getIconForType(type),
                            contentDescription = type.displayName,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                type.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                getTypeDescription(type),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

fun getIconForType(type: VaultItemType): androidx.compose.material.icons.Icons {
    return when (type) {
        is VaultItemType.Login -> Icons.Default.VpnKey
        is VaultItemType.Passkey -> Icons.Default.PhoneAndroid
        is VaultItemType.Passphrase -> Icons.Default.TextFields
        is VaultItemType.SecureNote -> Icons.Default.Note
        is VaultItemType.SecurityCode -> Icons.Default.VerifiedUser
        is VaultItemType.CreditCard -> Icons.Default.CreditCard
        is VaultItemType.Identity -> Icons.Default.Person
        is VaultItemType.Custom -> Icons.Default.MoreHoriz
    }
}

fun getTypeDescription(type: VaultItemType): String = when (type) {
    is VaultItemType.Login -> "Username, password, and TOTP codes"
    is VaultItemType.Passkey -> "Passkey credential"
    is VaultItemType.Passphrase -> "Memorable phrase or sentence"
    is VaultItemType.SecureNote -> "Plain text secure note"
    is VaultItemType.SecurityCode -> "Recovery, backup, or security codes"
    is VaultItemType.CreditCard -> "Card number, CVV, expiry"
    is VaultItemType.Identity -> "Name, email, phone, address"
    is VaultItemType.Custom -> "Custom-defined item type"
}
```

### 3.2 Vault Item Creation/Edit Screen

Create `presentation/screen/VaultItemCreateEditScreen.kt`:

```kotlin
@Composable
fun VaultItemCreateEditScreen(
    viewModel: VaultItemViewModel = hiltViewModel(),
    onSaved: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val selectedType by viewModel.selectedType.collectAsState()
    val itemName by viewModel.itemName.collectAsState()
    val itemDescription by viewModel.itemDescription.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Login fields
    val loginUrl by viewModel.loginUrl.collectAsState()
    val loginUsername by viewModel.loginUsername.collectAsState()
    val loginPassword by viewModel.loginPassword.collectAsState()
    val loginTotpSeed by viewModel.loginTotpSeed.collectAsState()
    val showLoginPassword by viewModel.showLoginPassword.collectAsState()

    // Passphrase
    val passphraseValue by viewModel.passphraseValue.collectAsState()

    val scrollState = rememberScrollState()
    var validationErrors by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(uiState) {
        if (uiState is VaultItemUiState.Success) {
            onSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add ${selectedType.displayName}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            "Favorite",
                            tint = if (isFavorite) Color.Red else Color.Gray
                        )
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        validationErrors = viewModel.validateForm()
                        if (validationErrors.isEmpty()) {
                            viewModel.saveVaultItem()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = uiState !is VaultItemUiState.Saving
                ) {
                    if (uiState is VaultItemUiState.Saving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            // Validation errors
            if (validationErrors.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFEBEE)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        validationErrors.forEach { error ->
                            Text(
                                error,
                                color = Color(0xFFC62828),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Common fields
            OutlinedTextField(
                value = itemName,
                onValueChange = { viewModel.updateItemName(it) },
                label = { Text("Item Name *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = itemDescription,
                onValueChange = { viewModel.updateItemDescription(it) },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                minLines = 2,
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Type-specific fields
            when (selectedType) {
                is VaultItemType.Login -> {
                    LoginItemFields(
                        url = loginUrl,
                        onUrlChange = { viewModel.updateLoginUrl(it) },
                        username = loginUsername,
                        onUsernameChange = { viewModel.updateLoginUsername(it) },
                        password = loginPassword,
                        onPasswordChange = { viewModel.updateLoginPassword(it) },
                        showPassword = showLoginPassword,
                        onShowPasswordToggle = { viewModel.toggleShowPassword() },
                        totpSeed = loginTotpSeed,
                        onTotpSeedChange = { viewModel.updateLoginTotpSeed(it) }
                    )
                }
                is VaultItemType.Passphrase -> {
                    PassphraseItemFields(
                        value = passphraseValue,
                        onValueChange = { viewModel.updatePassphraseValue(it) }
                    )
                }
                else -> {
                    Text(
                        "Additional fields for ${selectedType.displayName} coming soon",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LoginItemFields(
    url: String,
    onUrlChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    showPassword: Boolean,
    onShowPasswordToggle: () -> Unit,
    totpSeed: String,
    onTotpSeedChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = url,
            onValueChange = onUrlChange,
            label = { Text("Website URL") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Language, null) }
        )

        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username / Email *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, null) }
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.VpnKey, null) },
            trailingIcon = {
                IconButton(onClick = onShowPasswordToggle) {
                    Icon(
                        if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        null
                    )
                }
            }
        )

        OutlinedTextField(
            value = totpSeed,
            onValueChange = onTotpSeedChange,
            label = { Text("TOTP Seed (Base32)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.AccessTime, null) },
            supportingText = {
                Text("Optional: For 2FA/authenticator app codes")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun PassphraseItemFields(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Passphrase *") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            minLines = 3,
            maxLines = 6,
            leadingIcon = { Icon(Icons.Default.TextFields, null) }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}
```

### 3.3 Updated Home/Vault Screen

Create `presentation/screen/VaultHomeScreen.kt`:

```kotlin
@Composable
fun VaultHomeScreen(
    onNavigateToItemCreation: (VaultItemType) -> Unit,
    onNavigateToTypeSelection: () -> Unit
) {
    var showEmptyState by remember { mutableStateOf(true) } // Replace with actual vault items check

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Truvalt") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FilterList, "Filter")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToTypeSelection,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, "Add Item")
            }
        }
    ) { paddingValues ->
        if (showEmptyState) {
            EmptyVaultState(
                onAddItem = onNavigateToTypeSelection,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // TODO: Display vault items in LazyColumn
            Text("Vault items here")
        }
    }
}

@Composable
fun EmptyVaultState(
    onAddItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            "Your vault is empty",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            "Add your first password or secure note to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .padding(horizontal = 24.dp)
        )

        Button(
            onClick = onAddItem,
            modifier = Modifier
                .padding(top = 24.dp)
                .height(48.dp)
        ) {
            Icon(Icons.Default.Add, null, modifier = Modifier.padding(end = 8.dp))
            Text("Add First Item")
        }
    }
}
```

### 3.4 Navigation Graph Update

Create `navigation/TrauvaltNavigation.kt`:

```kotlin
@Composable
fun TrauvaltNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            VaultHomeScreen(
                onNavigateToItemCreation = { type ->
                    navController.navigate("create_item/${type.id}")
                },
                onNavigateToTypeSelection = {
                    navController.navigate("type_selection")
                }
            )
        }

        composable("type_selection") {
            VaultItemTypeSelectionScreen(
                onTypeSelected = { type ->
                    navController.navigate("create_item/${type.id}") {
                        popUpTo("type_selection") { inclusive = true }
                    }
                },
                onDismiss = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "create_item/{itemTypeId}",
            arguments = listOf(
                navArgument("itemTypeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemTypeId = backStackEntry.arguments?.getString("itemTypeId") ?: "login"
            val itemType = VaultItemType.fromId(itemTypeId)

            val viewModel: VaultItemViewModel = hiltViewModel()
            LaunchedEffect(itemType) {
                viewModel.setItemType(itemType)
            }

            VaultItemCreateEditScreen(
                viewModel = viewModel,
                onSaved = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
```

---

## PART 4: Database Setup

### 4.1 Update Room Database

```kotlin
@Database(
    entities = [VaultItem::class, Folder::class, Tag::class],
    version = 1,
    exportSchema = true
)
abstract class TrauvaltDatabase : RoomDatabase() {
    abstract fun vaultItemDao(): VaultItemDao
    abstract fun folderDao(): FolderDao
}

@Dao
interface VaultItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultItem)

    @Query("SELECT * FROM vault_items WHERE id = :id")
    suspend fun getVaultItem(id: Long): VaultItem?

    @Query("SELECT * FROM vault_items ORDER BY updatedAt DESC")
    fun getAllVaultItems(): Flow<List<VaultItem>>

    @Query("SELECT * FROM vault_items WHERE itemType = :itemType ORDER BY updatedAt DESC")
    fun getVaultItemsByType(itemType: String): Flow<List<VaultItem>>

    @Update
    suspend fun updateVaultItem(item: VaultItem)

    @Delete
    suspend fun deleteVaultItem(item: VaultItem)
}
```

---

## PART 5: Implementation Checklist

### Must-Have (Complete all before testing):
- [ ] Update app name to "Truvalt" everywhere
- [ ] Implement VaultItemType sealed class with all 8 types
- [ ] Create VaultItem entity with type-specific nullable fields
- [ ] Implement VaultItemViewModel with full state management
- [ ] Build VaultItemTypeSelectionScreen with proper icons/descriptions
- [ ] Build VaultItemCreateEditScreen with conditional fields for Login & Passphrase
- [ ] Implement LoginItemFields composable (URL, Username, Password, TOTP Seed)
- [ ] Implement PassphraseItemFields composable
- [ ] Create ValidateForm() function with proper error handling
- [ ] Update Room Database DAO with new schema
- [ ] Set up Hilt DI for ViewModel & Repository
- [ ] Update Navigation Graph with type selection → item creation flow
- [ ] Implement EmptyVaultState composable
- [ ] Password visibility toggle with eye icon
- [ ] Favorite toggle with heart icon
- [ ] Form validation with error display

### Next Phase (After core):
- [ ] Password generator button in Login form
- [ ] TOTP code preview/generation from seed
- [ ] Secure clipboard with auto-clear
- [ ] Password strength meter (zxcvbn)
- [ ] Folder & tag selection UI
- [ ] Encryption before storage (AES-256-GCM)
- [ ] Import/export screens
- [ ] Biometric unlock

---

## PART 6: Important Notes

1. **Zero-Knowledge Architecture:** All encryption happens client-side. Never send plaintext to any network.
2. **Type-Safe Fields:** Only show fields relevant to selected type. Use sealed class pattern.
3. **Material Design 3:** Use Material You colors, proper spacing (16.dp baseline), and Jetpack Compose conventions.
4. **Complete the Prompt:** Zero stubs, no TODOs left behind. All code must be production-ready.
5. **MVVM + Clean Architecture:** Separate concerns — UI layer (Composable) → Presentation (ViewModel) → Domain (Use Cases) → Data (Repository/DAO).

---

## Final Output Checklist

- [ ] All screens render without errors
- [ ] Type selection screen shows all 8 item types
- [ ] Item creation form shows correct fields based on selected type
- [ ] Form validation prevents saving with missing required fields
- [ ] Favorite toggle works correctly
- [ ] Password visibility toggle works
- [ ] Navigation flow: Home → Type Selection → Item Creation → Back to Home
- [ ] No hardcoded "CipherKeep" strings remain
- [ ] Room database properly integrated
- [ ] Hilt dependency injection configured
