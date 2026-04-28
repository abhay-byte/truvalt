# Software Requirements Specification (SRS)

## 1. Introduction

### 1.1 Purpose

This document defines the current product requirements for the Truvalt Android app.

### 1.2 Scope

Truvalt provides:
- Native Android app
- Local-only mode
- Client-side encrypted vault storage
- Firebase-backed account authentication
- Firestore-backed cloud vault metadata storage

> **There is no intermediate backend server.** The Android app communicates directly with Firebase Authentication and Cloud Firestore via the Firebase Android SDK.

### 1.3 Definitions

| Term | Definition |
|---|---|
| Argon2id | Memory-hard password hashing algorithm |
| AES-256-GCM | Symmetric authenticated encryption used for vault payloads |
| Firebase Auth | Account identity provider for email/password and Google sign-in |
| Firestore | Document database used for cloud vault/profile persistence |
| ID Token | Firebase bearer token used by the Android client |

---

## 2. Functional Requirements

### 2.1 Authentication

| ID | Requirement | Priority |
|---|---|---|
| FR-AUTH-01 | Users can register accounts with Firebase email/password credentials directly from the Android app. | Critical |
| FR-AUTH-02 | Users can sign in with Firebase email/password credentials directly from the Android app. | Critical |
| FR-AUTH-03 | Users can sign in with Google through Firebase Authentication directly from the Android app. | Critical |
| FR-AUTH-04 | Firebase ID tokens are managed client-side by the Firebase Android SDK. | Critical |
| FR-AUTH-05 | Client-derived vault-auth material may be stored locally as an Argon2id hash. | High |
| FR-AUTH-06 | Biometric unlock remains an Android-local capability. | Critical |
| FR-AUTH-07 | Auto-lock and session timeout remain configurable on Android. | Critical |

### 2.2 Vault

| ID | Requirement | Priority |
|---|---|---|
| FR-VAULT-01 | The app supports CRUD for encrypted vault items in Room (local) and Firestore (cloud). | Critical |
| FR-VAULT-02 | The app supports folders and tags per authenticated user. | High |
| FR-VAULT-03 | The app supports favorites and trash/restore state for vault items. | High |
| FR-VAULT-04 | Cloud store keeps encrypted item payloads as opaque base64-encoded blobs. | Critical |

### 2.3 Sync

| ID | Requirement | Priority |
|---|---|---|
| FR-SYNC-01 | Cloud sync uses the Firebase Android SDK to read/write Firestore directly. | Critical |
| FR-SYNC-02 | Client item UUIDs must be preserved during sync. | Critical |
| FR-SYNC-03 | Conflict handling is last-write-wins based on `updated_at`. | High |
| FR-SYNC-04 | Local-only mode must remain available when no cloud account is signed in. | High |

### 2.4 Crypto

| ID | Requirement | Priority |
|---|---|---|
| FR-CRYPTO-01 | Vault items are encrypted client-side using AES-256-GCM. | Critical |
| FR-CRYPTO-02 | Vault key derivation uses Argon2id on the client. | Critical |
| FR-CRYPTO-03 | Firestore never receives decrypted vault contents. | Critical |
| FR-CRYPTO-04 | The app rejects invalid encrypted payload encodings before storage. | High |

---

## 3. Non-Functional Requirements

### 3.1 Security

| Requirement | Description |
|---|---|
| HTTPS only | All Firebase traffic uses TLS |
| Token verification | Firebase Auth SDK handles token refresh and verification automatically |
| Zero-knowledge vault storage | Firestore stores encrypted blobs and metadata only |
| Hashing | Local auth key hash uses Argon2id |
| Authorization isolation | Firestore security rules scope access to the authenticated Firebase UID |

Clarification:
- The vault encryption key and decrypted vault contents are never sent to any server.
- Firebase email/password credentials are handled by Firebase Authentication.

### 3.2 Performance

| Requirement | Target |
|---|---|
| Vault unlock | < 500 ms on mid-range Android |
| Sync path | Suitable for incremental item-based sync |

### 3.3 Reliability

| Requirement | Target |
|---|---|
| Offline support | Android remains fully usable in local-only mode |
| Data persistence | Room database survives app restarts and supports encrypted backups |

---

## 4. Interface Requirements

### 4.1 Android

- The Android app uses the Firebase Android SDK for all cloud operations
- No REST API client is required for normal operation

---

## 5. Data Requirements

### 5.1 Local Android Data

- Room database for local/offline vault data

### 5.2 Cloud Data

- Firestore user profile documents
- Firestore vault item, folder, and tag documents
- No SQL database or backend server is involved

---

## 6. Acceptance Criteria

- Firebase Authentication routes (register, login, Google sign-in) work from the Android app
- Firestore security rules enforce per-user data isolation
- Unauthenticated Firestore access is denied
- Local-only mode functions without any network connection
