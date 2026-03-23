# Software Design Document (SDD)

---

## 1. System Architecture

### 1.1 Two-Layer Architecture Diagram

```
Android App                          Web Browser
┌─────────────────┐                  ┌──────────────────┐
│  Compose UI     │                  │  Blade + Alpine  │
│  ViewModels     │                  │  (Web Vault UI)  │
│  Use Cases      │◄────HTTPS/REST──►│                  │
│  Repositories   │                  └──────────────────┘
│  Room DB (local)│                           │
└─────────────────┘                           ▼
         │                         ┌─────────────────────┐
         └────────HTTPS/REST──────►│  Laravel 12 API     │
                                    │  Controllers        │
                                    │  Services           │
                                    │  Repositories       │
                                    │  PostgreSQL DB      │
                                    └─────────────────────┘
```

---

## 2. Tech Stack

| Layer | Android | Web/Backend |
|---|---|---|
| Language | Kotlin | PHP 8.3, Blade, Alpine.js |
| UI | Jetpack Compose + Material 3 | Blade templates + Tailwind CSS |
| Architecture | Clean Architecture + MVVM | Laravel MVC + Service/Repo pattern |
| DI | Hilt | Laravel IoC Container |
| Navigation | Compose Navigation | Laravel Router |
| Async | Kotlin Coroutines + Flow | Laravel Queues + async jobs |
| Local DB | Room + SQLCipher | — |
| Server DB | — | PostgreSQL 16 |
| ORM | — | Eloquent |
| Networking | Retrofit 2 + OkHttp 4 | Laravel HTTP Client |
| Crypto | Android Keystore + BouncyCastle (Argon2id) | PHP sodium extension (libsodium) |
| Auth | Email+Password, Biometric, WebAuthn Credential Manager | Laravel Sanctum, TOTP (pragmarx/google2fa), WebAuthn (asbiin/laravel-webauthn) |
| Import/Export | Kotlin serialization | PHP league/csv, Symfony serializer |
| Image loading | Coil | — |
| Testing (Android) | JUnit 5, MockK, Turbine, Espresso | — |
| Testing (Web) | — | PHPUnit, Pest, Laravel Dusk |
| Linting | Detekt, ktlint | Laravel Pint |

---

## 3. Package/Folder Structure

### 3.1 Android

```
android/
└── app/src/main/
    ├── java/com/ivarna/truvalt/
    │   ├── core/
    │   │   ├── crypto/         # AES-256-GCM, Argon2id, key management
    │   │   ├── clipboard/      # Secure clipboard manager
    │   │   ├── extensions/
    │   │   └── utils/
    │   ├── data/
    │   │   ├── local/
    │   │   │   ├── dao/        # Room DAOs (VaultItemDao, FolderDao, etc.)
    │   │   │   ├── entity/     # Room entities
    │   │   │   └── CipherKeepDatabase.kt
    │   │   ├── remote/
    │   │   │   ├── api/        # Retrofit interfaces
    │   │   │   ├── dto/        # Request/Response DTOs
    │   │   │   └── interceptors/
    │   │   ├── repository/     # Repository implementations
    │   │   └── preferences/    # DataStore preferences
    │   ├── domain/
    │   │   ├── model/          # Domain models (VaultItem, Folder, User)
    │   │   ├── repository/     # Repository interfaces
    │   │   └── usecase/        # Use cases grouped by feature
    │   └── presentation/
    │       ├── ui/
    │       │   ├── auth/       # Login, Register, 2FA, Biometric screens
    │       │   ├── vault/      # Vault list, item detail, item edit
    │       │   ├── generator/  # Password & passphrase generator
    │       │   ├── health/     # Vault health dashboard
    │       │   ├── import/     # Import wizard
    │       │   ├── settings/   # Settings, sessions, audit log
    │       │   └── shared/     # Reusable Compose components
    │       ├── theme/          # MaterialTheme, Type.kt, Color.kt
    │       └── navigation/     # NavGraph, Routes
    └── res/
        ├── values/
        ├── values-night/
        └── xml/               # network_security_config, backup_rules
```

### 3.2 Laravel Web Application

```
web/
├── app/
│   ├── Http/
│   │   ├── Controllers/Api/   # API controllers (AuthController, VaultController, etc.)
│   │   ├── Controllers/Web/   # Web vault Blade controllers
│   │   ├── Middleware/        # Auth, 2FA enforcement, rate limiting
│   │   └── Requests/          # Form request validation
│   ├── Models/                # Eloquent models
│   ├── Services/              # Business logic (CryptoService, SyncService, BreachService)
│   ├── Repositories/          # DB access abstraction
│   └── Jobs/                  # Queue jobs (breach check, audit flush)
├── database/
│   ├── migrations/
│   └── seeders/
├── resources/
│   ├── views/                 # Blade templates
│   │   ├── auth/
│   │   ├── vault/
│   │   ├── settings/
│   │   └── layouts/
│   ├── css/                   # Tailwind
│   └── js/                    # Alpine.js components
├── routes/
│   ├── api.php                # All /api/* routes
│   └── web.php                # Blade web vault routes
└── tests/
    ├── Feature/               # Pest feature tests
    └── Unit/                  # Unit tests
```

---

## 4. Database Design

### 4.1 Room (Android Local)

| Table | Column | Type | Constraints |
|---|---|---|---|
| `vault_items` | `id` | TEXT | PK (UUID) |
| | `type` | TEXT | NOT NULL (LOGIN, PASSKEY, PASSPHRASE, NOTE, SECURITY_CODE, CARD, IDENTITY, CUSTOM) |
| | `name` | TEXT | NOT NULL |
| | `folder_id` | TEXT | FK → folders.id, nullable |
| | `encrypted_data` | BLOB | NOT NULL — AES-256-GCM ciphertext |
| | `favorite` | INTEGER | NOT NULL DEFAULT 0 |
| | `created_at` | INTEGER | NOT NULL (epoch ms) |
| | `updated_at` | INTEGER | NOT NULL (epoch ms) |
| | `deleted_at` | INTEGER | nullable (soft delete) |
| | `sync_status` | TEXT | NOT NULL (SYNCED, PENDING_UPLOAD, PENDING_DELETE) |
| `folders` | `id` | TEXT | PK (UUID) |
| | `name` | TEXT | NOT NULL |
| | `icon` | TEXT | nullable |
| | `parent_id` | TEXT | FK → folders.id, nullable (nested folders) |
| | `updated_at` | INTEGER | NOT NULL |
| `tags` | `id` | TEXT | PK (UUID) |
| | `name` | TEXT | NOT NULL UNIQUE |
| `vault_item_tags` | `item_id` | TEXT | FK → vault_items.id |
| | `tag_id` | TEXT | FK → tags.id |
| `sessions` | `id` | TEXT | PK |
| | `device_name` | TEXT | |
| | `last_active_at` | INTEGER | |
| `audit_log` | `id` | TEXT | PK |
| | `action` | TEXT | |
| | `item_id` | TEXT | nullable |
| | `performed_at` | INTEGER | |

### 4.2 PostgreSQL (Server)

Same structure as above, plus:

| Table | Notes |
|---|---|
| `users` | id, email, auth_key_hash (Argon2id of derived auth key), two_factor_secret, two_factor_confirmed_at, emergency_access_*, created_at |
| `devices` | id, user_id, name, platform, push_token, last_seen_at |
| `share_links` | id, item_id, encrypted_item_blob, expires_at, max_views, view_count |
| `passkeys` | id, user_id, credential_id, public_key, sign_count (WebAuthn) |

---

## 5. API Design

Base URL: `https://[your-domain]/api`

### 5.1 Utility Endpoints

| Method | Path | Description |
|---|---|---|
| GET | /health | Render health check endpoint |
| GET | /keep-alive | Public cron/uplink keep-alive endpoint |

### 5.2 Authentication Endpoints

| Method | Path | Description |
|---|---|---|
| POST | /register | Register with email + client-derived auth key material |
| POST | /login | Login with email + client-derived auth key material |
| POST | /logout | Logout current session |
| GET | /me | Get current authenticated user |

### 5.3 Vault Endpoints

| Method | Path | Description |
|---|---|---|
| GET | /vault/items | List all vault items |
| POST | /vault/items | Create vault item |
| GET | /vault/items/{id} | Get vault item |
| PUT | /vault/items/{id} | Update vault item |
| DELETE | /vault/items/{id} | Delete vault item |
| GET | /vault/trash | List soft-deleted vault items |
| POST | /vault/items/{id}/restore | Restore a soft-deleted vault item |
| POST | /vault/sync | Batch sync with client-supplied UUIDs and conflict detection |
| GET | /folders | List folders |
| POST | /folders | Create folder |
| PUT | /folders/{id} | Update folder |
| DELETE | /folders/{id} | Delete folder |
| GET | /tags | List tags |
| POST | /tags | Create tag |
| DELETE | /tags/{id} | Delete tag |

### 5.4 Planned Endpoints (Not Yet Implemented)

| Method | Path | Description |
|---|---|---|
| POST | /vault/export | Export vault (encrypted/unencrypted) |
| POST | /vault/import | Import vault |
| GET | /breach/check | Check passwords against HIBP |
| GET | /audit/log | Get audit log |
| GET | /sessions | List active sessions |
| DELETE | /sessions/{id} | Revoke session |
| POST | /share-links | Create share link |
| GET | /share-links/{token} | View share link (public) |

---

## 6. ViewModel UiState Pattern (Kotlin)

```kotlin
// Example for VaultListViewModel
data class VaultListUiState(
    val isLoading: Boolean = false,
    val items: List<VaultItemUi> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedFolder: String? = null,
    val clipboardCountdown: Int? = null,
)

sealed interface VaultListUiEvent {
    data class ShowSnackbar(val message: String) : VaultListUiEvent
    data object NavigateToLogin : VaultListUiEvent
    data class CopyToClipboard(val value: String, val timeoutSeconds: Int) : VaultListUiEvent
}
```

---

## 7. Zero-Knowledge Crypto Flow

```
Master Password + Email
        │
        ▼
   Argon2id KDF
   ┌─────────────────────────────────┐
   │ memory=64MB, iter=3, par=4     │
   │ salt = SHA256(email.lowercase) │
   └─────────────────────────────────┘
        │
        ├──► 256-bit Master Key (NEVER leaves device)
        │         │
        │         └──► AES-256-GCM → encrypts vault items
        │
        └──► 256-bit Auth Key = HKDF(masterKey, "auth")
                  │
                  └──► Argon2id hash → stored on server
                            (server only sees this hash)
```

---

## 8. Error Handling Strategy

| Layer | Strategy |
|---|---|
| Network (Android) | Sealed Result<T, AppError>, retry with exponential backoff on 5xx, offline queue |
| Crypto errors | Throw CryptoException, surface as vault-locked state |
| Import parser | Per-item error collection; partial import with error report |
| Laravel API | JSON error envelope: {success, message, errors{}}, HTTP status codes per RFC 7807 |
| Web vault (Blade) | Laravel exception handler → user-friendly Blade error pages |

---

## 9. Security Considerations

- Master password never transmitted, never stored — not even in memory beyond unlock flow
- Client-submitted auth key material is stored server-side only as an Argon2id hash and verified on login
- Auth key rotated on password change (re-encrypts entire vault)
- Android: vault key stored in Android Keystore (hardware-backed if available), unlocked by biometric
- All API routes require Sanctum bearer token (no cookies on API routes)
- API auth failures return JSON `401` responses instead of web redirects
- Folder and parent-folder references are validated against the authenticated user before persistence
- CSRF protection on all web vault form submissions
- Content Security Policy headers on all web routes
- Rate limiting: 10 login attempts / 15 min per IP; 3 2FA attempts then lockout
- WebAuthn challenge nonces expire in 5 minutes
- Share links: AES key in URL fragment (#), never sent to server, 24h TTL default
