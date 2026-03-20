# Truvalt Backend - Laravel 12 Setup

## Overview

This directory contains the Laravel 12 backend for Truvalt. The backend has been initialized with:

✅ Database migrations for all tables (users, vault_items, folders, tags, sessions, audit_logs, passkeys, share_links, devices)
✅ Eloquent models with relationships
✅ Zero-knowledge architecture (server never decrypts vault data)

## Prerequisites

- PHP 8.3+
- Composer 2.x
- PostgreSQL 16+
- Redis (for cache/queue)
- Node.js 18+ (for assets)

## Installation Steps

### 1. Install PHP 8.3 and Extensions

```bash
# Debian/Ubuntu
sudo apt update
sudo apt install -y php8.3 php8.3-cli php8.3-fpm php8.3-pgsql php8.3-mbstring \
    php8.3-xml php8.3-curl php8.3-zip php8.3-gd php8.3-redis php8.3-bcmath \
    php8.3-intl php8.3-sodium
```

### 2. Install Composer

```bash
curl -sS https://getcomposer.org/installer | php
sudo mv composer.phar /usr/local/bin/composer
```

### 3. Install PostgreSQL

```bash
sudo apt install -y postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create database
sudo -u postgres createdb truvalt
sudo -u postgres psql -c "CREATE USER truvalt WITH PASSWORD 'your_password';"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE truvalt TO truvalt;"
```

### 4. Install Redis

```bash
sudo apt install -y redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

### 5. Initialize Laravel Project

```bash
cd ~/repos/Truvalt/web

# Install Laravel dependencies
composer install

# Copy environment file
cp .env.example .env

# Generate application key
php artisan key:generate

# Configure database in .env
# DB_CONNECTION=pgsql
# DB_HOST=127.0.0.1
# DB_PORT=5432
# DB_DATABASE=truvalt
# DB_USERNAME=truvalt
# DB_PASSWORD=your_password

# Run migrations
php artisan migrate

# Install Laravel Sanctum
composer require laravel/sanctum
php artisan vendor:publish --provider="Laravel\Sanctum\SanctumServiceProvider"

# Install additional packages
composer require pragmarx/google2fa-laravel
composer require asbiin/laravel-webauthn
composer require pestphp/pest --dev --with-all-dependencies
php artisan pest:install
```

### 6. Install Node Dependencies

```bash
npm install
npm run build
```

### 7. Start Development Server

```bash
# Terminal 1: Laravel server
php artisan serve

# Terminal 2: Queue worker
php artisan queue:work

# Terminal 3: Asset watcher (optional)
npm run dev
```

## Database Schema

The following tables have been created:

- **users** - User accounts with auth_key_hash and 2FA
- **vault_items** - Encrypted vault items (zero-knowledge)
- **folders** - Hierarchical folder structure
- **tags** - User-defined tags
- **vault_item_tags** - Many-to-many relationship
- **sessions** - Active user sessions
- **audit_logs** - Security audit trail
- **passkeys** - WebAuthn/FIDO2 credentials
- **share_links** - Encrypted item sharing
- **devices** - Registered devices with push tokens

## Next Steps

1. **TASK-001**: ✅ Laravel project initialized with migrations and models
2. **TASK-002**: ✅ PostgreSQL schema migrations created
3. **TODO**: Implement authentication controllers (register, login, 2FA)
4. **TODO**: Implement vault sync API endpoints
5. **TODO**: Implement audit logging service
6. **TODO**: Add API tests with Pest

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

## Security Notes

- All vault item data is encrypted client-side before reaching the server
- Server stores only encrypted blobs and never has access to decryption keys
- Auth key hash is derived using Argon2id (65536 KB memory, 3 iterations, 4 threads)
- All API endpoints require Sanctum token authentication
- Audit logs track all vault operations

## Testing

```bash
# Run all tests
php artisan test

# Run with Pest
./vendor/bin/pest

# Run with coverage
./vendor/bin/pest --coverage
```

## Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for production deployment instructions.
