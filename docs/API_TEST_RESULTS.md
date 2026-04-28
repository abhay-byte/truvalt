# Truvalt API Test Results — ARCHIVED

> **This document is archived.** The Laravel REST backend has been removed.
>
> Truvalt now communicates **directly with Firebase** — there is no intermediate backend server or REST API.
> All operations are performed via the Firebase Android SDK.

---

## Test Execution Summary

**Date:** 2026-03-23  
**Base URL:** `http://127.0.0.1:8000/api`  
**Backend Mode Tested:** Laravel + Firebase Authentication + Cloud Firestore *(removed on 2026-04-28)*  
**Firebase Project:** `truvalt`  
**Live Firebase Admin Credentials:** Configured

---

## Result

**Status:** ✅ Live Firebase-backed backend verification passed

Verified in this round:
- Live Firebase email/password registration and login
- Live Firestore-backed user profile, folder, tag, and vault item persistence
- Live sync conflict handling against Firestore
- Live logout refresh-token revocation endpoint
- Live unauthenticated JSON `401` handling
- PHPUnit route/middleware coverage

Not live-tested in this round:
- `POST /api/login/google`

Reason:
- A valid Google ID token was not supplied for the test run.

---

## Live Route Coverage

### ✅ Utility Routes

1. `GET /api/health` → `200 OK`
2. `GET /api/keep-alive` → `200 OK`

### ✅ Authentication Routes

3. `POST /api/register` → `201 Created`
4. `POST /api/login` → `200 OK`
5. `GET /api/me` → `200 OK`
6. `POST /api/logout` → `200 OK`

### ✅ Vault Routes

7. `GET /api/vault/items` → `200 OK`
8. `POST /api/vault/items` → `201 Created`
9. `GET /api/vault/items/{id}` → `200 OK`
10. `PUT /api/vault/items/{id}` → `200 OK`
11. `DELETE /api/vault/items/{id}` → `200 OK`
12. `GET /api/vault/trash` → `200 OK`
13. `POST /api/vault/items/{id}/restore` → `200 OK`
14. `POST /api/vault/sync` create → `200 OK`
15. `POST /api/vault/sync` conflict → `200 OK`

### ✅ Folder Routes

16. `POST /api/folders` → `201 Created`
17. `GET /api/folders` → `200 OK`
18. `PUT /api/folders/{id}` → `200 OK`
19. `DELETE /api/folders/{id}` → `200 OK`

### ✅ Tag Routes

20. `POST /api/tags` → `201 Created`
21. `GET /api/tags` → `200 OK`
22. `DELETE /api/tags/{id}` → `200 OK`

### ✅ Auth Failure Handling

23. `GET /api/vault/items` without bearer token → `401 Unauthorized`

---

## Live Scenarios Verified

### Scenario 1: Full Firebase Email/Password Flow

1. Register a new Firebase-backed user
2. Log in with the same email/password
3. Retrieve `/api/me`
4. Revoke tokens through `/api/logout`

**Result:** ✅ Passed

### Scenario 2: Firestore CRUD

1. Create folder
2. Create tag
3. Create vault item referencing that folder
4. Read, update, filter, soft-delete, trash-list, and restore the item
5. Delete tag and folder

**Result:** ✅ Passed

### Scenario 3: Delta Sync

1. Capture a timestamp
2. Update a vault item after that timestamp
3. Query `GET /api/vault/items?updated_after=...`

**Result:** ✅ Passed

### Scenario 4: Conflict Detection

1. Create an item through `POST /api/vault/sync`
2. Update that item through `PUT /api/vault/items/{id}`
3. Re-submit an older client copy through `POST /api/vault/sync`

**Result:** ✅ Passed, newer Firestore copy returned in `conflicts`

### Scenario 5: Unauthorized Access

1. Call a protected route without a bearer token
2. Verify the backend returns JSON `401`

**Result:** ✅ Passed

---

## Automated PHPUnit Coverage

The mocked feature suite still passes:

```text
Tests: 8 passed (21 assertions)
```

Covered there:
- `POST /api/register`
- `POST /api/login/google`
- `POST /api/logout`
- `GET /api/me`
- `GET /api/vault/items`
- public health route
- missing bearer token handling

---

## Performance Observations

Observed during the live Firebase + Firestore run:

| Endpoint | Observed Range |
|---|---|
| `GET /api/health` | ~0.5-1s |
| `GET /api/keep-alive` | ~0.4s |
| `POST /api/register` | ~5s |
| `POST /api/login` | ~4s |
| `GET /api/me` | ~4s |
| `POST /api/folders` | ~4s |
| `POST /api/vault/items` | ~4s |
| `POST /api/vault/sync` | ~3-5s |
| `POST /api/logout` | ~4s |

This is materially better than the previous external PostgreSQL path, but the backend still pays remote Firebase/Firestore network latency.

---

## Remaining Gaps

1. `POST /api/login/google` still needs one live verification run with a real Google ID token.
2. Android and web clients still need to be updated to use the Firebase-backed backend contract.
3. Audit logs, session listing, TOTP, passkeys, import/export, share links, and breach endpoints are still not implemented.

---

## Conclusion

The Laravel backend was live-verified against the real `truvalt` Firebase project for email/password auth and Firestore persistence on 2026-03-23. **On 2026-04-28, the Laravel backend was removed.** The Android app now communicates directly with Firebase Authentication and Cloud Firestore via the Firebase Android SDK. This archived document is kept for historical reference only.
