# Software Requirements Specification (SRS)

## 1. Introduction

### 1.1 Purpose

This document defines the current product requirements for Truvalt across Android, web, and the Laravel backend.

### 1.2 Scope

Truvalt provides:
- Native Android app
- Laravel-based web/API backend
- Local-only mode
- Client-side encrypted vault storage
- Firebase-backed account authentication
- Firestore-backed cloud vault metadata storage

### 1.3 Definitions

| Term | Definition |
|---|---|
| Argon2id | Memory-hard password hashing algorithm |
| AES-256-GCM | Symmetric authenticated encryption used for vault payloads |
| Firebase Auth | Account identity provider for email/password and Google sign-in |
| Firestore | Document database used for backend vault/profile persistence |
| ID Token | Firebase bearer token sent to protected Laravel API routes |

---

## 2. Functional Requirements

### 2.1 Authentication

| ID | Requirement | Priority |
|---|---|---|
| FR-AUTH-01 | Users can register backend accounts with Firebase email/password credentials. | Critical |
| FR-AUTH-02 | Users can sign in to the backend with Firebase email/password credentials. | Critical |
| FR-AUTH-03 | Users can sign in to the backend with Google through Firebase Authentication. | Critical |
| FR-AUTH-04 | Protected Laravel API routes must verify Firebase ID tokens server-side. | Critical |
| FR-AUTH-05 | The backend may store optional client-derived vault-auth material only as an Argon2id hash. | High |
| FR-AUTH-06 | Biometric unlock remains an Android-local capability. | Critical |
| FR-AUTH-07 | Auto-lock and session timeout remain configurable on Android. | Critical |

### 2.2 Vault

| ID | Requirement | Priority |
|---|---|---|
| FR-VAULT-01 | Backend supports CRUD for encrypted vault items. | Critical |
| FR-VAULT-02 | Backend supports folders and tags per authenticated user. | High |
| FR-VAULT-03 | Backend supports favorites and trash/restore state for vault items. | High |
| FR-VAULT-04 | Backend stores encrypted item payloads as opaque base64-encoded blobs. | Critical |

### 2.3 Sync

| ID | Requirement | Priority |
|---|---|---|
| FR-SYNC-01 | Cloud sync uses the Laravel API with Firestore-backed persistence. | Critical |
| FR-SYNC-02 | Client item UUIDs must be preserved during sync. | Critical |
| FR-SYNC-03 | Conflict handling is last-write-wins based on `updated_at`. | High |
| FR-SYNC-04 | Local-only mode must remain available when no backend is configured. | High |

### 2.4 Crypto

| ID | Requirement | Priority |
|---|---|---|
| FR-CRYPTO-01 | Vault items are encrypted client-side using AES-256-GCM. | Critical |
| FR-CRYPTO-02 | Vault key derivation uses Argon2id on the client. | Critical |
| FR-CRYPTO-03 | The Laravel backend must never decrypt vault contents. | Critical |
| FR-CRYPTO-04 | The backend must reject invalid encrypted payload encodings. | High |

---

## 3. Non-Functional Requirements

### 3.1 Security

| Requirement | Description |
|---|---|
| HTTPS only | All backend traffic must use TLS in deployed environments |
| Token verification | Laravel must verify Firebase ID tokens before any protected Firestore access |
| Zero-knowledge vault storage | Server stores encrypted blobs and metadata only |
| Hashing | Optional stored vault-auth material must use Argon2id |
| Authorization isolation | Folder/tag/item access must be scoped to the authenticated Firebase UID |

Clarification:
- The vault encryption key and decrypted vault contents are never sent to the backend.
- Firebase email/password credentials are handled by Firebase Authentication, not by Laravel.

### 3.2 Performance

| Requirement | Target |
|---|---|
| Public health response | Fast enough for uptime probes |
| API validation overhead | Minimal compared with remote auth/storage latency |
| Sync path | Suitable for incremental item-based sync, with pagination still pending |

### 3.3 Reliability

| Requirement | Target |
|---|---|
| Automated backend verification | PHPUnit feature coverage for route/middleware behavior |
| Offline support | Android remains usable in local-only mode |

---

## 4. Interface Requirements

### 4.1 Backend API

- REST API served from `/api`
- Public auth endpoints:
  - `POST /register`
  - `POST /login`
  - `POST /login/google`
- Protected routes require `Authorization: Bearer <firebase_id_token>`

### 4.2 Android

- Android currently remains local-first
- Backend cloud sync integration is in progress

---

## 5. Data Requirements

### 5.1 Local Android Data

- Room database for local/offline vault data

### 5.2 Backend Data

- Firestore user profile documents
- Firestore vault item, folder, and tag documents
- No SQL database is required in the normal backend request path

---

## 6. Acceptance Criteria

The current backend pivot is acceptable when:
- Laravel public routes boot without Firebase-only failures
- Protected routes return JSON `401` without a bearer token
- Firebase-backed auth routes and protected routes pass automated feature tests
- Firestore-backed controllers enforce ownership and payload validation rules
