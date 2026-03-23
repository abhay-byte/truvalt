# Deployment Guide

## Current Backend Deployment Model

Truvalt’s Laravel backend now expects Firebase Auth + Firestore instead of PostgreSQL-backed request-path storage.

### Required Firebase Configuration

| Variable | Required | Purpose |
|---|---|---|
| `FIREBASE_PROJECT_ID` | Yes | Firebase / GCP project identifier |
| `FIREBASE_CREDENTIALS` | Yes* | Path to service-account JSON |
| `FIREBASE_CREDENTIALS_JSON` | Yes* | Inline service-account JSON alternative |
| `FIREBASE_WEB_API_KEY` | Yes | Firebase Auth REST API key |
| `FIREBASE_AUTH_REDIRECT_URI` | No | Redirect URI used for `signInWithIdp`; default `http://localhost` |
| `FIRESTORE_DATABASE` | No | Firestore database name; default `(default)` |
| `FIREBASE_CHECK_REVOKED_TOKENS` | No | Enable revocation checks on protected routes; default `true` |

\* Provide either `FIREBASE_CREDENTIALS` or `FIREBASE_CREDENTIALS_JSON`.

### Recommended Laravel Runtime Settings

| Variable | Recommended Value | Why |
|---|---|---|
| `SESSION_DRIVER` | `file` | Avoid SQL-backed sessions |
| `CACHE_STORE` | `file` | Avoid SQL-backed cache |
| `QUEUE_CONNECTION` | `sync` | Keep deployment simple |
| `APP_ENV` | `production` | Standard production mode |
| `APP_DEBUG` | `false` | Disable debug in production |

### SQL Status

- PostgreSQL is no longer required for the normal backend request path.
- You can keep legacy SQL config present, but the Firebase/Firestore backend path should not depend on it.
- If a deployment still has `DB_URL` from the previous architecture, it is now optional unless other unported features still rely on SQL.

---

## Render Deployment

### Render Service Shape

Only one web service is required for the current backend path:

```
┌─────────────────┐
│   truvalt-api   │
│   Laravel 12    │
│   Port: 10000   │
│   Health: /api/health
└────────┬────────┘
         │
    ┌────┴──────────────┐
    │                   │
┌───▼───────────┐ ┌─────▼────────────┐
│ Firebase Auth │ │ Cloud Firestore  │
└───────────────┘ └──────────────────┘
```

### Render Build / Run Scripts

The repository now includes dedicated deployment scripts in `/web`:

| Script | Purpose |
|---|---|
| `render-build.sh` | Install Composer dependencies, install NPM dependencies, build frontend assets, prepare writable Laravel directories |
| `render-run.sh` | Clear stale Laravel caches, then start Supervisor/Nginx/PHP-FPM |

How Render uses them today:
- `render.yaml` provisions a Docker-based web service.
- `web/Dockerfile` runs `./render-build.sh` during image build.
- `web/Dockerfile` uses `./render-run.sh` as the container command.
- You do not need separate Render dashboard build/start commands when using the checked-in Docker Blueprint.

If you later switch the Render service away from Docker to a native runtime, use:

```bash
Build Command: ./render-build.sh
Start Command: ./render-run.sh
```

### Environment Variables in Render

Set these in the Render dashboard for `truvalt-api`:

```bash
APP_KEY=<generated-by-render-or-artisan>
APP_ENV=production
APP_DEBUG=false
SESSION_DRIVER=file
CACHE_STORE=file
QUEUE_CONNECTION=sync
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_WEB_API_KEY=your-web-api-key
FIREBASE_CREDENTIALS_JSON={"type":"service_account",...}
FIREBASE_AUTH_REDIRECT_URI=http://localhost
FIRESTORE_DATABASE=(default)
FIREBASE_CHECK_REVOKED_TOKENS=true
```

Notes:
- `FIREBASE_CREDENTIALS_JSON` is usually easier on Render than mounting a file path.
- Keep the service-account JSON private and out of git.
- Do not commit Firebase secrets into `.env.example` or docs.
- `APP_KEY` can be generated automatically by Render from the Blueprint.
- The current Firebase/Firestore request path does not require `DB_URL`, `DB_PASSWORD`, or `REDIS_URL`.
- The Android app should point its server URL to the public Render hostname, for example `https://truvalt-api.onrender.com`.

---

## Local Development

### 1. Configure Environment

```bash
cd ~/repos/Truvalt/web
cp .env.example .env
```

Add at least:

```bash
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_WEB_API_KEY=your-web-api-key
FIREBASE_CREDENTIALS=/absolute/path/to/service-account.json
```

### 2. Start the Server

```bash
php artisan serve --host=127.0.0.1 --port=8000
```

### 3. Smoke Test

```bash
./test-api.sh
```

For full live auth + Firestore verification:

```bash
TEST_PASSWORD='your-test-password' ./test-api.sh
```

Optional Google sign-in endpoint verification:

```bash
GOOGLE_ID_TOKEN='...' TEST_PASSWORD='your-test-password' ./test-api.sh
```

---

## Operational Notes

### Public Routes

These should work even if Firebase credentials are missing:
- `GET /api/health`
- `GET /api/keep-alive`

That is intentional so the app can still boot and expose readiness probes before Firebase-backed routes are exercised.

### Protected Routes

These require valid Firebase configuration:
- all bearer-token protected API endpoints
- all Firestore-backed reads/writes

### Logout Behavior

`POST /api/logout` revokes Firebase refresh tokens for the authenticated user. This is broader than deleting a single Laravel token and should be treated as account-level refresh-token revocation.

---

## Security Notes

- Prefer `FIREBASE_CREDENTIALS_JSON` only in encrypted secret stores.
- Rotate the Firebase service-account key periodically.
- Restrict who can view or edit production environment variables.
- Keep `FIREBASE_CHECK_REVOKED_TOKENS=true` unless you have a measured reason to relax it.
- The backend stores encrypted vault blobs only; do not add server-side vault decryption.
