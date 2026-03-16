# Problem Statement

## Background and Context

The average person has over 100 online accounts, from social media and email to banking and shopping. Managing credentials for all these accounts has become a significant challenge. Popular cloud password managers like LastPass and 1Password have suffered high-profile security breaches, and their subscription-based pricing creates an ongoing cost burden. While open-source alternatives exist, they often lack polished cross-platform native clients or require complex self-hosting setup that deters average users.

truvalt addresses this gap by providing a secure, self-hostable, or fully offline password manager that works natively on Android and via any web browser. It offers zero-knowledge end-to-end encryption, no mandatory subscription, and a first-class UI on both platforms.

---

## The Specific Problem Being Solved

Users need a secure, self-hostable, or fully offline password manager that works natively on Android and via any web browser — with zero-knowledge end-to-end encryption, no mandatory subscription, and a first-class UI on both platforms.

---

## Who Is Affected

| User Type | Description | Pain Point |
|---|---|---|
| Privacy-conscious individual | Doesn't want credentials stored on third-party US servers | No control over where data lives |
| Developer / sysadmin | Manages many API keys, SSH passphrases, server passwords | Needs developer-friendly import/export and CLI potential |
| Small team | Shared vault for service credentials | Expensive per-seat pricing on commercial tools |
| Self-hoster | Runs own infrastructure | Bitwarden is complex to self-host; other options lack polish |
| Android power user | Primarily lives on Android | Poor native Android UX in many open-source options |

---

## Goals

- [x] Build a cross-platform password manager (Android + Web)
- [x] Implement zero-knowledge end-to-end encryption (AES-256-GCM + Argon2id)
- [x] Support both cloud sync (self-hosted) and local-only offline mode
- [x] Provide first-class native Android experience with Jetpack Compose
- [x] Offer a browser-accessible web vault with Laravel 12
- [x] Include all major password manager features (vault items, generator, TOTP, import/export, breach check)
- [x] Enable biometric unlock on Android
- [x] Support passkey login via WebAuthn/FIDO2
- [x] Provide import from major password managers (Bitwarden, LastPass, 1Password, KeePass)
- [x] Support export in encrypted .truvalt format
- [x] Build vault health dashboard with breach detection (Have I Been Pwned API)
- [x] Implement audit logging and session management
- [x] Offer emergency access functionality

---

## Out of Scope

- iOS native app (future consideration)
- Desktop applications (web app covers this use case)
- Built-in cloud hosting (self-hosted only)
- Team/organization management beyond basic sharing
- Password changers/auto-fill browser extensions
- Built-in VPN or identity theft monitoring

---

## Success Metrics

| Metric | Target |
|---|---|
| Vault unlock time | < 500ms on mid-range Android |
| App startup time | < 2 seconds cold start |
| Sync completion | < 5 seconds for 1000 items |
| Unit test coverage | ≥ 80% for crypto, generator, sync modules |
| Security compliance | OWASP Top 10, zero-knowledge architecture |
| Platform support | Android 8.0+ (API 26+), modern browsers |

---

## Constraints

- **Master Password Constraint:** Master password is NEVER sent to server; only a derived authentication key is used
- **Zero-Knowledge Architecture:** Server stores only encrypted blobs, auth key hash, and metadata
- **Network Security:** HTTPS only (TLS 1.2+)
- **Encryption:** AES-256-GCM for vault items, Argon2id for key derivation
- **Platform:** Android (Kotlin + Compose) and Web (Laravel 12 + Blade)
- **Database:** PostgreSQL (server) + Room (Android local vault)
- **Auth Methods:** Email + Password, TOTP 2FA, Passkey (WebAuthn), Biometric (Android)
- **Distribution:** Google Play Store + F-Droid
- **Localization:** en-US (initial), with i18n scaffolding for hi-IN, de-DE
