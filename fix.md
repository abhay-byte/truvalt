# Truvalt — Kiro Fix Prompt Round 2
# Model: Claude Sonnet 4.5 via kiro-cli

## HOW TO USE
```bash
cd /path/to/truvalt/android
kiro chat
# Set model: /model → claude-sonnet-4-5
# Paste SECTION A first → create steering files
# Paste SECTION B → create spec files
# Then run each TASK in order
```

---

## SECTION A — UPDATE STEERING FILES

Tell Kiro: "Update `.kiro/steering/tech.md` to append the following section:"

```markdown
## Critical Android 15 Edge-to-Edge Rule
App targets SDK 35. `enableEdgeToEdge()` is called in MainActivity.
This means ALL Scaffold instances MUST consume innerPadding correctly:

CORRECT pattern for LazyColumn inside Scaffold:
```kotlin
Scaffold { innerPadding ->
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier.consumeWindowInsets(innerPadding)
    ) { ... }
}
```

CORRECT pattern for Column/Box inside Scaffold:
```kotlin
Scaffold { innerPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .consumeWindowInsets(innerPadding)
    ) { ... }
}
```

NEVER double-apply insets. NEVER add `.padding(bottom = X.dp)` manually when Scaffold already handles it.
NavigationBar in MainScaffold automatically gets bottom inset via `NavigationBarDefaults.windowInsets`.

## BiometricPrompt Requirement
MainActivity MUST extend `AppCompatActivity` (NOT ComponentActivity).
This is REQUIRED for BiometricPrompt to work correctly.
Theme MUST use an AppCompat parent: `Theme.AppCompat.NoActionBar` or bridge theme.
BiometricPrompt takes a `FragmentActivity` reference — cast context via `context as FragmentActivity`.

## Auth Screen Rule  
Auth screens (SplashScreen, BiometricUnlockScreen, PinSetupScreen, PinUnlockScreen, MasterPasswordScreen)
MUST NOT show the bottom NavigationBar.
These screens are in the OUTER NavHost (before "main"), not inside MainScaffold.
MainScaffold (which contains the bottom nav) is only reachable AFTER successful authentication.

## Tab Screen Rule
Screens that are bottom nav tab roots (VaultHomeScreen, GeneratorScreen, VaultHealthScreen, SettingsScreen)
MUST NOT have a TopAppBar with a navigation back arrow.
They are root destinations — there is nothing to go back to within their tab graph.
Only sub-screens within a tab graph (e.g. VaultItemDetail, AddEditItemScreen, SecuritySettingsScreen)
should have a back arrow.
```

---

## SECTION B — SPEC

Create `.kiro/specs/round2-fixes/requirements.md`, `design.md`, `tasks.md` as follows.

---

### `.kiro/specs/round2-fixes/requirements.md`

```markdown
# Truvalt Round 2 Fixes — Requirements

## REQ-R2-01: Biometric Prompt Must Actually Trigger

GIVEN the user has enabled biometric unlock in Settings
AND the vault is currently locked
WHEN the app is opened or the vault is locked by any means
THEN the system SHALL immediately invoke `biometricPrompt.authenticate(promptInfo)` without
  requiring any additional user tap.

The root cause of current failure:
- MainActivity extends ComponentActivity instead of AppCompatActivity.
- BiometricPrompt requires a FragmentActivity. ComponentActivity does not satisfy this.
- Fix: Change `class MainActivity : ComponentActivity()` to `class MainActivity : AppCompatActivity()`.
- Fix: Update `AndroidManifest.xml` theme to use `Theme.AppCompat.NoActionBar` or a bridge theme
  that extends AppCompat while applying Material3 styling.
- Fix: In `BiometricPromptManager` or `BiometricUnlockScreen`, ensure the BiometricPrompt is
  constructed with `activity as AppCompatActivity` and called in a `LaunchedEffect(Unit)` block
  so it fires immediately on screen entry — not waiting for a button press.

### Acceptance Criteria
- [ ] MainActivity extends AppCompatActivity
- [ ] Theme XML parent is AppCompat-compatible
- [ ] BiometricPrompt fires immediately when BiometricUnlockScreen is shown (no button required)
- [ ] Success → vault unlocked → navigate to vault home
- [ ] Negative button ("Use PIN") → navigate to PinUnlockScreen
- [ ] If device has no enrolled biometrics → skip directly to PinUnlockScreen

---

## REQ-R2-02: PIN Dots Must Be Dynamic

The PIN setup and unlock screens show 8 dots at all times.
This is wrong. The dots must reflect the actual number of digits entered.

### PinSetupScreen dot behavior
- Show dots equal to max PIN length (which is 8 by default, OR equal to the PIN being set).
- BETTER UX: show a fixed row of dots equal to the CURRENT input length + remaining empty dots up to max.
- Filled dot = digit entered. Empty dot = digit not yet entered.
- As user taps digits: fill dots left to right.
- As user taps backspace: unfill dots right to left.
- When the user has entered ≥4 digits and taps the confirm button: proceed.
- When the user enters a digit that brings total to 8: auto-proceed to confirm step.

### Correct implementation
```kotlin
// The dots row should show exactly `maxPinLength` dots
// filled = pin.length, unfilled = maxPinLength - pin.length
val maxPinLength = 8

Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    repeat(maxPinLength) { index ->
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(
                    if (index < pin.length)
                        MaterialTheme.colorScheme.primary          // filled
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) // empty
                )
        )
    }
}
```
Do NOT use `repeat(8)` as a hardcoded constant. Use `maxPinLength` variable.

### PinUnlockScreen dot behavior
- Show the same maxPinLength dots as was used when the PIN was set (stored in DataStore).
- Fill as user enters digits.
- On incorrect PIN: shake animation on the dots row, clear all dots, show "Incorrect PIN" text.
- On 5th incorrect attempt: clear input + show "Too many attempts. Re-enter master password." + navigate to MasterPasswordScreen.

### Acceptance Criteria
- [ ] Dots are dynamic: only filled dots count = digits entered
- [ ] Empty dots = remaining slots up to maxPinLength
- [ ] No hardcoded `repeat(8)` — use maxPinLength from DataStore or local state
- [ ] Incorrect PIN → shake animation → clear
- [ ] 5 failures → navigate to MasterPasswordScreen

---

## REQ-R2-03: Tab Root Screens Must NOT Have Back Arrows

Screens `GeneratorScreen`, `VaultHealthScreen`, `SettingsScreen` currently show a
`TopAppBar` with a back navigation icon (`←`). This is incorrect UX.

These are bottom navigation tab root destinations. Users navigate to them via the bottom bar,
not via a push navigation. Showing a back arrow implies there is a screen to go back to —
there is not (within the tab context).

### Fix per screen

**GeneratorScreen:**
```kotlin
// REMOVE this:
TopAppBar(
    navigationIcon = {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
        }
    },
    title = { Text("Password Generator") }
)

// REPLACE with:
TopAppBar(
    title = {
        Text(
            text = "Generator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
    // No navigationIcon
)
```

**VaultHealthScreen:**
```kotlin
// Same fix — remove navigationIcon, keep title "Health"
```

**SettingsScreen:**
```kotlin
// Same fix — remove navigationIcon, keep title "Settings"
```

**Sub-screens that SHOULD keep the back arrow:**
- SecuritySettingsScreen ✓ (accessed from Settings, has sub-navigation)
- PinSetupScreen ✓
- AuditLogScreen ✓
- SessionManagerScreen ✓
- VaultItemDetailScreen ✓
- AddEditItemScreen ✓

### Acceptance Criteria
- [ ] GeneratorScreen TopAppBar has no navigationIcon
- [ ] VaultHealthScreen TopAppBar has no navigationIcon
- [ ] SettingsScreen TopAppBar has no navigationIcon
- [ ] VaultHomeScreen TopAppBar has no navigationIcon (it already correctly has search/filter/more)
- [ ] Sub-screens within tabs still have back arrows

---

## REQ-R2-04: Content Must Not Be Clipped by Bottom Navigation Bar

ALL screens inside MainScaffold are having their bottom content clipped behind the NavigationBar.
This is because `innerPadding` from Scaffold is not being consumed in the content.

### Root fix in MainScaffold
```kotlin
Scaffold(
    bottomBar = { TruvaltBottomBar(...) }
) { innerPadding ->
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)          // ← apply top+bottom padding from Scaffold
            .consumeWindowInsets(innerPadding), // ← prevent double insets in children
        ...
    )
}
```

### Fix in every screen's LazyColumn
```kotlin
// CORRECT — pass contentPadding to LazyColumn
LazyColumn(
    contentPadding = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = 8.dp,
        bottom = 16.dp   // extra breathing room at bottom
    ),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxSize()
)
```

Do NOT add a hardcoded `Spacer(Modifier.height(80.dp))` at the bottom of lists.
Let the Scaffold innerPadding handle the space.

### Fix for screens with a bottom-anchored Button
For AddEditItemScreen and GeneratorScreen, the Save/Use button is inside a Column in a LazyColumn.
Ensure it is the LAST item in the LazyColumn with appropriate contentPadding.

```kotlin
LazyColumn(
    contentPadding = innerPadding + PaddingValues(horizontal = 16.dp, bottom = 16.dp)
) {
    // ... form fields ...
    item {
        Button(
            onClick = { ... },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) { Text("Save") }
        Spacer(Modifier.height(8.dp))
    }
}
```

### Acceptance Criteria
- [ ] Generator "Use this password" button fully visible without scrolling on standard phone
- [ ] Settings last item ("Lock Vault") fully visible
- [ ] Vault home list items don't disappear behind bottom nav
- [ ] Health page all issue cards visible
- [ ] No manual bottom spacers needed

---

## REQ-R2-05: Settings Screen — Restore Security Section

The Security section is missing from SettingsScreen. It was present in an earlier build but
is now absent. The section must be restored at the TOP of the settings list.

### Required Security section (must be the FIRST section in SettingsScreen)
```
Section header: "Security"    ← colored with colorScheme.primary, labelSmall, letterSpacing

Row 1: Biometric Unlock
  Icon: fingerprint
  Title: "Biometric Unlock"
  Subtitle: "Enabled" / "Disabled" (dynamic based on DataStore pref)
  Trailing: Switch (checked = isBiometricEnabled)
  On toggle:
    → If enabling AND BiometricStatus != AVAILABLE: show snackbar "No biometric enrolled"
    → If enabling AND AVAILABLE: save pref to DataStore, show snackbar "Biometric unlock enabled"
    → If disabling: save pref, show snackbar "Biometric unlock disabled"

Row 2: PIN Lock
  Icon: pin
  Title: "PIN Lock"
  Subtitle: "Set up" / "Enabled" / "Change PIN"
  Trailing: Switch (checked = isPinEnabled)
  On toggle:
    → If enabling: navigate to PinSetupScreen
    → If disabling: show confirmation dialog "Disable PIN lock?" → on confirm, clear PIN hash from storage

Row 3: Auto-lock (EXISTING — keep as is)
Row 4: Clipboard Timeout (EXISTING — keep as is)
```

### Acceptance Criteria
- [ ] "Security" section header visible at top of SettingsScreen
- [ ] Biometric Unlock row present with Switch
- [ ] PIN Lock row present with Switch
- [ ] Biometric toggle correctly reads from and writes to DataStore
- [ ] PIN toggle correctly triggers PinSetupScreen flow
- [ ] Security section is ABOVE Appearance, Sync, Data sections

---

## REQ-R2-06: Auth Screens Must Not Show Bottom Navigation

`PinSetupScreen` currently shows the bottom NavigationBar at the bottom.
Auth screens are part of the OUTER navigation graph and must be full-screen without chrome.

### Fix
Auth screens (Splash, BiometricUnlock, PinSetup, PinUnlock, MasterPassword, Onboarding)
must be placed in the OUTER `NavHost` in `TruvaltNavGraph.kt` — OUTSIDE of `MainScaffold`.

`MainScaffold` (which contains the Scaffold with bottom nav) is only navigated to as a
single composable destination `"main"` in the outer NavHost.

```kotlin
// TruvaltNavGraph.kt — OUTER NavHost (no bottom bar here)
NavHost(navController = rootNavController, startDestination = "splash") {
    composable("splash") { SplashScreen(...) }
    composable("onboarding") { OnboardingScreen(...) }
    composable("unlock_biometric") { BiometricUnlockScreen(...) }
    composable("unlock_pin") { PinUnlockScreen(...) }
    composable("unlock_password") { MasterPasswordScreen(...) }
    composable("pin_setup") { PinSetupScreen(...) } // ← MOVE HERE from settings_graph
    
    // The entire app with bottom nav lives here as a single destination:
    composable("main") { MainScaffold(rootNavController = rootNavController) }
}
```

`pin_setup` must be removed from `settings_graph` inside MainScaffold and placed in the outer
NavHost. When the user navigates to PIN setup from Settings, use `rootNavController.navigate("pin_setup")`
not the inner `tabNavController`.

### Acceptance Criteria  
- [ ] PinSetupScreen shows NO bottom NavigationBar
- [ ] PinUnlockScreen shows NO bottom NavigationBar
- [ ] BiometricUnlockScreen shows NO bottom NavigationBar
- [ ] SplashScreen shows NO bottom NavigationBar
- [ ] After PIN is set and saved, navigation returns to Settings (use rootNavController.popBackStack())

---

## REQ-R2-07: Seed Data Must Actually Load

The vault is empty on every launch. Seed data from the previous fix was written but is not
being inserted or is not decryptable.

### Diagnosis checklist (agent must check these)
1. Is `SeedDataInserter.insertIfEmpty()` actually being called? Add a log to verify.
2. Is the vault key available at the time seed insertion is called? Seed insertion requires
   an active vault key to encrypt items. If called before unlock, it will fail silently.
3. Is `IS_FIRST_LAUNCH` DataStore key being set correctly? If it's never set to false after
   first insert, seed may be re-inserting and failing each time.
4. Is Room database name correct? Verify `TruvaltDatabase` is not being recreated on each launch.

### Fix
Move seed data insertion to AFTER vault unlock, not in Application.onCreate():

```kotlin
// In VaultHomeViewModel or VaultRepository:
init {
    viewModelScope.launch {
        if (preferencesRepository.isFirstLaunch()) {
            seedDataInserter.insertSeedData(vaultKey = cryptoManager.getActiveKey())
            preferencesRepository.setFirstLaunchDone()
        }
        loadVaultItems()
    }
}
```

The seed data items from the previous spec (design.md DESIGN-05 table) must all be inserted.
If any encryption fails, log the error and insert as many as possible — do not silently fail.

### Acceptance Criteria
- [ ] On first launch after unlock, 16 seed items appear in vault
- [ ] VaultHealthScreen shows items analyzed (not "0 items analyzed")
- [ ] All 8 filter chips show ≥1 item when tapped
- [ ] Seed data is only inserted once (not re-inserted on every launch)
```

---

### `.kiro/specs/round2-fixes/design.md`

```markdown
# Truvalt Round 2 Fixes — Technical Design

## CRITICAL: MainActivity Must Extend AppCompatActivity

```kotlin
// android/app/src/main/java/com/ivarna/cipherkeep/MainActivity.kt

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {   // ← NOT ComponentActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TruvaltTheme {
                TruvaltNavGraph()
            }
        }
    }
}
```

**Theme in res/values/themes.xml:**
```xml
<resources>
    <style name="Theme.Truvalt" parent="Theme.AppCompat.DayNight.NoActionBar">
        <!-- Material3 bridge: use AppCompat parent, Material3 components still work -->
        <item name="android:statusBarColor">@android:color/transparent</item>
        <item name="android:navigationBarColor">@android:color/transparent</item>
        <item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item>
    </style>
</resources>
```

---

## DESIGN-R2-01: BiometricPromptManager (complete rewrite)

```kotlin
// core/biometric/BiometricPromptManager.kt

class BiometricPromptManager(
    private val activity: AppCompatActivity
) {
    sealed class BiometricResult {
        object Success : BiometricResult()
        object Failed : BiometricResult()
        data class Error(val code: Int, val message: String) : BiometricResult()
        object NotEnrolled : BiometricResult()
        object HardwareUnavailable : BiometricResult()
        object FallbackRequested : BiometricResult()   // user tapped "Use PIN"
    }

    private val resultChannel = Channel<BiometricResult>(Channel.CONFLATED)
    val results: Flow<BiometricResult> = resultChannel.receiveAsFlow()

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(activity)
        return manager.canAuthenticate(BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun showPrompt(title: String = "Unlock Truvalt", negativeText: String = "Use PIN") {
        val manager = BiometricManager.from(activity)
        when (manager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                resultChannel.trySend(BiometricResult.NotEnrolled)
                return
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
                return
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle("Verify your identity to access your vault")
            .setNegativeButtonText(negativeText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setConfirmationRequired(false)
            .build()

        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    resultChannel.trySend(BiometricResult.Success)
                }
                override fun onAuthenticationFailed() {
                    resultChannel.trySend(BiometricResult.Failed)
                }
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            resultChannel.trySend(BiometricResult.FallbackRequested)
                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            resultChannel.trySend(BiometricResult.NotEnrolled)
                        }
                        else -> {
                            resultChannel.trySend(BiometricResult.Error(errorCode, errString.toString()))
                        }
                    }
                }
            }
        )
        prompt.authenticate(promptInfo)
    }
}
```

**How to inject into Compose screen:**
```kotlin
// BiometricUnlockScreen.kt
@Composable
fun BiometricUnlockScreen(
    onUnlockSuccess: () -> Unit,
    onFallbackToPin: () -> Unit,
) {
    val activity = LocalContext.current as AppCompatActivity
    val biometricManager = remember { BiometricPromptManager(activity) }
    
    // Collect results
    LaunchedEffect(Unit) {
        biometricManager.results.collect { result ->
            when (result) {
                is BiometricPromptManager.BiometricResult.Success -> onUnlockSuccess()
                is BiometricPromptManager.BiometricResult.FallbackRequested -> onFallbackToPin()
                is BiometricPromptManager.BiometricResult.NotEnrolled -> onFallbackToPin()
                is BiometricPromptManager.BiometricResult.HardwareUnavailable -> onFallbackToPin()
                else -> { /* Failed — prompt re-shows automatically */ }
            }
        }
    }
    
    // Show prompt immediately on screen entry
    LaunchedEffect(Unit) {
        biometricManager.showPrompt()
    }
    
    // UI: simple centered layout while biometric prompt is active
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Touch the sensor to unlock",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = { biometricManager.showPrompt() }) {
                Text("Try again")
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onFallbackToPin) {
                Text("Use PIN instead")
            }
        }
    }
}
```

---

## DESIGN-R2-02: PIN Dots — Correct Implementation

```kotlin
// Shared composable: PinDotsRow.kt

@Composable
fun PinDotsRow(
    currentLength: Int,
    maxLength: Int = 8,
    hasError: Boolean = false,
    modifier: Modifier = Modifier
) {
    val shakeOffset = remember { Animatable(0f) }
    
    LaunchedEffect(hasError) {
        if (hasError) {
            // Shake animation
            repeat(4) {
                shakeOffset.animateTo(10f, tween(50))
                shakeOffset.animateTo(-10f, tween(50))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }
    
    Row(
        modifier = modifier.offset(x = shakeOffset.value.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(maxLength) { index ->
            val isFilled = index < currentLength
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(
                        color = when {
                            hasError -> MaterialTheme.colorScheme.error
                            isFilled -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                        },
                        shape = CircleShape
                    )
                    .scale(if (isFilled) 1.1f else 1f) // subtle scale on fill
            )
        }
    }
}
```

**PinSetupScreen — state flow:**
```kotlin
enum class PinSetupStep { ENTER_PIN, CONFIRM_PIN }

data class PinSetupUiState(
    val step: PinSetupStep = PinSetupStep.ENTER_PIN,
    val pin: String = "",                    // current input
    val confirmedPin: String = "",           // stored first entry during CONFIRM step
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val maxPinLength: Int = 8,
    val isComplete: Boolean = false
)

// In ViewModel:
fun onDigitEntered(digit: Int) {
    if (state.pin.length >= state.maxPinLength) return
    val newPin = state.pin + digit.toString()
    
    if (state.step == PinSetupStep.ENTER_PIN && newPin.length >= 4) {
        // Show confirm button or auto-advance at maxLength
        if (newPin.length == state.maxPinLength) {
            advanceToConfirm(newPin)
        } else {
            state = state.copy(pin = newPin)
        }
    } else if (state.step == PinSetupStep.CONFIRM_PIN) {
        val updated = state.pin + digit.toString()
        if (updated.length == state.confirmedPin.length) {
            // Auto-verify
            verifyAndSave(updated)
        } else {
            state = state.copy(pin = updated)
        }
    } else {
        state = state.copy(pin = newPin)
    }
}

fun onBackspace() {
    if (state.pin.isNotEmpty()) {
        state = state.copy(pin = state.pin.dropLast(1), hasError = false)
    }
}

fun onConfirmStep(pin: String) {
    state = state.copy(
        step = PinSetupStep.CONFIRM_PIN,
        confirmedPin = pin,
        pin = ""     // clear for re-entry
    )
}

fun verifyAndSave(confirmEntry: String) {
    if (confirmEntry == state.confirmedPin) {
        pinStorage.savePin(confirmEntry)
        preferencesRepository.setPinEnabled(true)
        state = state.copy(isComplete = true)
    } else {
        state = state.copy(
            hasError = true,
            errorMessage = "PINs do not match. Try again.",
            step = PinSetupStep.ENTER_PIN,
            pin = "",
            confirmedPin = ""
        )
    }
}
```

---

## DESIGN-R2-03: Tab Screen TopAppBar — No Back Arrow

```kotlin
// GeneratorScreen.kt — correct TopAppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneratorScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Generator",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                // NO navigationIcon parameter — omit it entirely
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            // generator content
        }
    }
}
```

Apply same pattern to `VaultHealthScreen` (title = "Health") and `SettingsScreen` (title = "Settings").

---

## DESIGN-R2-04: MainScaffold innerPadding Propagation

```kotlin
// MainScaffold.kt — correct innerPadding usage

@Composable
fun MainScaffold(rootNavController: NavHostController) {
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            TruvaltBottomBar(navController = tabNavController)
        }
    ) { innerPadding ->
        // Pass innerPadding to NavHost so all tab screens receive it
        NavHost(
            navController = tabNavController,
            startDestination = "vault_graph",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)            // ← consume top (none) + bottom (nav bar height)
                .consumeWindowInsets(innerPadding) // ← prevent children from double-applying
        ) {
            navigation(startDestination = "vault_home", route = "vault_graph") {
                composable("vault_home") {
                    VaultHomeScreen(tabNavController, rootNavController)
                }
                composable("vault_detail/{id}") { ... }
                composable("add_item/{type}") { ... }
                composable("edit_item/{id}") { ... }
            }
            navigation(startDestination = "generator", route = "generator_graph") {
                composable("generator") {
                    GeneratorScreen(tabNavController)
                }
            }
            navigation(startDestination = "health", route = "health_graph") {
                composable("health") {
                    VaultHealthScreen(tabNavController)
                }
            }
            navigation(startDestination = "settings", route = "settings_graph") {
                composable("settings") {
                    SettingsScreen(tabNavController, rootNavController)
                }
                composable("security_settings") {
                    SecuritySettingsScreen(tabNavController)
                }
                composable("audit_log") { ... }
                composable("session_manager") { ... }
            }
        }
    }
}
```

Each tab screen's own Scaffold should set `contentWindowInsets = WindowInsets(0)` to avoid
double insets since MainScaffold already handles the bottom:

```kotlin
// In GeneratorScreen, VaultHealthScreen, SettingsScreen, VaultHomeScreen:
Scaffold(
    contentWindowInsets = WindowInsets(0),   // ← prevent double insets
    topBar = { /* ... */ }
) { innerPadding ->
    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier.consumeWindowInsets(innerPadding)
    ) { ... }
}
```

---

## DESIGN-R2-05: SettingsScreen — Security Section Restored

```kotlin
// SettingsScreen.kt — complete structure

@Composable
fun SettingsScreen(navController: NavController, rootNavController: NavController) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings", style = MaterialTheme.typography.headlineMedium,
                         fontWeight = FontWeight.Bold)
                }
                // No navigationIcon
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            modifier = Modifier.fillMaxSize().consumeWindowInsets(innerPadding)
        ) {

            // ───────── SECURITY (FIRST SECTION) ─────────
            item {
                SettingsSectionHeader("Security")
            }
            item {
                SettingsToggleRow(
                    icon = Icons.Rounded.Fingerprint,
                    title = "Biometric Unlock",
                    subtitle = if (uiState.isBiometricEnabled) "Enabled" else "Disabled",
                    checked = uiState.isBiometricEnabled,
                    onCheckedChange = { viewModel.toggleBiometric(it) },
                    enabled = uiState.biometricAvailable
                )
            }
            item {
                SettingsToggleRow(
                    icon = Icons.Rounded.Pin,
                    title = "PIN Lock",
                    subtitle = when {
                        uiState.isPinEnabled -> "Tap to change PIN"
                        else -> "Not set up"
                    },
                    checked = uiState.isPinEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            rootNavController.navigate("pin_setup")
                        } else {
                            viewModel.showDisablePinDialog()
                        }
                    }
                )
            }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.Timer,
                    title = "Auto-lock",
                    subtitle = uiState.autoLockLabel,  // e.g. "5 minutes"
                    onClick = { viewModel.showAutoLockDialog() }
                )
            }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.ContentPaste,
                    title = "Clipboard Timeout",
                    subtitle = uiState.clipboardTimeoutLabel,
                    onClick = { viewModel.showClipboardDialog() }
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 4.dp)) }

            // ───────── APPEARANCE ─────────
            item { SettingsSectionHeader("Appearance") }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.Palette,
                    title = "Theme",
                    subtitle = uiState.themeLabel,
                    onClick = { viewModel.showThemeDialog() }
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 4.dp)) }

            // ───────── SYNC ─────────
            item { SettingsSectionHeader("Sync") }
            item {
                SettingsToggleRow(
                    icon = Icons.Rounded.CloudOff,
                    title = "Local-only Mode",
                    subtitle = "Vault stored locally only",
                    checked = uiState.isLocalOnly,
                    onCheckedChange = { viewModel.toggleLocalOnly(it) }
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 4.dp)) }

            // ───────── DATA ─────────
            item { SettingsSectionHeader("Data") }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.Upload,
                    title = "Import",
                    subtitle = "Import from other password managers",
                    onClick = { /* navigate to import */ }
                )
            }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.Download,
                    title = "Export",
                    subtitle = "Export vault to file",
                    onClick = { /* show export dialog */ }
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 4.dp)) }

            // ───────── DANGER ZONE ─────────
            item { SettingsSectionHeader("Danger Zone", color = MaterialTheme.colorScheme.error) }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.DeleteForever,
                    title = "Delete Vault",
                    subtitle = "Permanently delete all data",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.showDeleteVaultDialog() }
                )
            }
            item { Divider(modifier = Modifier.padding(vertical = 4.dp)) }

            // ───────── ACCOUNT ─────────
            item { SettingsSectionHeader("Account") }
            item {
                SettingsClickRow(
                    icon = Icons.Rounded.Lock,
                    title = "Lock Vault",
                    subtitle = "Lock and require authentication",
                    onClick = { viewModel.lockVault(); rootNavController.navigate("unlock_biometric") {
                        popUpTo("main") { inclusive = true }
                    }}
                )
            }

            // Bottom spacer
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}
```

---

## DESIGN-R2-06: Outer NavGraph — Auth Screens Outside MainScaffold

```kotlin
// TruvaltNavGraph.kt

@Composable
fun TruvaltNavGraph() {
    val rootNavController = rememberNavController()
    
    NavHost(
        navController = rootNavController,
        startDestination = "splash"
    ) {
        // ── AUTH SCREENS (no bottom nav, full screen) ──
        composable("splash") {
            SplashScreen(
                onNavigate = { dest ->
                    rootNavController.navigate(dest) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }
        composable("onboarding") {
            OnboardingScreen(
                onComplete = { rootNavController.navigate("main") {
                    popUpTo("onboarding") { inclusive = true }
                }}
            )
        }
        composable("unlock_biometric") {
            BiometricUnlockScreen(
                onUnlockSuccess = { rootNavController.navigate("main") {
                    popUpTo("unlock_biometric") { inclusive = true }
                }},
                onFallbackToPin = { rootNavController.navigate("unlock_pin") {
                    popUpTo("unlock_biometric") { inclusive = true }
                }}
            )
        }
        composable("unlock_pin") {
            PinUnlockScreen(
                onUnlockSuccess = { rootNavController.navigate("main") {
                    popUpTo("unlock_pin") { inclusive = true }
                }},
                onForgotPin = { rootNavController.navigate("unlock_password") }
            )
        }
        composable("unlock_password") {
            MasterPasswordScreen(
                onUnlockSuccess = { rootNavController.navigate("main") {
                    popUpTo("unlock_password") { inclusive = true }
                }}
            )
        }
        composable("pin_setup") {
            // Called from Settings — no bottom nav
            PinSetupScreen(
                onComplete = { rootNavController.popBackStack() },
                onBack = { rootNavController.popBackStack() }
            )
        }
        
        // ── MAIN APP (bottom nav lives here) ──
        composable("main") {
            MainScaffold(rootNavController = rootNavController)
        }
    }
}
```
```

---

### `.kiro/specs/round2-fixes/tasks.md`

```markdown
# Truvalt Round 2 Fixes — Tasks

## [ ] TASK-R2-01: Fix MainActivity + Theme (REQ-R2-01 prerequisite)
Files: `MainActivity.kt`, `res/values/themes.xml`, `res/values-night/themes.xml`

Steps:
- [ ] R2-01-a: Change `class MainActivity : ComponentActivity()` → `class MainActivity : AppCompatActivity()`
- [ ] R2-01-b: Keep `@AndroidEntryPoint` annotation
- [ ] R2-01-c: Keep `enableEdgeToEdge()` call
- [ ] R2-01-d: Update `themes.xml` parent to `Theme.AppCompat.DayNight.NoActionBar`
- [ ] R2-01-e: Add transparent status/nav bar items to theme XML
- [ ] R2-01-f: Verify app still compiles and runs after this change
- [ ] R2-01-g: Run app and confirm Material3 theming still applies correctly

---

## [ ] TASK-R2-02: Fix BiometricPromptManager (REQ-R2-01)
Files: `core/biometric/BiometricPromptManager.kt`, `presentation/ui/auth/BiometricUnlockScreen.kt`, `presentation/ui/auth/BiometricUnlockViewModel.kt`

Steps:
- [ ] R2-02-a: Rewrite `BiometricPromptManager` exactly as in design.md DESIGN-R2-01
- [ ] R2-02-b: Rewrite `BiometricUnlockScreen` with `LaunchedEffect(Unit) { biometricManager.showPrompt() }` — prompt fires immediately
- [ ] R2-02-c: Wire `BiometricResult.Success` → `onUnlockSuccess()` callback
- [ ] R2-02-d: Wire `BiometricResult.FallbackRequested` and `NotEnrolled` → `onFallbackToPin()`
- [ ] R2-02-e: Show "Try again" button and "Use PIN instead" TextButton on the screen
- [ ] R2-02-f: Test: lock vault, reopen app → biometric prompt appears WITHOUT tapping anything

---

## [ ] TASK-R2-03: Fix PIN Dots (REQ-R2-02)
Files: `presentation/ui/shared/PinDotsRow.kt` (CREATE), `presentation/ui/auth/PinSetupScreen.kt`, `presentation/ui/auth/PinSetupViewModel.kt`, `presentation/ui/auth/PinUnlockScreen.kt`

Steps:
- [ ] R2-03-a: Create `PinDotsRow.kt` composable exactly as in design.md DESIGN-R2-02
- [ ] R2-03-b: Include shake animation on `hasError = true`
- [ ] R2-03-c: Replace any hardcoded `repeat(8)` in PinSetupScreen with `PinDotsRow(currentLength = pin.length, maxLength = 8)`
- [ ] R2-03-d: Replace any hardcoded `repeat(8)` in PinUnlockScreen with `PinDotsRow(currentLength = pin.length, maxLength = storedPinLength)`
- [ ] R2-03-e: Update `PinSetupViewModel` with full state machine from design.md (ENTER_PIN → CONFIRM_PIN)
- [ ] R2-03-f: Auto-proceed to CONFIRM_PIN step when user enters 8 digits
- [ ] R2-03-g: On PIN mismatch: set `hasError = true`, reset step to ENTER_PIN
- [ ] R2-03-h: Test: enter 4 digits → 4 filled + 4 empty dots shown. Enter 8 → advances to confirm step.

---

## [ ] TASK-R2-04: Remove Back Arrows from Tab Root Screens (REQ-R2-03)
Files: `presentation/ui/generator/GeneratorScreen.kt`, `presentation/ui/health/VaultHealthScreen.kt`, `presentation/ui/settings/SettingsScreen.kt`

Steps:
- [ ] R2-04-a: In `GeneratorScreen`: find TopAppBar, remove `navigationIcon = { ... }` parameter entirely
- [ ] R2-04-b: In `VaultHealthScreen`: find TopAppBar, remove `navigationIcon = { ... }` parameter entirely
- [ ] R2-04-c: In `SettingsScreen`: find TopAppBar, remove `navigationIcon = { ... }` parameter entirely
- [ ] R2-04-d: Verify VaultHomeScreen TopAppBar also has no back arrow (it has search/filter/more only)
- [ ] R2-04-e: Verify SecuritySettingsScreen STILL has back arrow (it's a sub-screen)
- [ ] R2-04-f: Test: tap Generator tab → no back arrow. Tap back system button → app goes to background or asks to exit.

---

## [ ] TASK-R2-05: Fix Content Clipping (REQ-R2-04)
Files: `MainScaffold.kt`, `GeneratorScreen.kt`, `VaultHealthScreen.kt`, `SettingsScreen.kt`, `VaultHomeScreen.kt`, `AddEditItemScreen.kt`

Steps:
- [ ] R2-05-a: In `MainScaffold`: apply `Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)` to NavHost
- [ ] R2-05-b: In every tab screen's own Scaffold: add `contentWindowInsets = WindowInsets(0)` to prevent double insets
- [ ] R2-05-c: In every tab screen's LazyColumn: use `contentPadding = innerPadding` and `modifier = Modifier.consumeWindowInsets(innerPadding)`
- [ ] R2-05-d: In `GeneratorScreen`: ensure "Use this password" button is the last LazyColumn item and not clipped
- [ ] R2-05-e: In `SettingsScreen`: ensure "Lock Vault" row at bottom is fully visible
- [ ] R2-05-f: In `AddEditItemScreen`: ensure Save button is visible without scrolling on standard 6" phone
- [ ] R2-05-g: Test on device: scroll to bottom of Settings → all rows visible. Generator → Use button fully visible.

---

## [ ] TASK-R2-06: Restore Security Section in SettingsScreen (REQ-R2-05)
Files: `presentation/ui/settings/SettingsScreen.kt`, `presentation/ui/settings/SettingsViewModel.kt`

Steps:
- [ ] R2-06-a: Rewrite `SettingsScreen` with complete section structure from design.md DESIGN-R2-05
- [ ] R2-06-b: Security section MUST be first, above Appearance
- [ ] R2-06-c: Add `isBiometricEnabled: Boolean` and `biometricAvailable: Boolean` to `SettingsUiState`
- [ ] R2-06-d: Add `isPinEnabled: Boolean` to `SettingsUiState`
- [ ] R2-06-e: In `SettingsViewModel`: read both values from DataStore in `init { }` block
- [ ] R2-06-f: `toggleBiometric()` → check BiometricManager availability before enabling → write to DataStore
- [ ] R2-06-g: PIN toggle → if enabling → `rootNavController.navigate("pin_setup")`, if disabling → confirmation dialog → clear PIN storage
- [ ] R2-06-h: Test: open Settings → Security section visible at top with Biometric and PIN toggles

---

## [ ] TASK-R2-07: Remove Bottom Nav from Auth Screens (REQ-R2-06)
Files: `TruvaltNavGraph.kt`, `MainScaffold.kt`

Steps:
- [ ] R2-07-a: Rewrite `TruvaltNavGraph.kt` with outer NavHost exactly as in design.md DESIGN-R2-06
- [ ] R2-07-b: Move `pin_setup` route from `settings_graph` to the outer NavHost
- [ ] R2-07-c: In `SettingsScreen` PIN toggle, use `rootNavController.navigate("pin_setup")` (pass rootNavController into SettingsScreen)
- [ ] R2-07-d: In `PinSetupScreen.onComplete`: call `rootNavController.popBackStack()` to return to Settings
- [ ] R2-07-e: Verify `SplashScreen` has no bottom nav (it's in outer NavHost)
- [ ] R2-07-f: Verify `BiometricUnlockScreen` has no bottom nav
- [ ] R2-07-g: Verify `PinUnlockScreen` has no bottom nav
- [ ] R2-07-h: Verify `PinSetupScreen` has no bottom nav
- [ ] R2-07-i: Test: lock vault → unlock screen shown full-screen, no nav bar at bottom

---

## [ ] TASK-R2-08: Fix Seed Data Loading (REQ-R2-07)
Files: `data/local/SeedDataInserter.kt`, `presentation/ui/vault/VaultHomeViewModel.kt`, `data/preferences/AppPreferences.kt`

Steps:
- [ ] R2-08-a: Add log statements to `SeedDataInserter.insertIfEmpty()` to confirm it is being called
- [ ] R2-08-b: Move seed insertion call to `VaultHomeViewModel.init {}` block — AFTER vault is unlocked and key is available
- [ ] R2-08-c: Check `isFirstLaunch()` from DataStore — if true, call `insertSeedData()`, then `setFirstLaunchDone()`
- [ ] R2-08-d: Wrap entire seed insertion in `try-catch` — log any encryption errors, do NOT silently fail
- [ ] R2-08-e: After insertion, call `loadVaultItems()` to refresh the UI
- [ ] R2-08-f: If Room database is being wiped on each launch (e.g. `fallbackToDestructiveMigration` + schema change), add a proper migration or bump DB version
- [ ] R2-08-g: Test: fresh install → unlock → 16 items shown across all categories
- [ ] R2-08-h: Test: close and reopen app → seed NOT re-inserted, same 16 items shown
- [ ] R2-08-i: Open Health tab → "X items analyzed" (not "0 items analyzed")

---

## Final Verification

- [ ] FV-01: Open app → biometric prompt fires automatically (no button needed)
- [ ] FV-02: Enter 4 digits in PIN screen → 4 filled dots + 4 empty dots (not 8 filled)
- [ ] FV-03: Tap Generator in bottom nav → NO back arrow in TopAppBar
- [ ] FV-04: Tap Health in bottom nav → NO back arrow in TopAppBar
- [ ] FV-05: Tap Settings in bottom nav → NO back arrow in TopAppBar
- [ ] FV-06: Scroll to bottom of any screen → last item fully visible (not clipped)
- [ ] FV-07: Open Settings → Security section with Biometric and PIN toggles visible AT TOP
- [ ] FV-08: Lock vault → unlock screens have NO bottom navigation bar
- [ ] FV-09: PinSetupScreen from Settings → NO bottom navigation bar visible
- [ ] FV-10: Fresh install → 16 seed items visible across all 8 type filter chips
- [ ] FV-11: Health screen → shows actual item count, not "0 items analyzed"
```

---

## SECTION C — EXECUTION SEQUENCE

```bash
# In kiro-cli chat:

# Step 1 — Update steering
"Append the edge-to-edge, BiometricPrompt, auth screen, and tab screen rules 
 to .kiro/steering/tech.md as specified in the prompt."

# Step 2 — Create spec
"Create .kiro/specs/round2-fixes/requirements.md, design.md, and tasks.md 
 exactly as provided."

# Step 3 — Execute in order (CRITICAL: do NOT skip TASK-R2-01)
"Implement TASK-R2-01 from .kiro/specs/round2-fixes/tasks.md completely.
 This is a prerequisite for all other tasks. Do not stub or truncate any file."

"Implement TASK-R2-02. Write BiometricPromptManager and BiometricUnlockScreen 
 exactly as in design.md. The prompt MUST fire in LaunchedEffect(Unit), not on button tap."

"Implement TASK-R2-03. Create PinDotsRow.kt composable and rewrite PinSetupScreen 
 and PinUnlockScreen. No hardcoded repeat(8). Dots must be dynamic."

"Implement TASK-R2-04. Remove navigationIcon from GeneratorScreen, VaultHealthScreen, 
 and SettingsScreen TopAppBars. Write the full files."

"Implement TASK-R2-05. Fix all innerPadding / WindowInsets issues in MainScaffold 
 and all tab screens. Add contentWindowInsets = WindowInsets(0) to all tab screen Scaffolds."

"Implement TASK-R2-06. Restore the Security section in SettingsScreen. It must be 
 the FIRST section. Include Biometric Unlock and PIN Lock rows. Write the full file."

"Implement TASK-R2-07. Rewrite TruvaltNavGraph.kt so auth screens are in the outer 
 NavHost. Move pin_setup route outside of MainScaffold. Full file, no stubs."

"Implement TASK-R2-08. Fix seed data: move insertion to VaultHomeViewModel.init{}, 
 add error logging, ensure it fires after vault unlock."

# Step 4 — Final verification
"Run through every item in the Final Verification checklist in tasks.md.
 For each item, confirm the code satisfies it or explain what still needs fixing."
```

## AGENT RULES (include with every task)
> Write every function body completely. No stubs, no TODOs.
> TASK-R2-01 (MainActivity → AppCompatActivity) MUST be done first.
> Every Scaffold in a tab screen MUST have contentWindowInsets = WindowInsets(0).
> Every LazyColumn in a tab screen MUST use contentPadding = innerPadding.
> BiometricPrompt MUST fire in LaunchedEffect(Unit) — NOT on any button click.
> PinDotsRow MUST use a variable for maxPinLength — NOT a hardcoded 8.
> Auth screens MUST be in the outer NavHost, NOT inside MainScaffold.
