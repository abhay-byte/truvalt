#!/bin/bash

# Truvalt API Test Script
# Run Laravel server first: php artisan serve

BASE_URL="http://localhost:8000/api"
EMAIL="test@example.com"
AUTH_HASH="test_auth_key_hash_12345"

echo "=== Truvalt API Tests ==="
echo ""

# 1. Register
echo "1. Testing POST /api/register"
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/register" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"auth_key_hash\":\"$AUTH_HASH\"}")
echo "$REGISTER_RESPONSE" | jq '.'
TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token')
USER_ID=$(echo "$REGISTER_RESPONSE" | jq -r '.user.id')
echo "Token: $TOKEN"
echo ""

# 2. Login
echo "2. Testing POST /api/login"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$EMAIL\",\"auth_key_hash\":\"$AUTH_HASH\"}")
echo "$LOGIN_RESPONSE" | jq '.'
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token')
echo ""

# 3. Get current user
echo "3. Testing GET /api/me"
curl -s -X GET "$BASE_URL/me" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 4. Create folder
echo "4. Testing POST /api/folders"
FOLDER_RESPONSE=$(curl -s -X POST "$BASE_URL/folders" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Work","icon":"briefcase"}')
echo "$FOLDER_RESPONSE" | jq '.'
FOLDER_ID=$(echo "$FOLDER_RESPONSE" | jq -r '.id')
echo ""

# 5. Get all folders
echo "5. Testing GET /api/folders"
curl -s -X GET "$BASE_URL/folders" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 6. Create tag
echo "6. Testing POST /api/tags"
TAG_RESPONSE=$(curl -s -X POST "$BASE_URL/tags" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"important"}')
echo "$TAG_RESPONSE" | jq '.'
TAG_ID=$(echo "$TAG_RESPONSE" | jq -r '.id')
echo ""

# 7. Get all tags
echo "7. Testing GET /api/tags"
curl -s -X GET "$BASE_URL/tags" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 8. Create vault item
echo "8. Testing POST /api/vault/items"
ENCRYPTED_DATA=$(echo "encrypted_password_data" | base64)
ITEM_RESPONSE=$(curl -s -X POST "$BASE_URL/vault/items" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"type\":\"login\",\"name\":\"GitHub\",\"encrypted_data\":\"$ENCRYPTED_DATA\",\"folder_id\":\"$FOLDER_ID\",\"favorite\":true}")
echo "$ITEM_RESPONSE" | jq '.'
ITEM_ID=$(echo "$ITEM_RESPONSE" | jq -r '.id')
echo ""

# 9. Get all vault items
echo "9. Testing GET /api/vault/items"
curl -s -X GET "$BASE_URL/vault/items" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 10. Get vault item by ID
echo "10. Testing GET /api/vault/items/{id}"
curl -s -X GET "$BASE_URL/vault/items/$ITEM_ID" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 11. Update vault item
echo "11. Testing PUT /api/vault/items/{id}"
curl -s -X PUT "$BASE_URL/vault/items/$ITEM_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"GitHub Updated","favorite":false}' | jq '.'
echo ""

# 12. Filter by type
echo "12. Testing GET /api/vault/items?type=login"
curl -s -X GET "$BASE_URL/vault/items?type=login" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 13. Filter by folder
echo "13. Testing GET /api/vault/items?folder_id=$FOLDER_ID"
curl -s -X GET "$BASE_URL/vault/items?folder_id=$FOLDER_ID" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 14. Delete vault item (soft delete)
echo "14. Testing DELETE /api/vault/items/{id}"
curl -s -X DELETE "$BASE_URL/vault/items/$ITEM_ID" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 15. Get trash items
echo "15. Testing GET /api/vault/trash"
curl -s -X GET "$BASE_URL/vault/trash" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 16. Restore item
echo "16. Testing POST /api/vault/items/{id}/restore"
curl -s -X POST "$BASE_URL/vault/items/$ITEM_ID/restore" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 17. Batch sync
echo "17. Testing POST /api/vault/sync"
SYNC_DATA=$(cat <<EOF
{
  "items": [
    {
      "id": "$(uuidgen)",
      "type": "login",
      "name": "Twitter",
      "encrypted_data": "$(echo 'encrypted_twitter_data' | base64)",
      "folder_id": null,
      "favorite": false,
      "created_at": $(date +%s),
      "updated_at": $(date +%s)
    },
    {
      "id": "$(uuidgen)",
      "type": "secure_note",
      "name": "Secret Note",
      "encrypted_data": "$(echo 'encrypted_note_data' | base64)",
      "folder_id": null,
      "favorite": true,
      "created_at": $(date +%s),
      "updated_at": $(date +%s)
    }
  ]
}
EOF
)
curl -s -X POST "$BASE_URL/vault/sync" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "$SYNC_DATA" | jq '.'
echo ""

# 18. Update folder
echo "18. Testing PUT /api/folders/{id}"
curl -s -X PUT "$BASE_URL/folders/$FOLDER_ID" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Work Updated","icon":"building"}' | jq '.'
echo ""

# 19. Delete tag
echo "19. Testing DELETE /api/tags/{id}"
curl -s -X DELETE "$BASE_URL/tags/$TAG_ID" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 20. Delete folder
echo "20. Testing DELETE /api/folders/{id}"
curl -s -X DELETE "$BASE_URL/folders/$FOLDER_ID" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 21. Logout
echo "21. Testing POST /api/logout"
curl -s -X POST "$BASE_URL/logout" \
  -H "Authorization: Bearer $TOKEN" | jq '.'
echo ""

# 22. Test unauthorized access
echo "22. Testing unauthorized access (should fail)"
curl -s -X GET "$BASE_URL/vault/items" | jq '.'
echo ""

echo "=== All tests completed ==="
