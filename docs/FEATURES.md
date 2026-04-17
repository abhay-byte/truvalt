# Features List

Status legend: 🔴 Not Started | 🟡 In Progress | 🟢 Complete | ⚫ Cancelled | 🔵 Deferred

---

## Module: AUTH

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-001 | Email + Password Registration | Register with email, master password, hint | Critical | 🔴 | v1.0 |
| F-002 | Email + Password Login | Login flow with auth key derivation | Critical | 🔴 | v1.0 |
| F-003 | TOTP 2FA Setup & Verify | Enroll authenticator app, verify at login | Critical | 🔴 | v1.0 |
| F-004 | Passkey Login (WebAuthn) | FIDO2 passkey registration & login on web + Android | High | 🔴 | v1.1 |
| F-005 | Biometric Unlock (Android) | Fingerprint/face unlock via Android Keystore with startup unlock routing and keystore-backed vault key restore | Critical | 🟢 | v1.0 |
| F-006 | Auto-lock & Session Timeout | Configurable idle timeout, lock on background | Critical | 🔴 | v1.0 |
| F-007 | Emergency Access | Trusted contact request + delay-based approval | Medium | 🔴 | v1.2 |
| F-008 | Master Password Change | Re-derives key, re-encrypts all vault items | High | 🔴 | v1.0 |
| F-009 | Autofill Service | Android autofill for apps and browsers, save new credentials | Critical | 🟢 | v1.0 |

---

## Module: VAULT

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-009 | Login Item (Password) | Store URL, username, password, notes, custom fields | Critical | 🔴 | v1.0 |
| F-010 | Passkey Storage | Store FIDO2 passkey metadata and sync | High | 🔴 | v1.1 |
| F-011 | Passphrase Storage | Store multi-word passphrases with metadata | High | 🔴 | v1.0 |
| F-012 | Secure Note | Encrypted free-text note | High | 🔴 | v1.0 |
| F-013 | TOTP Seed Storage | Store TOTP seed, generate live codes inline | High | 🔴 | v1.0 |
| F-014 | Security / Recovery Code | Store one-time backup codes | High | 🔴 | v1.0 |
| F-015 | Credit Card Storage | Card number, CVV, expiry, billing address | Medium | 🔴 | v1.1 |
| F-016 | Identity Storage | Name, address, passport, SSN fields | Medium | 🔴 | v1.1 |
| F-017 | Custom Item Type | User-defined fields (text, hidden, URL, date) | Medium | 🔴 | v1.2 |
| F-018 | Folder Organization | Nested folders, drag-to-organize | High | 🔴 | v1.0 |
| F-019 | Tag System | Multi-tag items, filter by tag | Medium | 🔴 | v1.0 |
| F-020 | Favorites | Mark/unmark items as favorites | Medium | 🔴 | v1.0 |
| F-021 | Full-Text Search | Search by name, username, URL, notes | Critical | 🔴 | v1.0 |
| F-022 | Secure Clipboard | Copy field with auto-clear timer (configurable) | Critical | 🔴 | v1.0 |
| F-023 | Soft Delete / Trash | Move to trash, restore, permanent delete | High | 🔴 | v1.0 |

---

## Module: GENERATOR

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-024 | Password Generator | Length, charset, exclude ambiguous, exclude chars | Critical | 🟢 | v1.0 |
| F-025 | Passphrase Generator | EFF wordlist, word count, separator, capitalize, append digit | High | 🟢 | v1.0 |
| F-026 | Password Strength Meter | zxcvbn-based score, shown on generation and item edit | High | 🟢 | v1.0 |
| F-027 | Generator History | Last 20 generated passwords/phrases (session-only, not synced) | Low | 🔴 | v1.1 |

---

## Module: SYNC

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-028 | Cloud Sync | Encrypted delta sync with Laravel backend | Critical | 🔴 | v1.0 |
| F-029 | Local-Only Mode | No server connection, Room-only vault | High | 🔴 | v1.0 |
| F-030 | Conflict Resolution | Last-write-wins with per-field updated_at | High | 🔴 | v1.0 |
| F-031 | Multi-Device Sync | Sync across Android + web vault | High | 🔴 | v1.0 |

---

## Module: IMPORT / EXPORT

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-032 | Import — Bitwarden JSON | Parse and import Bitwarden encrypted/unencrypted export | High | 🔴 | v1.0 |
| F-033 | Import — 1Password 1PUX | Parse 1Password export format | Medium | 🔴 | v1.1 |
| F-034 | Import — LastPass CSV | Parse LastPass CSV export | High | 🔴 | v1.0 |
| F-035 | Import — KeePass XML | Parse KeePass 2.x KDBX-exported XML | Medium | 🔴 | v1.1 |
| F-036 | Import — Chrome/Firefox CSV | Parse browser password export | High | 🔴 | v1.0 |
| F-037 | Export — Encrypted `.truvalt` | AES-256-GCM encrypted JSON blob | Critical | 🔴 | v1.0 |
| F-038 | Export — Unencrypted JSON | Plain JSON (warned as sensitive) | High | 🔴 | v1.0 |
| F-039 | Export — Unencrypted CSV | CSV (warned as sensitive) | Medium | 🔴 | v1.0 |

---

## Module: BREACH & HEALTH

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-040 | HIBP Breach Check | k-Anonymity SHA-1 prefix check against HIBP API | High | 🔴 | v1.0 |
| F-041 | Vault Health Dashboard | Weak, reused, old (>180d), breached passwords report | High | 🔴 | v1.0 |

---

## Module: SHARING

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-042 | Encrypted Share Link | Generate time-limited share link; AES key in URL fragment | Medium | 🔴 | v1.1 |
| F-043 | View-Count Limit | Set max views on a share link | Low | 🔴 | v1.1 |

---

## Module: AUDIT & SESSIONS

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-044 | Audit Log | Log all login, access, change, export, share events | High | 🔴 | v1.0 |
| F-045 | Active Session List | View all active sessions with device/IP/last-seen | High | 🔴 | v1.0 |
| F-046 | Revoke Session | Remotely invalidate any active session token | High | 🔴 | v1.0 |

---

## Module: UI / SETTINGS

| ID | Feature | Description | Priority | Status | Version |
|---|---|---|---|---|---|
| F-047 | Dark / Light / AMOLED Theme | Full system theme support + AMOLED true-black | High | 🔴 | v1.0 |
| F-048 | Material You Dynamic Color | Android 12+ Monet dynamic color | Medium | 🔴 | v1.0 |
| F-049 | Clipboard Timeout Setting | User-configurable clipboard clear delay (15s–5min or never) | High | 🔴 | v1.0 |
| F-050 | Auto-Lock Setting | Configure idle timeout (immediate, 1min, 5min, 15min, 1h, never) | High | 🔴 | v1.0 |
| F-051 | Server URL Configuration | User sets their own self-hosted backend URL | Critical | 🔴 | v1.0 |

---

## Version Roadmap

| Version | Features Included |
|---|---|
| v1.0 | F-001–003, F-005–006, F-008–009, F-011–014, F-018–027, F-028–032, F-034, F-036–041, F-044–051 |
| v1.1 | F-004, F-010, F-015–016, F-027, F-033, F-035, F-042–043 |
| v1.2 | F-007, F-017 |

---

## Summary

Total features: 51 | 🔴 Not Started: 51 | 🟡 In Progress: 0 | 🟢 Complete: 0 | ⚫ Cancelled: 0 | 🔵 Deferred: 0
