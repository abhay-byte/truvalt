# truvalt Documentation

> **AGENT STRICT RULE: Whenever any feature is added, modified, or removed; any screen changes; the database schema changes; the API changes; any task status changes — you MUST update every affected document immediately. Do not leave any document stale. After every change, re-read this README and confirm all docs reflect the current state.**

---

## Master Index

| File | Description | Last Updated |
|---|---|---|
| [README.md](./README.md) | Master index and change management guide | 2026-03-23 |
| [PROBLEM_STATEMENT.md](./PROBLEM_STATEMENT.md) | Background, problem definition, goals, constraints | 2026-03-16 |
| [SRS.md](./SRS.md) | Software Requirements Specification with all functional requirements | 2026-03-23 |
| [SDD.md](./SDD.md) | Software Design Document with architecture, database, API design | 2026-03-23 |
| [FEATURES.md](./FEATURES.md) | Complete feature list with status tracking | 2026-03-16 |
| [UI_UX_DOCUMENTATION.md](./UI_UX_DOCUMENTATION.md) | Screen-by-screen UI/UX documentation | 2026-03-23 |
| [UI_DESIGN_SYSTEM.md](./UI_DESIGN_SYSTEM.md) | Design system, colors, typography, components | 2026-03-16 |
| [DIAGRAMS.md](./DIAGRAMS.md) | All 13 Mermaid diagrams | 2026-03-23 |
| [API.md](./API.md) | REST API documentation with all endpoints | 2026-03-23 |
| [API_TEST_RESULTS.md](./API_TEST_RESULTS.md) | API test results and coverage | 2026-03-23 |
| [DEPLOYMENT.md](./DEPLOYMENT.md) | Render deployment guide | 2026-03-23 |
| [progress/TODO.md](./progress/TODO.md) | Task list with priorities | 2026-03-23 |
| [progress/ONGOING.md](./progress/ONGOING.md) | Currently active tasks | 2026-03-23 |
| [progress/FINISHED.md](./progress/FINISHED.md) | Completed tasks | 2026-03-23 |

---

## Change Type → Documents Mapping

| Change Type | Documents to Update |
|---|---|
| New vault item type added | FEATURES.md, SRS.md, SDD.md, UI_UX_DOCUMENTATION.md, DIAGRAMS.md, TODO.md |
| New API endpoint | SDD.md, DIAGRAMS.md, API.md |
| New screen (Android or Web) | UI_UX_DOCUMENTATION.md, UI_DESIGN_SYSTEM.md, DIAGRAMS.md |
| Database schema change | SDD.md, DIAGRAMS.md (ER), API.md |
| Feature completed | FEATURES.md (status), TODO.md → FINISHED.md |
| Security model change | SRS.md, SDD.md, DIAGRAMS.md |
| Deployment change | DEPLOYMENT.md |

---

## Quick Links

- **Android App:** `/android/` — Kotlin + Jetpack Compose
- **Web Application:** `/web/` — Laravel 12 + Blade + REST API
- **Backend API:** `/web/` — Laravel 12 REST API with Firebase Auth + Firestore
- **Render Deployment:** `/render.yaml`, `/web/render-build.sh`, `/web/render-run.sh`
- **Release Automation:** `/fastlane/` — Android deployment

---

## Architecture Overview

```
truvalt/
├── android/          ← Kotlin + Compose Android app
├── web/              ← Laravel 12 + Blade web vault + Firebase-backed REST API
├── docs/             ← Shared documentation
└── fastlane/         ← Release automation
```

---

## Version Information

- **Current Version:** 1.0.0
- **Target SDK:** API 36 (Android 14)
- **Min SDK:** API 26 (Android 8.0)
- **Backend Identity/Storage:** Firebase Authentication + Cloud Firestore
- **Local Storage:** Room (Android local)
- **Encryption:** AES-256-GCM + Argon2id
