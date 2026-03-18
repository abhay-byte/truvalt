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
