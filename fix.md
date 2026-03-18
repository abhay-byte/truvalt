# 🔧 Truvalt — Agent Fix Prompt: Home Screen + Add Item Screen

> This prompt fixes critical UI/UX issues found in the current build.
> Send everything below the horizontal rule to your agent.

---

You are fixing the **Truvalt** Android app (`com.ivarna.cipherkeep`, previously `CipherKeep` — name changed to **Truvalt**).
The app uses **Kotlin + Jetpack Compose + Material Design 3**.

Two screens have critical issues that must be fixed completely. Do NOT stub, truncate, or use TODO comments anywhere. Write every file in full.

---

## ISSUES TO FIX

---

## FIX 1 — App Name & Branding

### Problem
The app name renders as `truvalt` (all lowercase) in the TopAppBar and elsewhere.

### Fix
- Rename all string references from `CipherKeep` / `cipherkeep` to `Truvalt`
- In `res/values/strings.xml`, set `<string name="app_name">Truvalt</string>`
- In `VaultHomeScreen.kt` TopAppBar, the title must render as **`Truvalt`** — styled with:
  - `style = MaterialTheme.typography.headlineMedium`
  - `fontWeight = FontWeight.Bold`
  - Color: `MaterialTheme.colorScheme.primary`
- Update `AndroidManifest.xml` `android:label="@string/app_name"` to reflect the new name
- Update any other hardcoded `"truvalt"` or `"CipherKeep"` string literals in the codebase

---

## FIX 2 — Vault Home Screen (`VaultHomeScreen.kt`)

### Problems
1. No search bar
2. No bottom navigation bar
3. No item type filter chips row
4. FAB jumps directly to "New Login" — there is NO vault item type selection step
5. Empty state has no visual polish or proper CTA

### Fix — complete replacement of `VaultHomeScreen.kt`

Rewrite `VaultHomeScreen.kt` as a complete, production-ready Compose screen. Requirements:

#### 2a. TopAppBar
```
Row layout:
  Left: "Truvalt" title text (headlineMedium, Bold, primary color)
  Right actions:
    - IconButton: Search icon → navigates to SearchScreen (or toggles inline search bar)
    - IconButton: Filter/Sort icon → opens FilterBottomSheet
    - IconButton: MoreVert icon → dropdown menu with: Settings, Import, Export, Lock Vault
```

#### 2b. Inline Search Bar (collapsible)
- When search icon is tapped, a `SearchBar` (Material 3 `DockedSearchBar` or `SearchBar`) animates in below the TopAppBar
- Placeholder text: `"Search vault…"`
- On query change: filter `vaultItems` list in ViewModel
- On clear: collapse search bar, restore full list
- Keyboard: `ImeAction.Search`, focus automatically requested on expand

#### 2c. Filter Chips Row
Below the search bar (or below TopAppBar when search is collapsed), a horizontally scrollable `LazyRow` of `FilterChip` components:

```
Chips (in order):
  [All]  [🔑 Logins]  [🔐 Passkeys]  [💬 Passphrases]  [📝 Notes]  [🔢 2FA Codes]  [🛡 Security Codes]  [💳 Cards]  [👤 Identities]
```

- Only one chip selected at a time (single-select)
- Selected chip: filled style, primary color
- Unselected chip: outlined style
- Selecting a chip filters the vault list to that item type
- "All" chip clears the type filter

#### 2d. Vault Item List
- `LazyColumn` of `VaultItemCard` composables (see FIX 3 for card design)
- Show folder group headers if a folder filter is active
- Sticky headers for alphabetical sections when no folder is selected and list has > 10 items
- Show count of items in current filter at top of list: e.g. `"12 items"`

#### 2e. Empty State (redesign)
When vault is empty OR filtered list is empty:

```
Column (centered):
  - Icon: Material Symbols `lock` or `shield`, size 72dp, tint = colorScheme.primary.copy(alpha=0.4f)
  - Title: "Your vault is empty" (titleLarge)
  - Subtitle: "Tap + to add your first item" (bodyMedium, onSurfaceVariant)
  - [Only when no filter active] Primary filled Button: "Add your first item" → triggers AddItemTypeSheet
```

When a filter is active and no results:
```
  - Icon: `search_off`, 64dp
  - Title: "No results"
  - Subtitle: "Try a different filter or search term"
  - TextButton: "Clear filters"
```

#### 2f. Bottom Navigation Bar
Add a `NavigationBar` (Material 3) at the bottom with these tabs:

| Tab | Icon | Label | Destination |
|---|---|---|---|
| Vault | `lock` (filled when selected) | Vault | VaultHomeScreen |
| Generator | `auto_fix_high` | Generator | GeneratorScreen |
| Health | `health_and_safety` | Health | VaultHealthScreen |
| Settings | `settings` | Settings | SettingsScreen |

- Selected tab indicator: indicator pill (MD3 default)
- Badge: show red badge dot on Health tab if breach issues exist (`healthIssueCount > 0`)

#### 2g. FAB → AddItemTypeSheet (CRITICAL FIX)
The FAB must NOT navigate directly to any screen. Instead:

**FAB spec:**
```kotlin
ExtendedFloatingActionButton(
    onClick = { showAddItemTypeSheet = true },
    icon = { Icon(Icons.Rounded.Add, contentDescription = "Add item") },
    text = { Text("New Item") },
    // Collapse to small FAB when list is scrolling down
    expanded = !listState.isScrollInProgress
)
```

**`AddItemTypeSheet` — Bottom Sheet:**

When FAB is tapped, show a `ModalBottomSheet` with a grid of item type options:

```
Title: "What would you like to add?"

Grid (2 columns, or LazyVerticalGrid):

┌──────────────────────┬──────────────────────┐
│  🔑 Login            │  🔐 Passkey          │
│  Username & password │  FIDO2 / biometric   │
├──────────────────────┼──────────────────────┤
│  💬 Passphrase       │  🔢 2FA / TOTP Code  │
│  Multi-word secret   │  Authenticator seed  │
├──────────────────────┼──────────────────────┤
│  🛡 Security Code    │  📝 Secure Note      │
│  Backup/recovery     │  Encrypted memo      │
├──────────────────────┼──────────────────────┤
│  💳 Credit Card      │  👤 Identity         │
│  Card details        │  Personal info       │
└──────────────────────┴──────────────────────┘
```

Each item in the grid is a `Card` (ElevatedCard) with:
- Large icon (48dp), Material Symbols Rounded
- Bold label (labelLarge)
- Subtitle (labelSmall, onSurfaceVariant)
- `onClick` → dismiss sheet + `navController.navigate(Routes.addItem(type = itemType))`

---

## FIX 3 — `VaultItemCard` Composable (new file: `VaultItemCard.kt`)

Create a new `VaultItemCard.kt` composable used in the vault list.

**Design spec:**
```
ElevatedCard (elevation=1dp, shape=RoundedCornerShape(16dp)):
  Row:
    Left: 
      Box (48dp x 48dp, shape=RoundedCornerShape(12dp), background=primaryContainer):
        Icon (28dp, tint=onPrimaryContainer): type-specific icon
    Center (weight=1f):
      Column:
        Text: item.name (titleSmall, Bold, maxLines=1, ellipsis)
        Text: item.username OR item.url OR item.subtitle (bodySmall, onSurfaceVariant, maxLines=1)
    Right:
      Row:
        [If favorite] Icon(Icons.Rounded.Star, 16dp, tint=tertiary)
        IconButton(24dp): copy icon → copies primary field to clipboard
        IconButton(24dp): more_vert → dropdown (Edit, Move to folder, Share, Delete)
```

**Type-to-icon mapping:**
| Type | Icon | Container color |
|---|---|---|
| LOGIN | `key` | primaryContainer |
| PASSKEY | `fingerprint` | secondaryContainer |
| PASSPHRASE | `chat_bubble` | tertiaryContainer |
| NOTE | `sticky_note_2` | surfaceVariant |
| TOTP | `pin` | secondaryContainer |
| SECURITY_CODE | `shield` | errorContainer (subtle) |
| CARD | `credit_card` | tertiaryContainer |
| IDENTITY | `person` | primaryContainer |

---

## FIX 4 — Add/Edit Item Screen (`AddEditItemScreen.kt`) — COMPLETE REWRITE

### Problems
1. "New Login" title is hardcoded — no type awareness
2. Missing fields: TOTP seed, folder selector, tags, favorite toggle, URL favicon, custom fields
3. No inline password generator button
4. No password strength bar
5. Save button is always grayed out
6. No Material 3 visual quality

### Fix — rewrite `AddEditItemScreen.kt` completely

This screen must be **type-aware** — it receives a `vaultItemType: VaultItemType` nav argument and adapts its fields accordingly.

#### Route
```kotlin
object Routes {
    fun addItem(type: VaultItemType) = "add_item/${type.name}"
    fun editItem(itemId: String) = "edit_item/$itemId"
}
```

#### Screen title
```kotlin
val title = when {
    isEditing -> "Edit ${itemType.displayName}"
    else -> "New ${itemType.displayName}"
}
```
Where `displayName` maps: LOGIN→"Login", PASSKEY→"Passkey", PASSPHRASE→"Passphrase", NOTE→"Secure Note", TOTP→"2FA Code", SECURITY_CODE→"Security Code", CARD→"Credit Card", IDENTITY→"Identity"

#### TopAppBar
```
NavigationIcon: back arrow → confirm discard dialog if form is dirty
Title: dynamic title (see above)
Actions:
  - IconButton: Star (favorite toggle) — filled=gold if favorite, outlined if not
```

#### Form layout
Use `LazyColumn` with `verticalArrangement = Arrangement.spacedBy(12.dp)` and horizontal padding 16dp.

---

### SECTION A — Fields common to ALL item types

**1. Name field (required)**
```kotlin
OutlinedTextField(
    value = name,
    onValueChange = { name = it },
    label = { Text("Name *") },
    leadingIcon = { Icon(Icons.Rounded.Label, null) },
    isError = showErrors && name.isBlank(),
    supportingText = {
        if (showErrors && name.isBlank()) Text("Name is required")
    },
    modifier = Modifier.fillMaxWidth(),
    singleLine = true,
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
)
```

**2. Folder selector**
```kotlin
ExposedDropdownMenuBox(
    // Shows list of user's folders
    // Displays as: OutlinedTextField with leading Icon(folder) and trailing dropdown arrow
    // Default: "No folder"
    // Options: all folders from FolderRepository
)
```

**3. Tags input**
```kotlin
// Chip-based tag input
// Shows existing tags as AssistChip with close (x) button
// InputChip at end with text field for adding new tags
// On Enter or comma: add tag chip
// Example: [gmail ×] [work ×] [+ Add tag...]
```

---

### SECTION B — Type-specific fields

#### Type: LOGIN
```
1. Website URL
   - OutlinedTextField, leadingIcon=language, keyboardType=Uri
   - Trailing: if URL valid, show favicon (AsyncImage/Coil from "https://www.google.com/s2/favicons?sz=32&domain={host}")

2. Username / Email
   - OutlinedTextField, leadingIcon=person, keyboardType=Email, imeAction=Next

3. Password (REQUIRED for this type)
   - OutlinedTextField, leadingIcon=lock
   - Trailing icons:
       a. Eye toggle (visibility / visibility_off)
       b. IconButton: auto_fix_high (Generate) → opens GeneratorBottomSheet inline
   - Below field: PasswordStrengthBar composable
       - Uses zxcvbn score (0–4)
       - Bar: 4 segments, color: red/orange/yellow/green/green
       - Label: "Very Weak" / "Weak" / "Fair" / "Strong" / "Very Strong"
   - visualTransformation = if passwordVisible VisualTransformation.None else PasswordVisualTransformation()

4. TOTP Seed (optional — for storing 2FA alongside login)
   - OutlinedTextField, leadingIcon=pin, label="2FA / TOTP Secret (optional)"
   - Trailing: QR code scan icon → launches QR scanner intent (CameraX / ML Kit barcode scanner)
   - Supporting text: "The secret key from your 2FA QR code"
   - If seed is valid: show live TOTP preview (6-digit code + countdown)
       TotpPreviewRow: Text("123 456", monospace, primary) + CircularProgressIndicator(size=20dp)

5. Notes
   - OutlinedTextField, minLines=3, maxLines=8, label="Notes"
```

#### Type: PASSPHRASE
```
1. Passphrase value
   - OutlinedTextField, leadingIcon=chat_bubble
   - Trailing: eye toggle + generate button → PassphraseGeneratorBottomSheet
   - PasswordStrengthBar below

2. Context / Label (what service uses this passphrase)
   - OutlinedTextField, label="Service / Context"

3. Notes
```

#### Type: TOTP (standalone 2FA seed storage)
```
1. Issuer / Service Name
   - OutlinedTextField, label="Service (e.g. GitHub)"

2. Account (email/username for this 2FA)
   - OutlinedTextField, label="Account", keyboardType=Email

3. TOTP Secret *
   - OutlinedTextField, leadingIcon=pin, label="Secret Key *"
   - Trailing: QR scan icon
   - isError if blank on save attempt
   - Live preview: if seed valid → show TotpLivePreview card:
       Card (surfaceVariant, rounded 16dp):
         Text "Current code" (labelSmall)
         Text "123 456" (headlineLarge, monospace, primary, letterSpacing=8.sp)
         LinearProgressIndicator showing countdown to next rotation
         Text "Refreshes in Xs" (labelSmall)

4. Algorithm (dropdown): SHA1 (default), SHA256, SHA512
5. Digits (segmented button): 6 (default) | 8
6. Period (segmented button): 30s (default) | 60s
7. Notes
```

#### Type: SECURITY_CODE (backup/recovery codes)
```
1. Service Name
   - OutlinedTextField, label="Service *"

2. Recovery Codes (multi-entry)
   - Label: "Recovery / Backup Codes"
   - Dynamic list: each code is an OutlinedTextField row with a remove (×) IconButton
   - "+ Add code" TextButton at bottom adds a new row
   - Show used/total counter: "3 of 10 remaining" (user can toggle each as used)
   - Each row: TextField + "Mark used" toggle chip

3. Notes
```

#### Type: PASSKEY
```
1. Service Name
   - OutlinedTextField

2. Username / Display Name registered with passkey
   - OutlinedTextField

3. Relying Party ID (e.g. github.com)
   - OutlinedTextField, label="Domain / RP ID"

4. Credential ID (optional, for reference)
   - OutlinedTextField, label="Credential ID (optional)"

5. Notes
   - Note at top: InfoCard: "Passkeys are stored for reference. Use your device's credential manager to authenticate."
```

#### Type: NOTE
```
1. Note content *
   - OutlinedTextField, minLines=8, maxLines=24, label="Note *"
   - Character counter: trailing "320 / 10000"

2. No other fields needed
```

#### Type: CARD
```
1. Cardholder Name
2. Card Number (keyboardType=Number, visualTransformation: groups of 4 with spaces, e.g. "4111 1111 1111 1111")
3. Expiry Month (dropdown 01-12)
4. Expiry Year (dropdown current year + 15 years)
5. CVV (trailing eye toggle, keyboardType=NumberPassword)
6. Card Type (auto-detected from number prefix, shown as chip: Visa/Mastercard/Amex/etc.)
7. Billing Address (optional, collapsible section "+ Add billing address")
8. PIN (optional, OutlinedTextField, trailing eye toggle)
9. Notes
```

#### Type: IDENTITY
```
1. Full Name
2. Date of Birth (DatePicker)
3. Email
4. Phone Number (keyboardType=Phone)
5. Address Line 1
6. Address Line 2 (optional)
7. City, State/Province, ZIP/Postal, Country
8. Passport Number (optional, OutlinedTextField)
9. National ID / SSN (optional, OutlinedTextField, trailing eye toggle)
10. Notes
```

---

### SECTION C — Custom Fields (all types except NOTE and IDENTITY)

Collapsible section at bottom of form, above Notes:

```
Header row: "Custom Fields" + TextButton("+ Add field")

When expanded, list of custom field rows:
  Each row:
    - Dropdown: field type (Text / Hidden / URL / Date / Phone)
    - OutlinedTextField: field name placeholder "Field name"
    - OutlinedTextField: field value
    - IconButton: drag handle (for reordering)
    - IconButton: delete (×)
```

---

### SECTION D — Save Button (CRITICAL FIX)

The Save button was always gray/disabled. Fix this:

```kotlin
val isFormValid by remember(name, /* type-specific required fields */) {
    derivedStateOf {
        name.isNotBlank() && when (itemType) {
            VaultItemType.LOGIN -> true // password not required (some sites use SSO)
            VaultItemType.TOTP -> totpSecret.isNotBlank()
            VaultItemType.NOTE -> noteContent.isNotBlank()
            else -> true
        }
    }
}

Button(
    onClick = {
        if (!isFormValid) {
            showErrors = true
            return@Button
        }
        viewModel.saveItem(buildVaultItem())
    },
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 0.dp),
    shape = RoundedCornerShape(16.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
    )
    // DO NOT set enabled=false — the button is always clickable; 
    // if invalid, set showErrors=true to reveal field-level errors
) {
    Icon(Icons.Rounded.Save, contentDescription = null)
    Spacer(Modifier.width(8.dp))
    Text("Save ${itemType.displayName}", style = MaterialTheme.typography.labelLarge)
}
```

Also add at bottom: `Spacer(Modifier.height(32.dp))` so Save button is not hidden behind system nav bar.

---

## FIX 5 — `GeneratorBottomSheet.kt` (inline generator)

When the generate button (auto_fix_high icon) is tapped inside the Password field:

Show a `ModalBottomSheet` containing:

```
Title: "Generate Password"

Toggle (SegmentedButton): [Password] [Passphrase]

--- PASSWORD MODE ---
Slider: Length (8–64, default 20), shows current value
Checkboxes row:
  [✓] Uppercase (A-Z)
  [✓] Lowercase (a-z)  
  [✓] Numbers (0-9)
  [✓] Symbols (!@#$...)
  [ ] Exclude ambiguous (0,O,l,1,I)
OutlinedTextField (readonly): shows generated password, large monospace font
Row:
  IconButton: refresh (regenerate)
  IconButton: copy to clipboard

--- PASSPHRASE MODE ---
Slider: Word count (3–10, default 5)
Dropdown: Separator ( - | . | _ | space )
Checkbox: [✓] Capitalize first letter
Checkbox: [ ] Append number
OutlinedTextField (readonly): shows generated passphrase
PasswordStrengthBar

--- Bottom ---
Button (filled, fullWidth): "Use this password/passphrase"
  → fills the password/passphrase field and dismisses sheet
```

---

## FIX 6 — Navigation Routes Update

Update `NavGraph.kt` to include:

```kotlin
// Home with bottom nav
composable(Routes.HOME) {
    VaultHomeScreen(navController)
}

// Add item — receives type argument
composable(
    route = "add_item/{itemType}",
    arguments = listOf(navArgument("itemType") { type = NavType.StringType })
) { backStackEntry ->
    val typeString = backStackEntry.arguments?.getString("itemType") ?: "LOGIN"
    val itemType = VaultItemType.valueOf(typeString)
    AddEditItemScreen(navController = navController, itemType = itemType, itemId = null)
}

// Edit item — receives item id
composable(
    route = "edit_item/{itemId}",
    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
) { backStackEntry ->
    val itemId = backStackEntry.arguments?.getString("itemId")!!
    AddEditItemScreen(navController = navController, itemType = null, itemId = itemId)
}
```

---

## FIX 7 — ViewModel Updates

### `VaultHomeViewModel.kt`
Ensure these states exist:

```kotlin
data class VaultHomeUiState(
    val isLoading: Boolean = false,
    val allItems: List<VaultItemUi> = emptyList(),
    val filteredItems: List<VaultItemUi> = emptyList(),
    val searchQuery: String = "",
    val activeTypeFilter: VaultItemType? = null,
    val activeFolderFilter: String? = null,
    val isSearchActive: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val healthIssueCount: Int = 0,
    val error: String? = null,
)
```

Filtering logic:
```kotlin
fun onSearchQueryChange(query: String) {
    // filter allItems by name, username, url containing query (case-insensitive)
}

fun onTypeFilterChange(type: VaultItemType?) {
    // null = show all, otherwise filter by type
}
```

### `AddEditItemViewModel.kt`
Ensure:
```kotlin
// Save is never blocked by the ViewModel — only field-level validation in UI
fun saveItem(item: VaultItemDomain): Flow<SaveResult>

// For TOTP preview
fun getTotpCode(secret: String): StateFlow<TotpState> // emits current code + seconds remaining, updates every second
```

---

## DESIGN TOKENS — Apply throughout both screens

Use these MD3 tokens consistently:

```kotlin
// Field shape
val fieldShape = RoundedCornerShape(12.dp)

// Card shape  
val cardShape = RoundedCornerShape(16.dp)

// Section header style
Text(
    text = "SECTION TITLE",
    style = MaterialTheme.typography.labelSmall,
    color = MaterialTheme.colorScheme.primary,
    letterSpacing = 1.5.sp,
    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp, top = 8.dp)
)

// Spacing between form fields
verticalArrangement = Arrangement.spacedBy(12.dp)

// Screen padding
contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
```

---

## FILES TO CREATE / MODIFY

| File | Action | Description |
|---|---|---|
| `res/values/strings.xml` | Modify | Update app_name to "Truvalt" |
| `AndroidManifest.xml` | Modify | Update label |
| `VaultHomeScreen.kt` | Rewrite | Full home screen with search, filter chips, bottom nav, FAB sheet |
| `AddItemTypeSheet.kt` | Create new | Bottom sheet grid of item type options |
| `VaultItemCard.kt` | Create new | Polished vault item card composable |
| `AddEditItemScreen.kt` | Rewrite | Type-aware, fully-featured add/edit form |
| `GeneratorBottomSheet.kt` | Create new | Inline password/passphrase generator sheet |
| `TotpLivePreview.kt` | Create new | Live TOTP code display with countdown |
| `PasswordStrengthBar.kt` | Create new | 4-level zxcvbn-based strength indicator |
| `NavGraph.kt` | Modify | Add typed routes for add/edit item |
| `VaultHomeViewModel.kt` | Modify | Add search + filter state and logic |
| `AddEditItemViewModel.kt` | Modify | Ensure save always enabled, add TOTP preview flow |
| `VaultItemType.kt` | Modify | Add `displayName` and `icon` properties |
| `Routes.kt` | Modify | Add `addItem(type)` and `editItem(id)` helpers |

---

## AGENT RULES

1. Do NOT write stubs, TODOs, or placeholder functions. Every function must be fully implemented.
2. Do NOT truncate any file. Write every composable, every field, every when-branch in full.
3. Every `when(itemType)` branch must cover ALL item types — no `else -> Unit` cop-outs for type-specific fields.
4. The AddItemTypeSheet must show ALL 8 vault item types with correct icons and subtitles.
5. The Save button must NEVER be `enabled = false`. Use field-level error state instead.
6. PasswordStrengthBar must compute real strength — integrate zxcvbn4j or implement equivalent scoring. Do not fake it with password.length.
7. TOTP code preview must use real HOTP/TOTP algorithm (javax.crypto HMAC-SHA1) — not placeholder text.
8. After all changes, confirm the navigation flow is: FAB tap → AddItemTypeSheet → select type → AddEditItemScreen(type) — with NO direct navigation from FAB to any item screen.
