# truvalt Documentation

> **AGENT STRICT RULE: Whenever any feature is added, modified, or removed; any screen changes; the Firestore schema changes; any task status changes — you MUST update every affected document immediately. Do not leave any document stale.**

---

## Master Index

| File | Description | Last Updated |
|---|---|---|
| [README.md](./README.md) | Master index and change management guide | 2026-04-07 |
| [PROBLEM_STATEMENT.md](./PROBLEM_STATEMENT.md) | Background, problem definition, goals, constraints | 2026-03-16 |
| [SRS.md](./SRS.md) | Software Requirements Specification | 2026-03-23 |
| [SDD.md](./SDD.md) | Software Design Document — architecture, Firestore schema, auth flows | 2026-04-02 |
| [FEATURES.md](./FEATURES.md) | Complete feature list with status tracking | 2026-03-30 |
| [UI_UX_DOCUMENTATION.md](./UI_UX_DOCUMENTATION.md) | Screen-by-screen UI/UX documentation | 2026-03-30 |
| [UI_DESIGN_SYSTEM.md](./UI_DESIGN_SYSTEM.md) | Design system, colors, typography, components | 2026-03-16 |
| [DIAGRAMS.md](./DIAGRAMS.md) | All Mermaid diagrams | 2026-03-23 |
| [DEPLOYMENT.md](./DEPLOYMENT.md) | Build and Firebase Hosting deploy guide | 2026-04-02 |
| [progress/TODO.md](./progress/TODO.md) | Task list with priorities | 2026-04-06 |
| [progress/ONGOING.md](./progress/ONGOING.md) | Currently active tasks | 2026-04-07 |
| [progress/FINISHED.md](./progress/FINISHED.md) | Completed tasks | 2026-04-07 |

> **Note:** `API.md` and `API_TEST_RESULTS.md` are archived. There is no REST backend — the app talks directly to Firebase.

---

## Change Type → Documents Mapping

| Change Type | Documents to Update |
|---|---|
| New vault item type | FEATURES.md, SRS.md, SDD.md, UI_UX_DOCUMENTATION.md, DIAGRAMS.md, TODO.md |
| Firestore schema change | SDD.md, DIAGRAMS.md |
| New screen | UI_UX_DOCUMENTATION.md, UI_DESIGN_SYSTEM.md, DIAGRAMS.md |
| Feature completed | FEATURES.md, TODO.md → FINISHED.md |
| Security model change | SRS.md, SDD.md, DIAGRAMS.md |
| Deploy change | DEPLOYMENT.md |

---

## Quick Links

- **Android App:** `/android/` — Kotlin + Jetpack Compose
- **Delete Account Site:** `/delete-account-site/` — Static Firebase Hosting page
- **Release Automation:** `/fastlane/` — Android deployment

---

## Architecture Overview

```
truvalt/
├── android/               ← Kotlin + Compose Android app
├── delete-account-site/   ← Static site (Firebase Hosting, Google Play compliance)
├── docs/                  ← Shared documentation
└── fastlane/              ← Release automation
```

**No backend server.** The Android app communicates directly with Firebase Authentication and Cloud Firestore via the Firebase Android SDK.

---

## Version Information

- **Current Version:** 1.0.0
- **Target SDK:** API 36 (Android 14)
- **Min SDK:** API 26 (Android 8.0)
- **Cloud Identity/Storage:** Firebase Authentication + Cloud Firestore (direct SDK)
- **Local Storage:** Room (Android local/offline mode)
- **Encryption:** AES-256-GCM + Argon2id (all client-side)
