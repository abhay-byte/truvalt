# Software Requirements Specification (SRS)

## 1. Introduction

### 1.1 Purpose

This document defines the complete software requirements for truvalt, a cross-platform password manager with Android and web applications.

### 1.2 Scope

truvalt is a password manager supporting:
- Native Android app (Kotlin + Jetpack Compose)
- Web application (Laravel 12 + Blade)
- Self-hosted backend with PostgreSQL
- Local-only offline mode
- End-to-end encryption with zero-knowledge architecture

### 1.3 Definitions, Acronyms, and Abbreviations

| Term | Definition |
|---|---|
| KDF | Key Derivation Function |
| Argon2id | Memory-hard password hashing algorithm |
| AES-256-GCM | Advanced Encryption Standard with Galois/Counter Mode |
| TOTP | Time-based One-Time Password (RFC 6238) |
| WebAuthn | Web Authentication API (FIDO2) |
| HIBP | Have I Been Pwned |
| Room | Android local database |
| Sanctum | Laravel API authentication |

---

## 2. Functional Requirements

### 2.1 Authentication Module (AUTH)

| ID | Requirement | Priority |
|---|---|---|
| FR-AUTH-01 | User registers with email + master password. Master password is NEVER sent to server; only a derived authentication key is used. | Critical |
| FR-AUTH-02 | Login with email + auth key. | Critical |
| FR-AUTH-03 | TOTP 2FA via authenticator app (RFC 6238). | Critical |
| FR-AUTH-04 | Passkey login (WebAuthn FIDO2) on web and Android (Credential Manager API). | High |
| FR-AUTH-05 | Biometric unlock (Android — unlocks local vault key from Android Keystore). | Critical |
| FR-AUTH-06 | Session timeout + auto-lock (configurable). | Critical |
| FR-AUTH-07 | Emergency access (trusted contact can request access after configurable delay). | Medium |

### 2.2 Vault Module (VAULT)

| ID | Requirement | Priority |
|---|---|---|
| FR-VAULT-01 | Create, read, update, delete (CRUD) vault items. | Critical |
| FR-VAULT-02 | Vault item types: Login (URL, username, password, TOTP seed), Passkey, Passphrase, Secure Note, Security/Recovery Code, Credit Card, Identity, Custom. | Critical |
| FR-VAULT-03 | Organize items into folders and apply multiple tags. | High |
| FR-VAULT-04 | Mark items as Favorite. | Medium |
| FR-VAULT-05 | Secure clipboard — copy field to clipboard, auto-clear after configurable timeout (default 30s). | Critical |
| FR-VAULT-06 | Inline TOTP code generation from stored seeds (shows live countdown). | High |

### 2.3 Generator Module (GEN)

| ID | Requirement | Priority |
|---|---|---|
| FR-GEN-01 | Generate strong random passwords (configurable length, charset: uppercase, lowercase, digits, symbols, exclude ambiguous). | Critical |
| FR-GEN-02 | Generate passphrases (configurable word count, separator, capitalize, append number — uses EFF large wordlist). | High |
| FR-GEN-03 | Password strength meter (zxcvbn algorithm). | High |

### 2.4 Sync Module (SYNC)

| ID | Requirement | Priority |
|---|---|---|
| FR-SYNC-01 | Full vault encrypted sync to self-hosted Laravel backend. | Critical |
| FR-SYNC-02 | Local-only mode — no network calls, all data stored in Room DB only. | High |
| FR-SYNC-03 | Conflict resolution strategy: last-write-wins with per-field timestamps. | High |

### 2.5 Crypto Module (CRYPTO)

| ID | Requirement | Priority |
|---|---|---|
| FR-CRYPTO-01 | Vault encryption: AES-256-GCM. Every item encrypted individually client-side. | Critical |
| FR-CRYPTO-02 | Key derivation: Argon2id (memory: 64MB, iterations: 3, parallelism: 4) from master password + email salt. | Critical |
| FR-CRYPTO-03 | The server never receives the master password or the vault encryption key. Zero-knowledge architecture. | Critical |
| FR-CRYPTO-04 | Encrypted export blob uses the same AES-256-GCM key with a separate export IV. | High |

### 2.6 Import/Export Module (IMPORT)

| ID | Requirement | Priority |
|---|---|---|
| FR-IMPORT-01 | Import from: Bitwarden JSON, 1Password 1PUX, LastPass CSV, KeePass XML, Chrome CSV, Firefox CSV, generic CSV. | High |
| FR-IMPORT-02 | Export to: truvalt encrypted `.truvalt` (AES-256-GCM JSON), unencrypted JSON, unencrypted CSV. | Critical |

### 2.7 Breach Module (BREACH)

| ID | Requirement | Priority |
|---|---|---|
| FR-BREACH-01 | Check passwords against HaveIBeenPwned k-Anonymity API (sends only first 5 chars of SHA-1 hash — privacy-preserving). | High |
| FR-BREACH-02 | Vault health dashboard: weak passwords, reused passwords, old passwords (>180 days), breached passwords. | High |

### 2.8 Share Module (SHARE)

| ID | Requirement | Priority |
|---|---|---|
| FR-SHARE-01 | Generate an encrypted time-limited share link for a single vault item (AES key in URL fragment — never sent to server). | Medium |

### 2.9 Audit Module (AUDIT)

| ID | Requirement | Priority |
|---|---|---|
| FR-AUDIT-01 | Full audit log: every login, item access, item change, export, share event logged server-side. | High |
| FR-AUDIT-02 | Active session management — list and revoke sessions (Android + web). | High |

### 2.10 Settings Module (SETTINGS)

| ID | Requirement | Priority |
|---|---|---|
| FR-SETTINGS-01 | User preferences: theme (dark/light/AMOLED), clipboard timeout, auto-lock timeout | High |

---

## 3. Non-Functional Requirements

### 3.1 Performance

| Requirement | Target |
|---|---|
| Vault unlock time | < 500ms on mid-range Android (Argon2id pre-computed at login, key cached in-memory) |
| Cold start time | < 2 seconds |
| Sync time | < 5 seconds for 1000 items |

### 3.2 Security

| Requirement | Description |
|---|---|
| Zero-knowledge | Server stores only encrypted blobs, auth key hash, and metadata |
| HTTPS only | TLS 1.2+ |
| OWASP compliance | Web vault follows OWASP Top 10 |
| Android Keystore | Used for biometric-protected key storage |
| Key derivation | Argon2id with specified parameters |

### 3.3 Reliability

| Requirement | Target |
|---|---|
| Unit test coverage | ≥ 80% for crypto, generator, and sync modules |
| Offline support | Full functionality in local-only mode |

---

## 4. User Interface Requirements

### 4.1 Android

- Material Design 3 (Material You)
- Min SDK: API 26 (Android 8.0)
- Target SDK: API 36
- Dynamic color support on Android 12+

### 4.2 Web

- Laravel 12 Blade templates
- Tailwind CSS
- Alpine.js for interactivity

---

## 5. Data Requirements

### 5.1 Local Storage (Android - Room)

- Encrypted with SQLCipher
- Full sync with server when online

### 5.2 Server Storage (PostgreSQL)

- Encrypted blobs only (zero-knowledge)
- User metadata, auth key hash, audit logs

---

## 6. Acceptance Criteria

All functional requirements must pass their respective test cases. Non-functional requirements (performance, security) must meet the specified targets.
