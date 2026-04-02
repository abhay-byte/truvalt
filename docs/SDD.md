# Software Design Document (SDD)

## 1. System Architecture

### 1.1 Runtime Topology

```
Android App
    │
    ├──Firebase Android SDK──▶ Firebase Authentication
    │                         (sign in, sign up, Google OAuth, delete user)
    │
    └──Firebase Android SDK──▶ Cloud Firestore
                               (vault items, folders, tags, user profile)
```

There is **no intermediate backend server**. The Android app communicates directly with Firebase services using the Firebase Android SDK.

### 1.2 Design Intent

- Keep vault encryption fully client-side — the server never sees plaintext data.
- Use Firebase Authentication for account identity and session management.
- Use Cloud Firestore directly from the Android SDK for all persistence.
- Keep the architecture simple and operationally zero-cost (no server to maintain).

---

## 2. Tech Stack

| Layer | Android |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM |
| Networking | Firebase Android SDK (no Retrofit for cloud ops) |
| Local DB | Room (offline/local-only mode) |
| Cloud Persistence | Cloud Firestore (direct SDK) |
| Identity | Firebase Authentication (direct SDK) |
| Crypto | Android Keystore + BouncyCastle Argon2id + AES-256-GCM |
| Testing | JUnit / Android tests |

---

## 3. Android Package Structure

```
android/app/src/main/java/com/ivarna/truvalt/
├── core/
│   ├── biometric/         ← BiometricHelper
│   ├── crypto/            ← CryptoManager (Argon2id + AES-GCM + Keystore)
│   └── pin/               ← PinStorage
├── data/
│   ├── preferences/       ← TruvaltPreferences (DataStore)
│   ├── remote/            ← FirestoreVaultRepository (direct Firestore SDK)
│   └── repository/        ← AuthRepositoryImpl, SyncRepositoryImpl, VaultRepositoryImpl
├── domain/
│   └── repository/        ← Repository interfaces
└── presentation/
    └── ui/                ← Compose screens (vault, settings, auth, health, generator)
```

---

## 4. Storage Design

### 4.1 Android Local Storage (Room)

Used in local-only mode and as a cache for cloud-synced data.

### 4.2 Firestore Layout

The Android app reads and writes Firestore directly using the Firebase SDK:

| Path | Purpose |
|---|---|
| `users/{uid}` | Truvalt user profile metadata |
| `users/{uid}/vault_items/{itemId}` | Encrypted vault items |
| `users/{uid}/folders/{folderId}` | Folder metadata |
| `users/{uid}/tags/{tagId}` | Tag metadata |

### 4.3 User Profile Document

| Field | Type | Notes |
|---|---|---|
| `id` | string | Firebase UID |
| `email` | string | Firebase account email |
| `providers` | string[] | `password`, `google.com`, etc. |
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
| `encrypted_data` | string | Base64-encoded AES-256-GCM encrypted payload |
| `favorite` | bool | Favorite flag |
| `created_at` | int | Unix timestamp |
| `updated_at` | int | Unix timestamp |
| `deleted_at` | int/null | Soft-delete marker |

---

## 5. Auth Flow

### 5.1 Email + Password

1. `AuthRepositoryImpl.createVault()` calls `FirebaseAuth.createUserWithEmailAndPassword()` directly.
2. On success, the Firebase UID and ID token are stored locally in `TruvaltPreferences`.
3. `FirestoreVaultRepository.upsertUserProfile()` writes the user profile to Firestore.
4. Vault encryption keys are derived client-side from the master password using Argon2id.

### 5.2 Google Sign-In

1. Android Credential Manager provides a Google ID token.
2. `AuthRepositoryImpl.signInWithGoogle()` calls `FirebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(...))`.
3. Firebase session is stored locally; Firestore profile is upserted.
4. Vault key is derived deterministically from the Firebase UID (no master password in Google flow).

### 5.3 Account Deletion

1. `FirestoreVaultRepository.deleteAllUserData(uid)` hard-deletes all Firestore subcollections and the user profile document.
2. `FirebaseAuth.currentUser.delete()` deletes the Firebase Auth account.
3. All local preferences, vault key material, and Room data are cleared.

---

## 6. Sync / Conflict Logic

- Vault items carry integer `updated_at` timestamps.
- `FirestoreVaultRepository.syncVaultItems()` uses last-write-wins: incoming items with an older `updated_at` than the Firestore copy yield a conflict rather than an overwrite.
- Android pushes folders, tags, and pending vault items before pulling active and trashed items back into Room.
- Folder/tag deletion tombstones are not yet propagated in the current sync pass.

---

## 7. Security Model

- **Zero-knowledge vault**: All encryption/decryption is done on-device. Firestore stores only base64-encoded encrypted blobs. Firebase never sees plaintext vault data.
- **Account identity**: Firebase Authentication owns account credentials. The app never handles raw passwords server-side.
- **Key derivation**: Argon2id derives master key and vault key from the user's master password + email salt for email/password accounts. For Google accounts, keys are derived from the Firebase UID.
- **Local key protection**: The vault key is wrapped with the Android Keystore and requires biometric/PIN unlock.
- **Account deletion**: Firestore data is removed first (while the UID is still valid), then the Firebase Auth user is deleted. Combined with local wipe, this is a complete erasure.

---

## 8. Operational Notes

- No backend server is required. All cloud operations go directly to Firebase.
- Firebase project: `tru-valt`
- No environment variables, no server deployments, no Docker.
- `delete-account-site/` contains a static Firebase Hosting page for Google Play compliance.
