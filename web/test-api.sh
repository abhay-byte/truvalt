#!/bin/bash

set -euo pipefail

BASE_URL="${BASE_URL:-http://127.0.0.1:8000/api}"
RUN_ID="$(date +%s)-$$"

TEST_EMAIL="${TEST_EMAIL:-api-test-${RUN_ID}@example.com}"
TEST_PASSWORD="${TEST_PASSWORD:-}"
TEST_AUTH_KEY_HASH="${TEST_AUTH_KEY_HASH:-vault_auth_${RUN_ID}}"
GOOGLE_ID_TOKEN="${GOOGLE_ID_TOKEN:-}"

TOKEN=""
FOLDER_ID=""
TAG_ID=""
ITEM_ID=""
SYNC_ITEM_ID=""

LAST_STATUS=""
LAST_BODY=""

request() {
  local method="$1"
  local path="$2"
  local data="${3:-}"
  local auth_header=()
  local tmp_body

  tmp_body="$(mktemp)"

  if [[ -n "$TOKEN" ]]; then
    auth_header=(-H "Authorization: Bearer $TOKEN")
  fi

  if [[ -n "$data" ]]; then
    LAST_STATUS="$(
      curl -sS -o "$tmp_body" -w "%{http_code}" -X "$method" "$BASE_URL$path" \
        "${auth_header[@]}" \
        -H "Content-Type: application/json" \
        -d "$data"
    )"
  else
    LAST_STATUS="$(
      curl -sS -o "$tmp_body" -w "%{http_code}" -X "$method" "$BASE_URL$path" \
        "${auth_header[@]}"
    )"
  fi

  LAST_BODY="$(cat "$tmp_body")"
  rm -f "$tmp_body"
}

assert_status() {
  local expected="$1"
  local label="$2"

  if [[ "$LAST_STATUS" != "$expected" ]]; then
    echo "[FAIL] $label"
    echo "Expected status: $expected"
    echo "Actual status:   $LAST_STATUS"
    print_body
    exit 1
  fi

  echo "[PASS] $label ($LAST_STATUS)"
}

print_body() {
  printf '%s' "$LAST_BODY" | php -r '
    $input = stream_get_contents(STDIN);
    $data = json_decode($input, true);
    if (json_last_error() === JSON_ERROR_NONE) {
        echo json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES), PHP_EOL;
        exit(0);
    }
    echo $input, PHP_EOL;
  '
}

json_get() {
  local path="$1"

  printf '%s' "$LAST_BODY" | php -r '
    $path = $argv[1];
    $data = json_decode(stream_get_contents(STDIN), true);

    if (!is_array($data)) {
        exit(1);
    }

    $value = $data;

    foreach (explode(".", $path) as $segment) {
        if (is_array($value) && array_key_exists($segment, $value)) {
            $value = $value[$segment];
        } else {
            exit(1);
        }
    }

    if (is_bool($value)) {
        echo $value ? "true" : "false";
    } elseif (is_scalar($value)) {
        echo $value;
    } else {
        echo json_encode($value, JSON_UNESCAPED_SLASHES);
    }
  ' "$path"
}

echo "=== Truvalt Firebase API Tests ==="
echo "Base URL: $BASE_URL"
echo "Run ID: $RUN_ID"
echo

request GET "/health"
assert_status "200" "GET /health"
print_body
echo

request GET "/keep-alive"
assert_status "200" "GET /keep-alive"
print_body
echo

request GET "/vault/items"
assert_status "401" "GET /vault/items without auth"
print_body
echo

if [[ -z "$TEST_PASSWORD" ]]; then
  echo "[SKIP] Live Firebase auth flow tests require TEST_PASSWORD and a configured Firebase project."
  exit 0
fi

request POST "/register" "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\",\"auth_key_hash\":\"$TEST_AUTH_KEY_HASH\"}"
assert_status "201" "POST /register"
print_body
TOKEN="$(json_get "token")"
echo

request POST "/login" "{\"email\":\"$TEST_EMAIL\",\"password\":\"$TEST_PASSWORD\",\"auth_key_hash\":\"$TEST_AUTH_KEY_HASH\"}"
assert_status "200" "POST /login"
print_body
TOKEN="$(json_get "token")"
echo

request GET "/me"
assert_status "200" "GET /me"
print_body
echo

request POST "/folders" '{"name":"Work","icon":"briefcase"}'
assert_status "201" "POST /folders"
print_body
FOLDER_ID="$(json_get "id")"
echo

request GET "/folders"
assert_status "200" "GET /folders"
print_body
echo

request PUT "/folders/$FOLDER_ID" '{"name":"Work Updated","icon":"building"}'
assert_status "200" "PUT /folders/{id}"
print_body
echo

request POST "/tags" '{"name":"important"}'
assert_status "201" "POST /tags"
print_body
TAG_ID="$(json_get "id")"
echo

request GET "/tags"
assert_status "200" "GET /tags"
print_body
echo

LOGIN_BLOB="$(printf 'encrypted_password_data_%s' "$RUN_ID" | base64 -w 0)"
request POST "/vault/items" "{\"type\":\"login\",\"name\":\"GitHub\",\"encrypted_data\":\"$LOGIN_BLOB\",\"folder_id\":\"$FOLDER_ID\",\"favorite\":true}"
assert_status "201" "POST /vault/items"
print_body
ITEM_ID="$(json_get "id")"
echo

request GET "/vault/items"
assert_status "200" "GET /vault/items"
print_body
echo

request GET "/vault/items/$ITEM_ID"
assert_status "200" "GET /vault/items/{id}"
print_body
echo

UPDATED_BLOB="$(printf 'updated_encrypted_password_data_%s' "$RUN_ID" | base64 -w 0)"
request PUT "/vault/items/$ITEM_ID" "{\"name\":\"GitHub Updated\",\"encrypted_data\":\"$UPDATED_BLOB\",\"favorite\":false}"
assert_status "200" "PUT /vault/items/{id}"
print_body
echo

request GET "/vault/items?type=login"
assert_status "200" "GET /vault/items?type=login"
print_body
echo

request GET "/vault/items?folder_id=$FOLDER_ID"
assert_status "200" "GET /vault/items?folder_id={folder_id}"
print_body
echo

DELTA_TS="$(date +%s)"
sleep 1
request PUT "/vault/items/$ITEM_ID" '{"name":"GitHub Delta Updated"}'
assert_status "200" "PUT /vault/items/{id} delta setup"
print_body
echo

request GET "/vault/items?updated_after=$DELTA_TS"
assert_status "200" "GET /vault/items?updated_after={timestamp}"
print_body
echo

request DELETE "/vault/items/$ITEM_ID"
assert_status "200" "DELETE /vault/items/{id}"
print_body
echo

request GET "/vault/trash"
assert_status "200" "GET /vault/trash"
print_body
echo

request POST "/vault/items/$ITEM_ID/restore"
assert_status "200" "POST /vault/items/{id}/restore"
print_body
echo

SYNC_ITEM_ID="$(uuidgen)"
SYNC_TS="$(date +%s)"
SYNC_BLOB="$(printf 'sync_item_blob_%s' "$RUN_ID" | base64 -w 0)"
request POST "/vault/sync" "{\"items\":[{\"id\":\"$SYNC_ITEM_ID\",\"type\":\"login\",\"name\":\"Twitter\",\"encrypted_data\":\"$SYNC_BLOB\",\"folder_id\":null,\"favorite\":false,\"created_at\":$SYNC_TS,\"updated_at\":$SYNC_TS}]}"
assert_status "200" "POST /vault/sync create"
print_body
echo

sleep 1
SERVER_WIN_BLOB="$(printf 'server_wins_blob_%s' "$RUN_ID" | base64 -w 0)"
request PUT "/vault/items/$SYNC_ITEM_ID" "{\"name\":\"Twitter Server Updated\",\"encrypted_data\":\"$SERVER_WIN_BLOB\",\"favorite\":true}"
assert_status "200" "PUT /vault/items/{id} conflict setup"
print_body
echo

OLDER_SYNC_BLOB="$(printf 'older_sync_blob_%s' "$RUN_ID" | base64 -w 0)"
request POST "/vault/sync" "{\"items\":[{\"id\":\"$SYNC_ITEM_ID\",\"type\":\"login\",\"name\":\"Twitter Older Client Copy\",\"encrypted_data\":\"$OLDER_SYNC_BLOB\",\"folder_id\":null,\"favorite\":false,\"created_at\":$SYNC_TS,\"updated_at\":$SYNC_TS}]}"
assert_status "200" "POST /vault/sync conflict"
print_body
echo

request DELETE "/tags/$TAG_ID"
assert_status "200" "DELETE /tags/{id}"
print_body
echo

request DELETE "/folders/$FOLDER_ID"
assert_status "200" "DELETE /folders/{id}"
print_body
echo

if [[ -n "$GOOGLE_ID_TOKEN" ]]; then
  request POST "/login/google" "{\"id_token\":\"$GOOGLE_ID_TOKEN\"}"
  assert_status "200" "POST /login/google"
  print_body
  echo
fi

request POST "/logout"
assert_status "200" "POST /logout"
print_body
echo

TOKEN=""
request GET "/vault/items"
assert_status "401" "GET /vault/items after logout without auth header"
print_body
echo

echo "=== All tests passed ==="
