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
