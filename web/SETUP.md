# Truvalt Backend - Laravel 12

## ✅ Installation Complete!

### Installed Components
- **PHP 8.4.16** with all required extensions
- **Composer 2.8.8**
- **PostgreSQL 17.9** (running)
- **Redis 8.0.2** (installed)
- **Laravel 12.55.1**
- **Laravel Sanctum 4.3.1**

### Database Setup
- Database: `truvalt`
- User: `truvalt`
- Password: `truvalt_dev_password`
- All 18 tables created successfully

### Tables Created
✅ users, folders, tags, vault_items, vault_item_tags  
✅ audit_logs, passkeys, share_links, devices  
✅ sessions, personal_access_tokens (Sanctum)  
✅ cache, jobs, migrations (Laravel)

## Quick Start

### Start Development Server
```bash
cd ~/repos/Truvalt/web
php artisan serve
```

Server will be available at: http://localhost:8000

### Run Tests
```bash
php artisan test
```

### Database Commands
```bash
# Run migrations
php artisan migrate

# Rollback migrations
php artisan migrate:rollback

# Fresh migration
php artisan migrate:fresh
```

## Next Steps

1. **Authentication Controllers** - Implement register/login/2FA
2. **Vault Sync API** - CRUD endpoints for vault items
3. **Audit Logging Service** - Track all operations
4. **API Tests** - Write Pest tests for all endpoints

## Environment Configuration

The `.env` file is configured with:
- PostgreSQL connection (pgsql)
- Database: truvalt
- Redis for cache/queue
- App key generated

## API Development

Start building controllers in:
- `app/Http/Controllers/Auth/` - Authentication
- `app/Http/Controllers/Api/` - Vault sync endpoints

Models are ready in `app/Models/`:
- User, VaultItem, Folder, Tag
- Session, AuditLog, Passkey, ShareLink, Device

## Zero-Knowledge Architecture

- Server never decrypts vault data
- All encryption/decryption happens client-side
- Server stores only encrypted blobs
- Auth key hash uses Argon2id

## API Endpoints (To Be Implemented)

### Authentication
- `POST /api/register` - Register new user
- `POST /api/login` - Login with auth key
- `POST /api/logout` - Logout current session
- `POST /api/2fa/enable` - Enable 2FA
- `POST /api/2fa/verify` - Verify 2FA code

### Vault Sync
- `GET /api/vault/items` - Get all vault items (delta sync)
- `POST /api/vault/items` - Create vault item
- `PUT /api/vault/items/{id}` - Update vault item
- `DELETE /api/vault/items/{id}` - Soft delete vault item
- `POST /api/vault/sync` - Batch sync with conflict resolution

### Folders & Tags
- `GET /api/folders` - Get all folders
- `POST /api/folders` - Create folder
- `GET /api/tags` - Get all tags
- `POST /api/tags` - Create tag

### Sessions & Audit
- `GET /api/sessions` - Get active sessions
- `DELETE /api/sessions/{id}` - Revoke session
- `GET /api/audit-logs` - Get audit logs
