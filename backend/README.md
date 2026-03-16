# truvalt Backend

## Tech Stack

- **PHP:** 8.3
- **Framework:** Laravel 12
- **Database:** PostgreSQL 16
- **Cache/Queue:** Redis
- **Web Server:** Nginx
- **Authentication:** Laravel Sanctum, TOTP (pragmarx/google2fa), WebAuthn (asbiin/laravel-webauthn)
- **Encryption:** Argon2id via PHP `password_hash` with `PASSWORD_ARGON2ID`

---

## Local Development Setup

### 1. Prerequisites

- PHP 8.3+
- Composer
- PostgreSQL 16+
- Redis
- Node.js 18+ (for building assets)

### 2. Installation

```bash
# Clone the repository
cd /path/to/truvalt/web

# Install PHP dependencies
composer install

# Install Node dependencies
npm install

# Create environment file
cp .env.example .env

# Generate application key
php artisan key:generate

# Create database
createdb truvalt

# Run migrations
php artisan migrate

# Build assets
npm run build
```

### 3. Run Development Server

```bash
# Start Laravel development server
php artisan serve

# In another terminal, start queue worker
php artisan queue:work
```

### 4. Testing

```bash
# Run all tests
php artisan test

# Or use Pest
./vendor/bin/pest
```

---

## Environment Variables

| Variable | Description | Default |
|---|---|---|
| `APP_NAME` | Application name | truvalt |
| `APP_ENV` | Environment | local |
| `APP_DEBUG` | Debug mode | true |
| `APP_URL` | Application URL | http://localhost |
| `DB_CONNECTION` | Database driver | pgsql |
| `DB_HOST` | Database host | 127.0.0.1 |
| `DB_PORT` | Database port | 5432 |
| `DB_DATABASE` | Database name | truvalt |
| `DB_USERNAME` | Database user | postgres |
| `DB_PASSWORD` | Database password | |
| `CACHE_DRIVER` | Cache driver | redis |
| `QUEUE_CONNECTION` | Queue driver | redis |
| `SESSION_DRIVER` | Session driver | redis |
| `REDIS_HOST` | Redis host | 127.0.0.1 |
| `REDIS_PORT` | Redis port | 6379 |
| `MAIL_MAILER` | Mail driver | smtp |
| `MAIL_HOST` | SMTP host | |
| `MAIL_PORT` | SMTP port | 587 |
| `MAIL_USERNAME` | SMTP username | |
| `MAIL_PASSWORD` | SMTP password | |
| `MAIL_FROM_ADDRESS` | From address | noreply@example.com |
| `HIBP_API_URL` | HIBP API URL | https://api.pwnedpasswords.com |
| `ALLOW_REGISTRATION` | Allow new registrations | true |
| `INSTANCE_NAME` | Instance name | truvalt Self-Hosted |
| `ARGON2_MEMORY` | Argon2 memory (KB) | 65536 |
| `ARGON2_THREADS` | Argon2 threads | 4 |
| `ARGON2_TIME` | Argon2 time | 3 |

---

## Queue Worker

For processing background jobs (breach checks, audit logging):

```bash
php artisan queue:work --queue=default,high,low
```

For Supervisor configuration:

```ini
[program:truvalt-worker]
process_name=%(program_name)s_%(process_num)02d
command=php /path/to/truvalt/web/artisan queue:work --sleep=3 --tries=3 --max-time=3600
autostart=true
autorestart=true
stopasgroup=true
killasgroup=true
user=www-data
numprocs=2
redirect_stderr=true
stdout_logfile=/var/log/truvalt-worker.log
stopwaitsecs=3600
```

---

## Testing Commands

```bash
# Run all tests
php artisan test

# Run specific test file
./vendor/bin/pest tests/Feature/AuthTest.php

# Run unit tests only
./vendor/bin/pest tests/Unit

# Run with coverage
./vendor/bin/pest --coverage
```

---

## Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for complete deployment instructions.

---

## Zero-Knowledge Architecture

The backend is designed as a **zero-knowledge encrypted blob store**:

- **Server never decrypts vault items** — all encryption/decryption happens client-side
- Server stores only:
  - User metadata (email, auth key hash)
  - Encrypted vault item blobs
  - Audit logs
  - Session tokens
- Auth key hash is derived from master password + email using Argon2id
- This ensures even server administrators cannot access user vault data
