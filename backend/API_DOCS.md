# API Documentation

Base URL: `https://[your-domain]/api`

Authentication: `Authorization: Bearer {sanctum_token}` header on all authenticated routes.

---

## Utility Endpoints

### GET /keep-alive

Public keep-alive endpoint for external cron jobs and uptime pings.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/keep-alive` |
| Auth Required | No |

**Success Response (200):**
```json
{
  "status": "ok",
  "purpose": "keep-alive",
  "timestamp": "2026-03-22T12:00:00Z"
}
```

---

## Zero-Knowledge Constraint

**Important:** The server never decrypts vault items. The `encrypted_data` field is always an opaque base64 string from the server's perspective. All encryption and decryption happens client-side.

---

## Authentication Endpoints

### POST /auth/register

Register a new user account.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/auth/register` |
| Auth Required | No |

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "master_password",
  "password_hint": "Optional hint"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "user": {
      "id": "uuid",
      "email": "user@example.com"
    },
    "token": "sanctum_token"
  }
}
```

**Error Responses:**

| Status | Message |
|---|---|
| 422 | Validation error with `errors` object |
| 409 | Email already registered |

---

### POST /auth/login

Login with email and auth key (derived from master password).

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/auth/login` |
| Auth Required | No |

**Request Body:**
```json
{
  "email": "user@example.com",
  "auth_key": "derived_auth_key"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "uuid",
      "email": "user@example.com",
      "two_factor_enabled": true
    },
    "token": "sanctum_token",
    "requires_2fa": false
  }
}
```

**Error Responses:**

| Status | Message |
|---|---|
| 422 | Validation error |
| 401 | Invalid credentials |
| 429 | Too many login attempts |

---

### POST /auth/logout

Logout current session.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/auth/logout` |
| Auth Required | Yes |

**Success Response (200):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

---

### POST /auth/two-factor/setup

Initialize TOTP 2FA enrollment.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/auth/two-factor/setup` |
| Auth Required | Yes |

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "secret": "JBSWY3DPEHPK3PXP",
    "qr_code": "data:image/png;base64,...",
    "otpauth_url": "otpauth://totp/..."
  }
}
```

---

### POST /auth/two-factor/verify

Verify TOTP code.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/auth/two-factor/verify` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "code": "123456"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "message": "2FA enabled successfully"
}
```

---

## Vault Endpoints

### GET /vault/items

List all vault items for the authenticated user.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/vault/items` |
| Auth Required | Yes |

**Success Response (200):**
```json
{
  "success": true,
  "data": [
    {
      "id": "uuid",
      "type": "LOGIN",
      "name": "GitHub",
      "folder_id": "uuid",
      "favorite": false,
      "created_at": 1700000000000,
      "updated_at": 1700000000000,
      "sync_status": "SYNCED"
    }
  ]
}
```

---

### POST /vault/items

Create a new vault item.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/vault/items` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "type": "LOGIN",
  "name": "GitHub",
  "folder_id": "uuid",
  "favorite": false,
  "encrypted_data": "base64_encrypted_blob"
}
```

**Success Response (201):**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "type": "LOGIN",
    "name": "GitHub",
    "created_at": 1700000000000,
    "updated_at": 1700000000000,
    "sync_status": "SYNCED"
  }
}
```

---

### GET /vault/items/{id}

Get a single vault item.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/vault/items/{id}` |
| Auth Required | Yes |

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "type": "LOGIN",
    "name": "GitHub",
    "folder_id": "uuid",
    "favorite": false,
    "encrypted_data": "base64_encrypted_blob",
    "created_at": 1700000000000,
    "updated_at": 1700000000000,
    "sync_status": "SYNCED"
  }
}
```

---

### PUT /vault/items/{id}

Update a vault item.

| Field | Value |
|---|---|
| Method | PUT |
| Path | `/api/vault/items/{id}` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "name": "GitHub Updated",
  "encrypted_data": "base64_encrypted_blob"
}
```

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "uuid",
    "updated_at": 1700000000000
  }
}
```

---

### DELETE /vault/items/{id}

Delete a vault item (soft delete).

| Field | Value |
|---|---|
| Method | DELETE |
| Path | `/api/vault/items/{id}` |
| Auth Required | Yes |

**Success Response (200):**
```json
{
  "success": true,
  "message": "Item moved to trash"
}
```

---

### GET /vault/folders

List all folders.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/folders` |
| Auth Required | Yes |

---

### POST /vault/folders

Create a folder.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/folders` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "name": "Work Accounts",
  "icon": "briefcase",
  "parent_id": "uuid (optional)"
}
```

---

### PUT /vault/folders/{id}

Update a folder.

| Field | Value |
|---|---|
| Method | PUT |
| Path | `/api/folders/{id}` |
| Auth Required | Yes |

---

### DELETE /vault/folders/{id}

Delete a folder.

| Field | Value |
|---|---|
| Method | DELETE |
| Path | `/api/folders/{id}` |
| Auth Required | Yes |

---

### GET /vault/tags

List all tags.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/tags` |
| Auth Required | Yes |

---

### POST /vault/tags

Create a tag.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/tags` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "name": "important"
}
```

---

### DELETE /vault/tags/{id}

Delete a tag.

| Field | Value |
|---|---|
| Method | DELETE |
| Path | `/api/tags/{id}` |
| Auth Required | Yes |

---

### POST /vault/sync

Delta sync with server.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/vault/sync` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "last_sync": 1700000000000,
  "items": [
    {
      "id": "uuid",
      "type": "LOGIN",
      "name": "GitHub",
      "encrypted_data": "base64",
      "deleted_at": null,
      "updated_at": 1700000000000
    }
  ]
}
```

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "items": [...],
    "folders": [...],
    "tags": [...],
    "server_timestamp": 1700000000000
  }
}
```

---

### POST /vault/export

Export vault data.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/vault/export` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "format": "json" // or "csv", "truvalt"
}
```

---

### POST /vault/import

Import vault data.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/vault/import` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "format": "bitwarden_json",
  "data": "base64_encoded_content"
}
```

---

## Breach Check Endpoints

### GET /breach/check

Check passwords against Have I Been Pwned (k-anonymity).

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/breach/check` |
| Auth Required | Yes |

**Note:** The app sends only the first 5 characters of the SHA-1 hash of the password (k-anonymity).

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "checked": 10,
    "breached": 2,
    "breached_items": [
      {"item_id": "uuid", "name": "GitHub"}
    ]
  }
}
```

---

## Audit & Sessions Endpoints

### GET /audit/log

Get audit log.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/audit/log` |
| Auth Required | Yes |

**Query Parameters:**
- `page` (optional)
- `per_page` (optional, default 50)
- `action` (optional, filter by action type)
- `from` (optional, timestamp)
- `to` (optional, timestamp)

---

### GET /sessions

List active sessions.

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/sessions` |
| Auth Required | Yes |

---

### DELETE /sessions/{id}

Revoke a session.

| Field | Value |
|---|---|
| Method | DELETE |
| Path | `/api/sessions/{id}` |
| Auth Required | Yes |

---

## Share Links Endpoints

### POST /share-links

Create a share link.

| Field | Value |
|---|---|
| Method | POST |
| Path | `/api/share-links` |
| Auth Required | Yes |

**Request Body:**
```json
{
  "item_id": "uuid",
  "expires_at": "2024-01-01T00:00:00Z",
  "max_views": 10
}
```

---

### GET /share-links/{token}

View a share link (public, unauthenticated).

| Field | Value |
|---|---|
| Method | GET |
| Path | `/api/share-links/{token}` |
| Auth Required | No |

**Success Response (200):**
```json
{
  "success": true,
  "data": {
    "encrypted_item": "base64_encrypted_blob",
    "expires_at": "2024-01-01T00:00:00Z",
    "views_remaining": 9
  }
}
```

---

## Error Response Format

All errors follow RFC 7807:

```json
{
  "success": false,
  "message": "Human-readable error message",
  "errors": {
    "field": ["Error message"]
  }
}
```

### Common HTTP Status Codes

| Status | Meaning |
|---|---|
| 200 | Success |
| 201 | Created |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 422 | Validation Error |
| 429 | Too Many Requests |
| 500 | Server Error |
