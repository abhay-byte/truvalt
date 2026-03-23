# Software Design Document (SDD)

## 1. System Architecture

### 1.1 Current Runtime Topology

```
Android App / Web Client
        │
        │ HTTPS / REST
        ▼
┌──────────────────────────────┐
│ Laravel 12 API               │
│ - Route validation           │
│ - Firebase ID token auth     │
│ - Firestore REST persistence │
└──────────────┬───────────────┘
               │
      ┌────────┴────────┐
      │                 │
      ▼                 ▼
Firebase Auth      Cloud Firestore
```

### 1.2 Design Intent

- Keep vault encryption client-side.
- Let Firebase Authentication handle account identity.
- Keep the Laravel API as the policy and validation layer.
- Store backend metadata and encrypted vault blobs in Firestore instead of PostgreSQL.
- Remove SQL from the request path to reduce operational complexity and avoid slow external SQL round-trips.

---

## 2. Tech Stack

| Layer | Android | Web/Backend |
|---|---|---|
| Language | Kotlin | PHP 8.4, Blade, Alpine.js |
| UI | Jetpack Compose + Material 3 | Blade templates + Tailwind CSS |
| Architecture | Clean Architecture + MVVM | Laravel controllers + service/repository pattern |
| Networking | Retrofit + OkHttp | Laravel HTTP Client |
| Local DB | Room | file/session cache only |
| Server Persistence | — | Cloud Firestore |
| Identity | Local vault auth + Firebase-backed cloud session bootstrap | Firebase Authentication |
| Crypto | Android Keystore + BouncyCastle Argon2id | Argon2id for optional stored vault-auth metadata |
| Testing | JUnit / Android tests | PHPUnit feature tests |

Backend notes:
- Firebase Admin SDK for PHP verifies Firebase ID tokens.
- Google Auth service-account credentials mint OAuth access tokens for Firestore REST calls.
- Firestore is used through REST instead of the PHP gRPC client because the runtime does not provide the `grpc` extension.

---

## 3. Backend Package / Folder Structure

```
web/
├── app/
│   ├── Http/
│   │   ├── Controllers/Api/
│   │   │   ├── AuthController.php
│   │   │   ├── VaultController.php
│   │   │   ├── FolderController.php
│   │   │   └── TagController.php
│   │   └── Middleware/
│   │       └── AuthenticateWithFirebase.php
│   ├── Providers/
│   │   └── AppServiceProvider.php
│   └── Services/Firebase/
│       ├── FirebaseAdmin.php
│       ├── FirebaseAuthService.php
│       ├── FirebaseProjectConfig.php
│       ├── FirebaseRequestException.php
│       ├── FirestoreRestClient.php
│       ├── IdentityToolkitClient.php
│       └── TruvaltFirestoreRepository.php
├── routes/
│   └── api.php
└── tests/
    └── Feature/Api/
```

---

## 4. Storage Design

### 4.1 Android Local Storage

Android remains Room-backed for local/offline data.

### 4.2 Firestore Backend Storage

Firestore document layout:

| Path | Purpose |
|---|---|
| `users/{uid}` | Truvalt user profile metadata |
| `users/{uid}/vault_items/{itemId}` | Encrypted vault items |
| `users/{uid}/folders/{folderId}` | Folder metadata |
| `users/{uid}/tags/{tagId}` | Tag metadata |

### 4.3 User Profile Document

Example fields:

| Field | Type | Notes |
|---|---|---|
| `id` | string | Firebase UID |
| `email` | string | Firebase account email |
| `providers` | string[] | `password`, `google.com`, etc. |
| `auth_key_hash` | string | Optional Argon2id hash of client-provided vault-auth material |
| `email_verified` | bool | Mirrored from Firebase Auth |
| `created_at` | int | Unix timestamp |
| `updated_at` | int | Unix timestamp |
| `last_login_at` | int | Unix timestamp |

### 4.4 Vault Item Document

| Field | Type | Notes |
|---|---|---|
| `id` | string | Client UUID preserved |
| `user_id` | string | Firebase UID |
| `type` | string | `login`, `note`, etc. |
| `name` | string | Display label |
| `folder_id` | string/null | Firestore folder document id |
| `encrypted_data` | string | Base64-encoded encrypted payload |
| `favorite` | bool | Favorite flag |
| `created_at` | int | Unix timestamp |
| `updated_at` | int | Unix timestamp |
| `deleted_at` | int/null | Soft-delete marker |

---

## 5. API Design

### 5.1 Public Routes

| Method | Path | Purpose |
|---|---|---|
| GET | `/health` | Readiness probe |
| GET | `/keep-alive` | External ping route |
| POST | `/register` | Firebase email/password sign-up + Truvalt profile bootstrap |
| POST | `/login` | Firebase email/password sign-in |
| POST | `/login/google` | Firebase Google sign-in |

### 5.2 Protected Routes

All protected routes require `Authorization: Bearer <firebase_id_token>`.

| Method | Path | Purpose |
|---|---|---|
| GET | `/me` | Current Firestore-backed user profile |
| POST | `/logout` | Firebase refresh-token revocation |
| GET | `/vault/items` | List non-deleted items |
| POST | `/vault/items` | Create item |
| GET | `/vault/items/{id}` | Read item |
| PUT | `/vault/items/{id}` | Update item |
| DELETE | `/vault/items/{id}` | Soft-delete item |
| GET | `/vault/trash` | List deleted items |
| POST | `/vault/items/{id}/restore` | Restore item |
| POST | `/vault/sync` | Batch sync + conflict detection |
| GET | `/folders` | List folders |
| POST | `/folders` | Create folder |
| PUT | `/folders/{id}` | Update folder |
| DELETE | `/folders/{id}` | Delete folder |
| GET | `/tags` | List tags |
| POST | `/tags` | Create tag |
| DELETE | `/tags/{id}` | Delete tag |

---

## 6. Auth Flow

### 6.1 Email + Password

1. Client sends `/register` or `/login` to Laravel.
2. Laravel calls Firebase Auth REST endpoints through `IdentityToolkitClient`.
3. Firebase returns `idToken`, `refreshToken`, `localId`.
4. Laravel uses the Firebase Admin SDK to read the Firebase user record.
5. Laravel upserts the Truvalt Firestore profile document.
6. Client stores the returned Firebase ID token and sends it as the bearer token on protected routes.

### 6.2 Google Sign-In

1. Client obtains a Google ID token.
2. Client posts that token to `/login/google`.
3. Laravel exchanges it against Firebase Auth via `accounts:signInWithIdp`.
4. Laravel upserts the Firestore profile and returns Firebase tokens.

### 6.3 Android Cloud-Mode Session Handling

1. Android still derives and stores vault encryption material locally.
2. In cloud mode, `AuthRepositoryImpl` also calls `/register` or `/login`.
3. The app stores the returned Firebase `token`, optional `refresh_token`, and backend `user.id` in `TruvaltPreferences`.
4. `SyncRepositoryImpl` reuses that bearer token for folders, tags, vault items, trash reads, and `/vault/sync`.
5. Folder/tag create requests may include client IDs so Room and Firestore stay aligned after the first sync.

### 6.4 Protected Route Verification

1. `AuthenticateWithFirebase` reads the bearer token.
2. `FirebaseAuthService` verifies the Firebase ID token with the Admin SDK.
3. Revocation checking is enabled by default.
4. The request user is resolved as the authenticated Firebase UID/email pair.

---

## 7. Sync / Conflict Logic

- Vault items are stored with integer `updated_at` timestamps.
- `POST /vault/sync` preserves client UUIDs.
- If the stored item has a newer `updated_at` than the incoming item, the stored copy is returned in `conflicts`.
- Otherwise the incoming item overwrites the stored copy.
- Folder ownership is validated before create/update/sync writes.
- Android currently pushes folders, tags, and pending vault items before pulling active and trashed vault items back into Room.
- Folder/tag deletion tombstones are not implemented yet, so those deletes are not propagated in the current sync pass.

---

## 8. Security Model

- Vault encryption remains client-side; Laravel stores encrypted payloads only.
- Firebase Authentication handles account identity and session issuance.
- Laravel verifies Firebase ID tokens server-side before Firestore access.
- Optional `auth_key_hash` input is stored only as an Argon2id hash.
- Cross-user folder references are rejected.
- Invalid base64 vault blobs are rejected before persistence.
- API auth failures return JSON `401` instead of web redirects.

Security tradeoff:
- The backend no longer uses Laravel Sanctum or a SQL user table for API auth.
- Email/password credentials are handled by Firebase Authentication, not by Laravel.
- Zero-knowledge still applies to vault contents and vault encryption keys, not to the Firebase account password itself.

---

## 9. Operational Notes

- Public endpoints can boot without Firebase credentials because Firebase services are resolved lazily.
- Full authenticated operation requires:
  - `FIREBASE_PROJECT_ID`
  - `FIREBASE_CREDENTIALS` or `FIREBASE_CREDENTIALS_JSON`
  - `FIREBASE_WEB_API_KEY`
- Session/cache/queue settings should stay file/sync oriented; SQL is no longer part of the normal request path.
- Render Docker deployments use `web/render-build.sh` during image build and `web/render-run.sh` as the container start command.
