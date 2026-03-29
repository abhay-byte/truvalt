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
| TASK-018 | Cloud sync implementation via direct Android → Firebase Auth + Firestore connection | 2026-03-23 | Android now connects directly to Firebase Auth (createUserWithEmailAndPassword / signInWithEmailAndPassword) and Cloud Firestore without routing through Laravel; added FirebaseModule (Hilt), FirestoreVaultRepository (direct Firestore SDK), rewrote AuthRepositoryImpl and SyncRepositoryImpl; awaiting google-services.json placement and build verification; Google sign-in still pending |

---

## Blocked Tasks

| Task ID | Description | Blocker | Notes |
|---|---|---|---|
