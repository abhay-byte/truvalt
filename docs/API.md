# Truvalt API Documentation

**Base URL:** `http://localhost:8000/api`

**Authentication:** Bearer Token (Sanctum)

---

## Authentication

### Register User

**POST** `/register`

Create a new user account.

**Request Body:**
```json
{
  "email": "user@example.com",
  "auth_key_hash": "argon2id_hash_of_derived_key"
}
```

**Response:** `201 Created`
```json
{
  "user": {
    "id": "uuid",
    "email": "user@example.com",
    "auth_key_hash": "argon2id_hash_of_derived_key",
    "two_factor_secret": null,
    "two_factor_confirmed_at": null,
    "created_at": "2026-03-20T11:00:00.000000Z",
    "updated_at": "2026-03-20T11:00:00.000000Z"
  },
  "token": "1|sanctum_token_here"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/register \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","auth_key_hash":"test_hash"}'
```

---

### Login

**POST** `/login`

Authenticate and receive access token.

**Request Body:**
```json
{
  "email": "user@example.com",
  "auth_key_hash": "argon2id_hash_of_derived_key"
}
```

**Response:** `200 OK`
```json
{
  "user": { ... },
  "token": "2|sanctum_token_here"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","auth_key_hash":"test_hash"}'
```

---

### Get Current User

**GET** `/me`

Get authenticated user information.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "created_at": "2026-03-20T11:00:00.000000Z",
  "updated_at": "2026-03-20T11:00:00.000000Z"
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8000/api/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Logout

**POST** `/logout`

Revoke current access token.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "message": "Logged out successfully"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/logout \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Vault Items

### List Vault Items

**GET** `/vault/items`

Get all vault items (excluding deleted).

**Headers:**
- `Authorization: Bearer {token}`

**Query Parameters:**
- `updated_after` (optional): Unix timestamp - Get items updated after this time
- `type` (optional): Filter by type (login, passkey, secure_note, etc.)
- `folder_id` (optional): Filter by folder UUID

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "user_id": "uuid",
    "type": "login",
    "name": "GitHub",
    "folder_id": "uuid",
    "encrypted_data": "base64_encrypted_blob",
    "favorite": true,
    "created_at": 1710936000,
    "updated_at": 1710936000,
    "deleted_at": null
  }
]
```

**cURL Examples:**
```bash
# Get all items
curl -X GET http://localhost:8000/api/vault/items \
  -H "Authorization: Bearer YOUR_TOKEN"

# Filter by type
curl -X GET "http://localhost:8000/api/vault/items?type=login" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Filter by folder
curl -X GET "http://localhost:8000/api/vault/items?folder_id=FOLDER_UUID" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Delta sync (get items updated after timestamp)
curl -X GET "http://localhost:8000/api/vault/items?updated_after=1710936000" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Create Vault Item

**POST** `/vault/items`

Create a new vault item.

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "type": "login",
  "name": "GitHub",
  "encrypted_data": "base64_encoded_encrypted_blob",
  "folder_id": "uuid",
  "favorite": false
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "user_id": "uuid",
  "type": "login",
  "name": "GitHub",
  "folder_id": "uuid",
  "encrypted_data": "base64_encrypted_blob",
  "favorite": false,
  "created_at": 1710936000,
  "updated_at": 1710936000,
  "deleted_at": null
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/vault/items \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "type": "login",
    "name": "GitHub",
    "encrypted_data": "'"$(echo 'encrypted_data' | base64)"'",
    "folder_id": null,
    "favorite": true
  }'
```

---

### Get Vault Item

**GET** `/vault/items/{id}`

Get a specific vault item by ID.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "user_id": "uuid",
  "type": "login",
  "name": "GitHub",
  "encrypted_data": "base64_encrypted_blob",
  "favorite": true,
  "created_at": 1710936000,
  "updated_at": 1710936000,
  "deleted_at": null
}
```

**cURL Example:**
```bash
curl -X GET http://localhost:8000/api/vault/items/ITEM_UUID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Update Vault Item

**PUT** `/vault/items/{id}`

Update an existing vault item.

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:** (all fields optional)
```json
{
  "name": "GitHub Updated",
  "encrypted_data": "new_base64_encoded_blob",
  "folder_id": "uuid",
  "favorite": false
}
```

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "name": "GitHub Updated",
  "updated_at": 1710936100,
  ...
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8000/api/vault/items/ITEM_UUID \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"GitHub Updated","favorite":false}'
```

---

### Delete Vault Item (Soft Delete)

**DELETE** `/vault/items/{id}`

Move item to trash (soft delete).

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "message": "Item moved to trash"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:8000/api/vault/items/ITEM_UUID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Get Trash Items

**GET** `/vault/trash`

Get all deleted vault items.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "name": "Deleted Item",
    "deleted_at": 1710936000,
    ...
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8000/api/vault/trash \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Restore Vault Item

**POST** `/vault/items/{id}/restore`

Restore a deleted item from trash.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "deleted_at": null,
  "updated_at": 1710936200,
  ...
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/vault/items/ITEM_UUID/restore \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Batch Sync

**POST** `/vault/sync`

Sync multiple vault items with conflict detection (last-write-wins).

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "items": [
    {
      "id": "uuid",
      "type": "login",
      "name": "Twitter",
      "encrypted_data": "base64_blob",
      "folder_id": null,
      "favorite": false,
      "created_at": 1710936000,
      "updated_at": 1710936000,
      "deleted_at": null
    }
  ]
}
```

**Response:** `200 OK`
```json
{
  "synced": [
    { "id": "uuid", "name": "Twitter", ... }
  ],
  "conflicts": [
    { "id": "uuid", "name": "Conflicted Item", ... }
  ]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/vault/sync \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "id": "'"$(uuidgen)"'",
        "type": "login",
        "name": "Twitter",
        "encrypted_data": "'"$(echo 'data' | base64)"'",
        "created_at": '"$(date +%s)"',
        "updated_at": '"$(date +%s)"'
      }
    ]
  }'
```

---

## Folders

### List Folders

**GET** `/folders`

Get all folders for the authenticated user.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "user_id": "uuid",
    "name": "Work",
    "icon": "briefcase",
    "parent_id": null,
    "updated_at": 1710936000
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8000/api/folders \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Create Folder

**POST** `/folders`

Create a new folder.

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "Work",
  "icon": "briefcase",
  "parent_id": null
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "user_id": "uuid",
  "name": "Work",
  "icon": "briefcase",
  "parent_id": null,
  "updated_at": 1710936000
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/folders \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Work","icon":"briefcase","parent_id":null}'
```

---

### Update Folder

**PUT** `/folders/{id}`

Update an existing folder.

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:** (all fields optional)
```json
{
  "name": "Work Updated",
  "icon": "building",
  "parent_id": "uuid"
}
```

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "name": "Work Updated",
  "icon": "building",
  "updated_at": 1710936100,
  ...
}
```

**cURL Example:**
```bash
curl -X PUT http://localhost:8000/api/folders/FOLDER_UUID \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Work Updated","icon":"building"}'
```

---

### Delete Folder

**DELETE** `/folders/{id}`

Delete a folder (cascades to child folders and items).

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "message": "Folder deleted"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:8000/api/folders/FOLDER_UUID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Tags

### List Tags

**GET** `/tags`

Get all tags for the authenticated user.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "user_id": "uuid",
    "name": "important"
  }
]
```

**cURL Example:**
```bash
curl -X GET http://localhost:8000/api/tags \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### Create Tag

**POST** `/tags`

Create a new tag.

**Headers:**
- `Authorization: Bearer {token}`

**Request Body:**
```json
{
  "name": "important"
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "user_id": "uuid",
  "name": "important"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8000/api/tags \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"important"}'
```

---

### Delete Tag

**DELETE** `/tags/{id}`

Delete a tag.

**Headers:**
- `Authorization: Bearer {token}`

**Response:** `200 OK`
```json
{
  "message": "Tag deleted"
}
```

**cURL Example:**
```bash
curl -X DELETE http://localhost:8000/api/tags/TAG_UUID \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Error Responses

### 401 Unauthorized
```json
{
  "message": "Unauthenticated."
}
```

### 404 Not Found
```json
{
  "message": "No query results for model [App\\Models\\VaultItem] uuid"
}
```

### 422 Validation Error
```json
{
  "message": "The email field is required.",
  "errors": {
    "email": ["The email field is required."]
  }
}
```

---

## Testing

Run the comprehensive test script:

```bash
cd ~/repos/Truvalt/web
php artisan serve  # In one terminal
./test-api.sh      # In another terminal
```

The test script will:
1. Register a new user
2. Login and get token
3. Create folders, tags, and vault items
4. Test all CRUD operations
5. Test filtering and delta sync
6. Test soft delete and restore
7. Test batch sync with conflicts
8. Clean up and logout

---

## Security Notes

- All vault item data (`encrypted_data`) must be base64-encoded encrypted blobs
- Server never decrypts vault data (zero-knowledge architecture)
- `auth_key_hash` is derived using Argon2id on the client
- All endpoints (except register/login) require Bearer token authentication
- Tokens are managed by Laravel Sanctum
