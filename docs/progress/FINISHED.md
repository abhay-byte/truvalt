# FINISHED - Completed Tasks

---

## Summary

| Completed Date | Task Count |
| 2026-05-01 | 3 |
| 2026-04-17 | 1 |
| 2026-04-08 | 2 |
| 2026-04-07 | 2 |
| 2026-04-06 | 4 |
| 2026-04-02 | 3 |
| 2026-03-30 | 1 |
| 2026-03-29 | 10 |
| 2026-03-23 | 8 |
| 2026-03-22 | 2 |
| 2026-03-20 | 3 |
| 2026-03-19 | 7 |
| 2026-03-18 | 8 |
| 2026-03-17 | 5 |
| 2026-03-16 | 21 |

---

## Completed Tasks

| Task ID | Description | Completed Date | Notes |
|---|---|---|---|
| TASK-024 | Export encrypted .truvalt format | 2026-05-01 | Implemented `exportToEncryptedTruvalt()` in `ImportExportService` using AES-256-GCM + Argon2id password-derived key. Exports items, folders, tags, and item-tag mappings in a versioned JSON envelope. |
| TASK-025 | Export unencrypted JSON | 2026-05-01 | Implemented `exportToJson()` producing plaintext JSON with full vault data. Items include decrypted JSON payloads. Compatible with `importTruvaltJson()`. |
| TASK-026 | Export unencrypted CSV | 2026-05-01 | Implemented `exportToCsv()` generating login-only CSV with name, type, url, username, password, notes columns. Non-login items export payload as notes. |
| TASK-073 | Remove Laravel backend — Firebase-direct architecture | 2026-04-28 | Deleted `backend/` docs, `render.yaml`, Android Retrofit API classes (`BackendApiFactory`, `TruvaltApiService`, `BackendDtos`), and `ServerSetupScreen`/`ViewModel`. Renamed DataStore keys `backend*` → `firebase*`. Removed `serverUrl` preference and sync repository methods. Updated `NavHost` to route onboarding → Login directly. Rewrote `SRS.md` and `FEATURES.md` to reflect Firebase-direct architecture. |
| TASK-072 | Android Autofill Service | 2026-04-17 | Implemented TruvaltAutofillService for password autofill in apps and browsers. Added AutofillPermissionScreen after onboarding. Supports credential filling and saving new logins. Works with both native apps and WebView websites. |
| TASK-071 | Rehaul Vault Item Cards and Item Details Screen | 2026-04-08 | Overhauled `VaultItemCard.kt` and `VaultItemDetailScreen.kt` according to `DESIGN.md` guidelines ("The Fortified Sanctuary"). Implemented live 2FA countdowns, enhanced data display (usernames/URLs), and tonal layering. |
| TASK-037 | Inline TOTP code generation with countdown | 2026-04-08 | Added TOTP display on vault item cards and detail screen, fixed QR scanner to portrait mode, improved image QR detection with multiple binarizer strategies, optimized Gradle build for ARM64. |
| TASK-R2-10 | Fix crash on startup due to Room schema mismatch | 2026-04-07 | Incremented Room version to 2 and added fallbackToDestructiveMigration in AppModule.kt to fix IllegalStateException. |
| TASK-R2-09 | Build and run APK on device | 2026-04-07 | Connected to device 192.168.137.114:44049, fixed missing Log import in SyncRepositoryImpl.kt, built and installed APK. |
| TASK-019 | Delta sync with timestamps | 2026-04-06 | Implemented true delta-based synchronization for vault items, folders, and tags. The app now tracks `updated_at` and `deleted_at` timestamps for all entities, fetching only modified records since the last sync. This reduces bandwidth and improves performance. |
| TASK-020 | Conflict resolution (last-write-wins) | 2026-04-06 | Implemented Last-Write-Wins (LWW) conflict resolution logic in `SyncRepositoryImpl`. During the push phase, local changes are only applied if they are newer than the server version. During the pull phase, remote changes updated after `lastSyncTime` are merged into Room, ensuring the latest version prevails across all devices. |
| TASK-051 | Passkey login (WebAuthn FIDO2) | 2026-04-02 | Implemented WebAuthn FIDO2 passkey login flow for Android, allowing users to sign in using biometric or device security instead of passwords. |
| TASK-069 | Rehaul Security Settings & UI Cleanup | 2026-04-06 | Overhauled `SecuritySettingsScreen.kt` to follow the "Fortified Sanctuary" design system from `DESIGN.md`. Implemented the "Biometric Trigger" hero element with a central surface-container-lowest circle and primary glow. Removed redundant "Auto-lock" and "Clipboard Timeout" settings from the main `SettingsScreen.kt` and moved them to `SecuritySettingsScreen.kt` (or kept them there) for better grouping. Applied editorial typography and tonal layering across the security suite. Verified dark mode contrast and successful build/install. |
| TASK-070 | Opaque TopBars for Scrolled Screens | 2026-04-06 | Fixed transparent `TopAppBar` in `AddEditItemScreen`, `VaultItemDetailScreen`, `VaultItemTypeSelectionScreen`, `SettingsScreen`, `SecuritySettingsScreen`, `AppearanceSettingsScreen`, and `TrashScreen` to prevent content overlap during scroll. Wrapped `TruvaltTopAppBar` in a solid `Surface` using `palette.background` to ensure opacity in Home, Generator, and Health screens. Applied explicit `TopAppBarDefaults` colors to ensure consistent "The Fortified Sanctuary" tonal layering. |
| TASK-067 | Auth Animations & Theme Contrast Improvements | 2026-04-02 | Added `AuthLoadingOverlay` with `CircularProgressIndicator` and full-screen touch interception to `LoginScreen` and `RegisterScreen` to prevent "stuck" UX during Google OAuth and network calls. Fixed Dark and AMOLED theme readability by increasing `onSurfaceVariant` contrast from `0xFFA1AAC2` to `0xFFC1C9E0` and `outline` to `0xFF8E99B3`. Updated `VaultHomePalette` to use `onSurfaceVariant` for body and muted text. Verified via release APK installation. |
| TASK-050 | Delete Account — backend + Android UI + static website | 2026-04-02 | Added `DELETE /account` route to Laravel API (AuthController + FirebaseAuthService + TruvaltFirestoreRepository). Server-side deletion wipes all Firestore subcollections (vault_items, folders, tags, user profile) then deletes the Firebase Auth user via Admin SDK. Android: added `deleteAccount()` to AuthRepositoryImpl (calls backend then signs out locally), `DeleteAccountState` sealed class + `deleteAccount()` in SettingsViewModel, and a cloud-only "Delete Account" row in the Danger Zone with a 2-step dialog (warning screen → mandatory type-DELETE confirmation). Created `delete-account-site/public/index.html` (premium dark static page) and `firebase.json`. Updated API.md, TODO.md, ONGOING.md, FINISHED.md. Static site ready to deploy via `firebase deploy --project tru-valt`. |
| TASK-046 | Biometric unlock (Android) | 2026-03-30 | Fixed the startup routing so biometric unlock is actually honored when enabled, restored the encrypted vault key during biometric and PIN unlock after app relaunch/process death, tightened the Security Settings biometric toggle to only enable when a keystore-backed encrypted vault key exists, rebuilt the debug APK, and verified install/launch on the connected Android device |
| UI-009 | Unified "Fortified Sanctuary" Design System | 2026-03-29 | Integrated Manrope variable font with tight letter tracking, rebuilt core Theme.kt to properly map the custom Vault dark mode natively to DarkColorScheme while maintaining LightColorScheme tokens, removed shadow elevations from Auth screens, and replaced the hardcoded VaultHomePalette with dynamic MaterialTheme components for true application-wide synchrony. |
| UI-007 | Implement Material 3 Theme in Auth & Onboarding | 2026-03-29 | Removed hardcoded background and surface colors from OnboardingScreen and AuthUIComponents. Replaced them with Composable getters bound to MaterialTheme.colorScheme to ensure the gateway respected Dark/Light system modes correctly |
| FIX-008 | Add account sign-out action in Settings | 2026-03-29 | Added a dedicated Settings sign-out action that signs out the current account session, clears cached backend auth tokens, locks the local vault, routes back to Login, and updated the Settings UI/docs before rebuilding and reinstalling the debug APK |
| TASK-018 | Complete Firebase auth/cloud flow and premium Android UX refinement | 2026-03-29 | Finalized direct Android → Firebase Auth + Firestore flow, fixed Google sign-in configuration and Android Keystore IV handling, added signed-in account details in Settings, refreshed onboarding/splash/startup routing, redesigned Vault Home and Generator with dark/light-aware premium UI, verified builds, installed the APK via `adb`, and updated all affected docs/progress tracking |
| UI-005 | Correct App Bar logo and Vault theme | 2026-03-29 | Removed hardcoded background colors in VaultHomeScreen and VaultItemCard; added truvalt_icon to Top Bar; switched text color styling to respect dark/light Material 3 theme scheme |
| UI-004 | Redesign Vault Home Screen | 2026-03-29 | Replaced generic Material TopAppBar and NavigationBar with a custom glass-like header and floating bottom navigation, revamped filter chips, and introduced category-based colored `VaultItemCard` layouts to match the new high-fidelity html design. |
| UI-006 | Finalize Health Dashboard UI and Nav Bar rendering | 2026-03-29 | Corrected Health Gauge overlapping pill mask, mapped ActionItems dynamically to HealthUiState, removed Scaffold padding constraints to allow screen gradients to render correctly behind the custom curved bottom nav bar, adapted bottom icons to respect selected/outlined material states, and enforced Top Bar brand logo light-theme tinting. |
| UI-003 | Redesign Auth Gateway & Security Suite | 2026-03-29 | Overhauled the Server Setup, Login, and Registration screens with a premium design system, including a password strength meter, security status cards, and a unified gateway hub. |
| UI-002 | Redesign Onboarding Screens | 2026-03-29 | Implemented a 3-slide HorizontalPager flow with high-fidelity Compose illustrations, smooth page indicators, and Material 3 styling based on Tailwind designs |
| UI-001 | Redesign Splash Screen | 2026-03-29 | Translated Tailwind HTML into Compose UI; Added new brand colors, radial gradient background, and custom spinning animation loader |
| FEAT-003 | Integrate Android cloud mode with the Firebase-backed Laravel API foundation | 2026-03-23 | Added Android Retrofit DTO/service/factory layers, persisted backend Firebase session tokens in DataStore, wired register/login to the Laravel backend in cloud mode, wired sync for folders/tags/vault items, and verified the app still builds successfully with `assembleDebug` |
| CHORE-005 | Add Render build/run scripts and refresh Android auth-entry workflow docs | 2026-03-23 | Added `web/render-build.sh` and `web/render-run.sh`, documented the exact Render Firebase/file-runtime variables and Docker flow, updated the server setup UX to explicitly branch to sign-up vs login, and copied the latest debug APK to `/sdcard/Download/Truvalt-20260323-1253.apk` |
| TEST-002 | Complete live Firebase + Firestore backend regression verification | 2026-03-23 | Wired the real Truvalt Firebase service-account credentials into Laravel, completed live email/password auth testing, verified Firestore-backed folders/tags/vault CRUD plus sync conflict handling, and confirmed logout revocation plus unauthenticated JSON `401` responses |
| FEAT-002 | Pivot Laravel backend auth/storage to Firebase Authentication and Cloud Firestore | 2026-03-23 | Replaced Sanctum-protected route middleware with Firebase ID token verification, added email/password and Google sign-in endpoints, moved vault/folder/tag/user persistence to Firestore via REST, refreshed the API test script and PHPUnit coverage, and updated deployment/design/API docs for the new backend model |
| CHORE-004 | Switch backend to an IPv4-reachable external PostgreSQL instance and complete live API verification/docs sync | 2026-03-23 | Repointed Laravel to the new external PostgreSQL connection, verified DNS/TCP/PDO connectivity, rebuilt and seeded the schema, completed authenticated API regression coverage, fixed the unauthenticated JSON `401` response path, and synchronized `/docs` to the verified backend behavior |
| FIX-007 | Align Sanctum personal access tokens with UUID users | 2026-03-23 | Updated the `personal_access_tokens` migration from `morphs()` to `uuidMorphs()` and rebuilt the external PostgreSQL schema so API token creation works with UUID-backed users |
| SEC-001 | Harden backend auth key storage and vault ownership validation | 2026-03-23 | Server now stores Argon2id-hashed auth key material, verifies it on login, validates `folder_id` / `parent_id` ownership, and rejects invalid base64 payloads for encrypted vault data |
| TEST-001 | Refresh Laravel seeding and API regression tooling for external PostgreSQL | 2026-03-23 | Reworked the database seeder and user factory for the actual schema, removed the `jq` dependency from the API script, and verified the implemented route set including health, keep-alive, auth, vault CRUD/filtering/trash/restore/sync, folders, tags, logout, and unauthenticated `401` handling |
| CHORE-003 | Switch backend to managed external PostgreSQL | 2026-03-22 | Configured Laravel to use external `DB_URL` with `DB_SSLMODE=require`; updated ignored local `.env`; changed the Render Blueprint to expect a manually supplied secret `DB_URL` instead of provisioning Render Postgres; updated deployment/backend docs |
| CHORE-002 | Harden Render deployment and add keep-alive endpoint | 2026-03-22 | Moved Render Blueprint to repository root; fixed Render env vars to match Laravel (`DB_URL`, `CACHE_STORE`, `QUEUE_CONNECTION=sync`); updated Docker/Nginx for port 10000 and asset builds; registered `routes/api.php` in `bootstrap/app.php`; added public `GET /api/keep-alive`; updated deployment/API/design docs |
| TASK-002 | PostgreSQL schema migrations (all tables) | 2026-03-20 | Created 10 migration files: users, folders, tags, vault_items, vault_item_tags, sessions, audit_logs, passkeys, share_links, devices; added user_id FK to folders and tags; updated ER diagram |
| TASK-001 | Initialize Laravel 12 project structure | 2026-03-20 | Created web/ directory with Laravel structure; created 9 Eloquent models (User, VaultItem, Folder, Tag, Session, AuditLog, Passkey, ShareLink, Device) with relationships; created SETUP.md with installation instructions; ready for composer install when PHP/Composer available |
| BUG-011 | Implement master password for local-only vault | 2026-03-20 | Created MasterPasswordSetupScreen/ViewModel for initial vault creation; created MasterPasswordUnlockScreen/ViewModel for unlocking; added deriveKey() method in CryptoManager for local-only mode (fixed salt); added setWrappedVaultKey/getWrappedVaultKey in TruvaltPreferences; updated navigation flow (ServerSetup→MasterPasswordSetup→Vault); updated SplashScreen to use UNLOCK_MASTER_PASSWORD; vault key now properly generated and stored on first launch in local-only mode |
| BUG-010 | Fix "vault not unlocked" error when saving items | 2026-03-19 | Added getVaultKey() method in VaultRepositoryImpl to dynamically get key from AuthRepository; injected AuthRepository into VaultRepositoryImpl; updated LoginViewModel to set vault key after login; fixed toEntity() and toDomain() methods to use getVaultKey() |
| BUG-009 | Update password generator to 128 chars & add camera for QR | 2026-03-19 | Updated password generator dialog length slider from 32 to 128 characters (matching main generator page); added camera scanning option using ZXing ScanContract; added CAMERA permission to manifest |
| FEAT-001 | Add password auto-generate & QR code scanner for 2FA | 2026-03-19 | Added password generator dialog in login creation with length slider (8-32) and character options; added QR code scanner for TOTP seed using ZXing library; supports gallery image selection; extracts secret from otpauth:// URLs |
| BUG-008 | Fix passphrase refresh button & duplicate passphrases | 2026-03-19 | Fixed generatePassphrase to use random words from wordlist; added refresh key to force state update; passphrases now unique on each generation |
| BUG-007 | Fix passphrase refresh button & add passphrase count/length options | 2026-03-19 | Fixed refresh button to regenerate passphrases correctly; added word count slider (3-8 words); added passphrase count slider (1-10); displays all generated passphrases; copies all to clipboard |
| BUG-006 | Remove emoji from vault filter text & add quit app menu option | 2026-03-19 | Removed emoji from filter chip labels (Logins, Passkeys, etc.); added "Quit App" option in top menu with divider |
| BUG-005 | Fix PIN dots to be dynamic (min 4, grow with input) | 2026-03-19 | Updated PinDotsRow to show minimum 4 dots, dynamically grow as user types; setup screen shows dynamic dots, unlock screen shows fixed dots based on stored PIN length |
| FIX-006 | Fix app lock not triggering on relaunch | 2026-03-18 | Created SplashViewModel; added MainActivity lifecycle callbacks (onPause/onResume); fixed hardcoded isLocked state; immediate lock now works when app is closed |
| FIX-005 | Fix biometric authentication and security settings | 2026-03-18 | Fixed duplicate biometric toggle; connected toggle to ViewModel; added auto-lock dialog with "Immediately" option; improved security settings UI |
| FIX-004 | Build fixes and UI improvements | 2026-03-18 | Fixed SeedDataInserter encryption method (encryptData→encryptWithKeystore); removed duplicate bottom navigation bar from VaultHomeScreen; app now builds successfully |
| FIX-003 | Round 2 Critical Fixes (MainActivity, Biometric, PIN, Navigation, Content Clipping, Seed Data) | 2026-03-18 | Fixed 8 tasks: MainActivity→AppCompatActivity for BiometricPrompt; BiometricPromptManager with auto-trigger; PinDotsRow with dynamic dots; removed back arrows from tab screens; fixed innerPadding/WindowInsets; moved PinSetup to outer NavHost; SeedDataInserter with first-launch tracking; 3 new files, 15 modified |
| CHORE-001 | Update .gitignore to exclude build files | 2026-03-18 | Added comprehensive build exclusions (**/build/, **/out/, **/.gradle/, **/.kotlin/, **/generated/, **/intermediates/, **/tmp/, **/outputs/); removed cached build files from git; prevents committing build artifacts |
| FIX-002 | Critical Bug Fixes (Biometric, PIN, Lock, Splash, Seed, Generator, TOTP, Bottom Nav) | 2026-03-18 | Implemented 7 tasks: BiometricUnlock with Keystore, PIN lock with Argon2id, AppLockManager, SplashScreen with branding, 16 seed items, generator 128 chars, TOTP auto-refresh, nested navigation graphs; 13 new files created; APK: Truvalt-20260318-0645.apk (24MB) |
| FIX-001 | Complete UI/UX overhaul of VaultHomeScreen and AddEditItemScreen | 2026-03-18 | Fixed app branding (Truvalt), rewrote VaultHomeScreen with search bar, filter chips, bottom navigation, AddItemTypeSheet modal; created VaultItemCard, PasswordStrengthBar, AddItemTypeSheet components; created type-aware AddEditItemScreen with proper validation; APK built successfully |
| TASK-010 | Vault item type system (8 types) | 2026-03-17 | VaultItemType sealed class, type selection screen, comprehensive edit screen with Login/Passphrase/SecureNote/SecurityCode/CreditCard/Identity/Passkey support |
| BUG-001 | Fix local-only mode to skip auth and go directly to vault | 2026-03-17 | ServerSetup → VaultHome in local-only, Splash → VaultHome if local-only configured |
| BUG-002 | Fix KSP + Hilt compatibility issue | 2026-03-17 | Switched Hilt to KAPT, upgraded Kotlin 2.0.21→2.1.21 |
| BUG-003 | Fix BouncyCastle Argon2 API usage | 2026-03-17 | Fixed Algorithm enum and withMemory() calls |
| BUG-004 | Fix VaultItemEditScreen showing placeholder | 2026-03-17 | Removed PlaceholderScreens.kt, created VaultItemDetailScreen and TrashScreen |
| TASK-000 | Project and documentation initialized | 2026-03-16 | All /docs files created |
| TASK-003 | Implement Argon2id key derivation (Android) | 2026-03-16 | CryptoManager with BouncyCastle |
| TASK-004 | Implement AES-256-GCM encryption (Android) | 2026-03-16 | CryptoManager with AES-GCM |
| TASK-008 | Email + password authentication (Android) | 2026-03-16 | Login/Register screens |
| TASK-009 | Room database setup | 2026-03-16 | VaultItem, Folder, Tag entities |
| TASK-011 | Read vault items list | 2026-03-16 | VaultHomeScreen with list |
| TASK-017 | Local-only mode | 2026-03-16 | SyncRepository with offline support |
| TASK-033 | Server URL configuration | 2026-03-16 | ServerSetupScreen |
| TASK-010 | Create vault item (Login type) | 2026-03-16 | VaultItemEditScreen implemented |
| TASK-014 | Password generator (random string) | 2026-03-16 | PasswordGenerator with config options |
| TASK-015 | Passphrase generator (EFF wordlist) | 2026-03-16 | EFF wordlist with word count |
| TASK-016 | Password strength meter | 2026-03-16 | Strength calculation with 5 levels |
| TASK-021 | Import from Bitwarden JSON | 2026-03-16 | ImportExportService with parser |
| TASK-022 | Import from LastPass CSV | 2026-03-16 | CSV parser implemented |
| TASK-023 | Import from Chrome/Firefox CSV | 2026-03-16 | Browser CSV import supported |
| TASK-027 | HIBP breach check | 2026-03-16 | k-anonymity SHA-1 check |
| TASK-028 | Vault health dashboard | 2026-03-16 | HealthScreen with score |
| TASK-031 | Auto-lock timeout | 2026-03-16 | SettingsViewModel with timeout |
| TASK-034 | Clipboard timeout setting | 2026-03-16 | Configurable in settings |
| TASK-047 | Dark/Light/AMOLED theme | 2026-03-16 | Theme options in settings |
