# Truvalt API Test Results

## Test Execution Summary

**Date:** 2026-03-23  
**Base URL:** http://127.0.0.1:8000/api  
**Implemented Route Coverage:** 21 API routes  
**Additional Auth Check:** 1 unauthenticated verification (`GET /api/vault/items` → `401`)  
**Database:** External PostgreSQL over SSL

---

## Result

**Status:** ✅ Current implemented backend routes verified

- Authenticated route coverage passed end-to-end with the live Laravel server
- Final unauthenticated retest passed after fixing Laravel guest redirect handling for API routes
- Docs were updated to match the verified route surface, payload handling, and security behavior

---

## Route Coverage

### ✅ Utility Routes (2/2)

1. `GET /api/health` → `200 OK`
2. `GET /api/keep-alive` → `200 OK`

### ✅ Authentication Routes (4/4)

3. `POST /api/register` → `201 Created`
4. `POST /api/login` → `200 OK`
5. `GET /api/me` → `200 OK`
6. `POST /api/logout` → `200 OK`

### ✅ Vault Routes (8/8)

7. `GET /api/vault/items` → `200 OK`
8. `POST /api/vault/items` → `201 Created`
9. `GET /api/vault/items/{id}` → `200 OK`
10. `PUT /api/vault/items/{id}` → `200 OK`
11. `DELETE /api/vault/items/{id}` → `200 OK`
12. `GET /api/vault/trash` → `200 OK`
13. `POST /api/vault/items/{id}/restore` → `200 OK`
14. `POST /api/vault/sync` → `200 OK`

### ✅ Folder Routes (4/4)

15. `GET /api/folders` → `200 OK`
16. `POST /api/folders` → `201 Created`
17. `PUT /api/folders/{id}` → `200 OK`
18. `DELETE /api/folders/{id}` → `200 OK`

### ✅ Tag Routes (3/3)

19. `GET /api/tags` → `200 OK`
20. `POST /api/tags` → `201 Created`
21. `DELETE /api/tags/{id}` → `200 OK`

### ✅ Auth Failure Handling

22. `GET /api/vault/items` without a bearer token → `401 Unauthorized`

---

## Scenarios Verified

### Scenario 1: Full User Flow

1. Register a new user
2. Login and obtain a bearer token
3. Create and list folders
4. Create and list tags
5. Create, read, update, filter, soft-delete, trash-list, and restore a vault item
6. Logout

**Result:** ✅ Passed

### Scenario 2: Delta Sync

1. Capture a timestamp
2. Update a vault item after that timestamp
3. Query `GET /api/vault/items?updated_after=...`

**Result:** ✅ Passed

### Scenario 3: Sync Conflict Detection

1. Create a vault item through `POST /api/vault/sync`
2. Update that same item through `PUT /api/vault/items/{id}`
3. Re-submit an older copy through `POST /api/vault/sync`

**Result:** ✅ Passed, server version preserved in `conflicts`

### Scenario 4: Authorization

1. Access a protected endpoint without a token
2. Verify the API returns JSON `401` instead of redirecting

**Result:** ✅ Passed

---

## Backend Fixes Validated During Testing

- Sanctum token creation now works with UUID users because `personal_access_tokens` uses `uuidMorphs()`
- Auth key material is stored as an Argon2id hash and verified on login
- `encrypted_data` is returned as base64 in JSON responses even when PostgreSQL returns `bytea` streams
- Sync now preserves client-provided item UUIDs so follow-up CRUD operations target the correct item
- API auth middleware now returns JSON `401` for unauthenticated requests
- Folder ownership checks prevent cross-user `folder_id` and `parent_id` references

---

## Performance Observations

These timings came from the live run against the current external PostgreSQL setup and should be treated as environment-specific, not product targets.

| Endpoint | Observed Range |
|---|---|
| `GET /api/health` | ~0.5s |
| `POST /api/register` | ~8-9s |
| `POST /api/login` | ~8-9s |
| `GET /api/vault/items` | ~10-12s |
| `POST /api/vault/items` | ~11-12s |
| `PUT /api/vault/items/{id}` | ~10-12s |
| `POST /api/vault/sync` | ~10-13s |

The external database path is currently the dominant source of latency.

---

## Known Limitations

1. Implemented API surface is still limited to auth, vault items, folders, tags, health, and keep-alive
2. TOTP, passkey, audit log, sessions, import/export, breach, and share-link endpoints are documented in product planning but are not implemented in the current backend
3. External database latency is high in the current development environment
4. Pagination is not implemented; list endpoints currently return full result sets

---

## Conclusion

The currently implemented Laravel API is now functionally verified against the live server and external PostgreSQL backend. The remaining work is feature expansion and performance improvement, not basic route correctness.
