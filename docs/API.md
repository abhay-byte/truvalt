# Truvalt API Documentation

**Base URL:** `http://127.0.0.1:8000/api`  
**Backend Auth:** Firebase Authentication  
**Protected Route Auth:** `Authorization: Bearer <firebase_id_token>`  
**Primary Backend Storage:** Cloud Firestore  
**Last Updated:** `2026-03-23`

**Live Backend Verification:** Email/password + Firestore paths verified against the real `truvalt` Firebase project on `2026-03-23`. `POST /login/google` remains pending a live Google ID token.

---

## Overview

The Laravel backend now uses Firebase for account/session identity and Firestore for backend vault metadata and encrypted blobs.

- Public utility routes do not require Firebase configuration.
- Public auth routes require a configured Firebase project plus:
  - `FIREBASE_PROJECT_ID`
  - `FIREBASE_CREDENTIALS` or `FIREBASE_CREDENTIALS_JSON`
  - `FIREBASE_WEB_API_KEY`
- Protected routes verify Firebase ID tokens server-side before touching Firestore.
- `encrypted_data` is still expected as base64-encoded encrypted client data.
- The backend stores encrypted blobs only; it does not decrypt vault payloads.
- The Android app now stores the returned Firebase `token`, optional `refresh_token`, and backend `user.id` locally for cloud-mode requests and sync.

---

## Public Utility Endpoints

### `GET /health`

Simple readiness probe.

**Response**
```json
{
  "status": "ok"
}
```

### `GET /keep-alive`

Public route for uptime pings.

**Response**
```json
{
  "status": "ok",
  "purpose": "keep-alive",
  "timestamp": "2026-03-23T10:21:31+00:00"
}
```

---

## Authentication

### `POST /register`

Create a Firebase email/password account and bootstrap the Truvalt Firestore profile.

**Request**
```json
{
  "email": "user@example.com",
  "password": "secret12",
  "auth_key_hash": "optional_client_derived_vault_auth_material"
}
```

Notes:
- `password` is handled by Firebase Authentication.
- `auth_key_hash` is optional metadata. When supplied, the backend stores only an Argon2id hash of that value in the Firestore user profile.

**Response**
```json
{
  "user": {
    "id": "firebase_uid",
    "email": "user@example.com",
    "providers": ["password"],
    "auth_key_hash_configured": true,
    "created_at": 1774260000,
    "updated_at": 1774260000,
    "last_login_at": 1774260000,
    "email_verified": false
  },
  "token": "firebase_id_token",
  "refresh_token": "firebase_refresh_token",
  "expires_in": 3600
}
```

### `POST /login`

Sign in with Firebase email/password and return a Firebase ID token.

**Request**
```json
{
  "email": "user@example.com",
  "password": "secret12",
  "auth_key_hash": "optional_client_derived_vault_auth_material"
}
```

If `auth_key_hash` is supplied and the profile already has a stored Argon2id hash, the backend verifies it before completing the login response.

**Response**
```json
{
  "user": {
    "id": "firebase_uid",
    "email": "user@example.com",
    "providers": ["password"],
    "auth_key_hash_configured": true,
    "created_at": 1774260000,
    "updated_at": 1774260100,
    "last_login_at": 1774260100,
    "email_verified": false
  },
  "token": "firebase_id_token",
  "refresh_token": "firebase_refresh_token",
  "expires_in": 3600
}
```

### `POST /login/google`

Sign in with a Google ID token through Firebase Authentication.

**Request**
```json
{
  "id_token": "google_identity_token",
  "auth_key_hash": "optional_client_derived_vault_auth_material"
}
```

**Response**
```json
{
  "user": {
    "id": "firebase_uid",
    "email": "user@example.com",
    "providers": ["google.com"],
    "auth_key_hash_configured": false,
    "created_at": 1774260000,
    "updated_at": 1774260200,
    "last_login_at": 1774260200,
    "email_verified": true
  },
  "token": "firebase_id_token",
  "refresh_token": "firebase_refresh_token",
  "expires_in": 3600
}
```

### `GET /me`

Return the authenticated Firestore user profile.

**Headers**
- `Authorization: Bearer <firebase_id_token>`

**Response**
```json
{
  "id": "firebase_uid",
  "email": "user@example.com",
  "providers": ["password"],
  "auth_key_hash_configured": true,
  "created_at": 1774260000,
  "updated_at": 1774260100,
  "last_login_at": 1774260100,
  "email_verified": false
}
```

### `POST /logout`

Revoke Firebase refresh tokens for the authenticated account.

**Headers**
- `Authorization: Bearer <firebase_id_token>`

**Response**
```json
{
  "message": "Logged out successfully. Firebase refresh tokens for this account were revoked."
}
```

Important:
- This is broader than deleting a single Sanctum token.
- Existing Firebase ID tokens remain usable until they expire unless the client refreshes or the server checks revocation. The backend is configured to check revocation during token verification.

---

## Vault Items

All vault item routes require a Firebase bearer token.

### `GET /vault/items`

List non-deleted items for the authenticated user.

**Query Params**
- `updated_after` optional integer Unix timestamp
- `type` optional string
- `folder_id` optional string

**Response**
```json
[
  {
    "id": "uuid",
    "user_id": "firebase_uid",
    "type": "login",
    "name": "GitHub",
    "folder_id": "uuid",
    "encrypted_data": "YmFzZTY0LWVuY29kZWQtYmxvYg==",
    "favorite": true,
    "created_at": 1774260000,
    "updated_at": 1774260100,
    "deleted_at": null
  }
]
```

### `POST /vault/items`

Create an item.

**Request**
```json
{
  "type": "login",
  "name": "GitHub",
  "encrypted_data": "YmFzZTY0LWVuY29kZWQtYmxvYg==",
  "folder_id": "uuid",
  "favorite": false
}
```

Rules:
- `encrypted_data` must be valid base64.
- `folder_id`, when present, must belong to the authenticated user.

### `GET /vault/items/{id}`

Return one non-deleted item.

### `PUT /vault/items/{id}`

Update an item.

**Request Fields**
- `name` optional string
- `encrypted_data` optional base64 string
- `folder_id` optional string or `null`
- `favorite` optional boolean

### `DELETE /vault/items/{id}`

Soft-delete an item by setting `deleted_at`.

**Response**
```json
{
  "message": "Item moved to trash"
}
```

### `GET /vault/trash`

List soft-deleted items.

### `POST /vault/items/{id}/restore`

Restore a soft-deleted item.

### `POST /vault/sync`

Batch upsert with last-write-wins conflict detection.

**Request**
```json
{
  "items": [
    {
      "id": "uuid",
      "type": "login",
      "name": "GitHub",
      "encrypted_data": "YmFzZTY0LWVuY29kZWQtYmxvYg==",
      "folder_id": null,
      "favorite": false,
      "created_at": 1774260000,
      "updated_at": 1774260100,
      "deleted_at": null
    }
  ]
}
```

**Response**
```json
{
  "synced": [
    {
      "id": "uuid",
      "user_id": "firebase_uid",
      "type": "login",
      "name": "GitHub",
      "folder_id": null,
      "encrypted_data": "YmFzZTY0LWVuY29kZWQtYmxvYg==",
      "favorite": false,
      "created_at": 1774260000,
      "updated_at": 1774260100,
      "deleted_at": null
    }
  ],
  "conflicts": []
}
```

Conflict rule:
- If the server copy has a newer `updated_at` than the incoming item, the server copy is returned in `conflicts` and the incoming copy is skipped.

---

## Folders

### `GET /folders`

List folders sorted by name.

### `POST /folders`

Create a folder.

**Request**
```json
{
  "id": "optional-client-uuid",
  "name": "Work",
  "icon": "briefcase",
  "parent_id": null
}
```

Rules:
- `id` is optional. When supplied, the backend preserves the client UUID so Android Room IDs and Firestore IDs stay aligned.
- `parent_id`, when present, must belong to the authenticated user.

### `PUT /folders/{id}`

Update a folder.

Rules:
- A folder cannot be its own parent.
- `parent_id`, when present, must belong to the authenticated user.

### `DELETE /folders/{id}`

Delete a folder.

Current backend behavior:
- Child folders referencing that folder have `parent_id` cleared.
- Vault items referencing that folder have `folder_id` cleared.

---

## Tags

### `GET /tags`

List tags sorted by name.

### `POST /tags`

Create a tag.

**Request**
```json
{
  "id": "optional-client-uuid",
  "name": "important"
}
```

Rules:
- `id` is optional. When supplied, the backend preserves the client UUID so Android Room IDs and Firestore IDs stay aligned.

### `DELETE /tags/{id}`

Delete a tag.

---

## Error Responses

### `401 Unauthorized`

```json
{
  "message": "Unauthenticated."
}
```

Returned when:
- The bearer token is missing.
- The Firebase ID token is invalid.
- The Firebase ID token is revoked and revocation checking is enabled.

### `422 Unprocessable Entity`

Typical causes:
- Invalid email/password payloads
- Invalid Google ID token
- Invalid base64 `encrypted_data`
- Cross-user `folder_id` or `parent_id`

Example:
```json
{
  "message": "The given data was invalid.",
  "errors": {
    "encrypted_data": [
      "The encrypted_data field must be valid base64."
    ]
  }
}
```

---

## Storage Model

The backend writes Firestore documents under the authenticated user namespace:

- `users/{uid}`
- `users/{uid}/vault_items/{itemId}`
- `users/{uid}/folders/{folderId}`
- `users/{uid}/tags/{tagId}`

This keeps data ownership explicit and simplifies per-user authorization checks in Laravel.

---

## Testing Notes

- `web/test-api.sh` now supports a smoke-test mode with no Firebase credentials.
- Full live auth flow testing requires:
  - server-side Firebase credentials/env vars configured
  - `TEST_PASSWORD` for email/password auth
  - optional `GOOGLE_ID_TOKEN` for the Google sign-in endpoint
- The current automated PHPUnit suite verifies route wiring, middleware, and controller behavior with mocked Firebase/Firestore services.
