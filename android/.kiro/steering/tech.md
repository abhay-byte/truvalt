# Technical Steering

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
