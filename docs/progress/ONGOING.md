# ONGOING - Currently Active Tasks

---

## Summary

| Category | Count |
|---|---|
| Tasks In Progress | 1 |
| Tasks Blocked | 0 |

---

## Tasks In Progress

| Task ID | Description | Started | Progress |
|---|---|---|---|
| TASK-018 | Cloud sync implementation via direct Android → Firebase Auth + Firestore connection | 2026-03-23 | Android now connects directly to Firebase Auth and Cloud Firestore without routing through Laravel; Google sign-in is wired through Credential Manager + Firebase Auth; the debug signing config now matches the Firebase-registered SHA-1; Google OAuth now reads `default_web_client_id` from generated resources instead of a hardcoded value; fixed the Android Keystore `Caller-provided IV not permitted` failure by letting the keystore-generated AES/GCM IV be used during vault-key encryption; added a signed-in account card to Settings with profile photo/initial, email, provider label, and tappable account details; rebuilt and reinstalled the debug APK on 2026-03-29. Remaining validation is UI confirmation on-device. |

---

## Blocked Tasks

| Task ID | Description | Blocker | Notes |
|---|---|---|---|
