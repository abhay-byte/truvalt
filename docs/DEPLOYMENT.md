# Deployment Guide

## Render Deployment

Truvalt backend is configured for one-click deployment to Render using Infrastructure as Code (Blueprint).

### Prerequisites

- GitHub account
- Render account (free tier available)
- Repository pushed to GitHub

### Quick Deploy

1. **Go to Render Dashboard**
   - Visit [render.com](https://render.com)
   - Sign in with GitHub

2. **Create Blueprint**
   - Click "New" → "Blueprint"
   - Select `Truvalt` repository
   - Render auto-detects `/render.yaml`
   - Click "Apply"

3. **Services Created**
   - `truvalt-api` - Laravel web service
   - `truvalt-redis` - Redis cache/sessions
   - External managed PostgreSQL - configured via `DB_URL`

### Architecture

```
┌─────────────────┐
│   truvalt-api   │  (Web Service)
│   Laravel 12    │  Port: 10000
│   PHP 8.4 FPM   │  Health: /api/health
└────────┬────────┘
         │
    ┌────┴────┬──────────┐
    │         │          │
┌───▼───┐ ┌──▼──────┐ ┌─▼────────┐
│ Nginx │ │ PHP-FPM │ │Supervisor│
└───────┘ └─────────┘ └──────────┘
         │          │
    ┌────┴────┬─────┴────┐
    │         │          │
┌───▼──────────────┐ ┌──────▼──────┐
│ Managed Postgres │ │    Redis    │
│ (external DB_URL)│ │ (sessions)  │
└────────────┘ └───────────┘
```

### Environment Variables

Auto-configured by Render:

| Variable | Source | Description |
|----------|--------|-------------|
| `APP_KEY` | Generated | Laravel encryption key |
| `DB_CONNECTION` | Hardcoded | `pgsql` |
| `DB_URL` | Manual secret | External PostgreSQL connection string |
| `DB_SSLMODE` | Hardcoded | `require` |
| `REDIS_URL` | truvalt-redis | Redis connection |
| `APP_ENV` | Hardcoded | `production` |
| `APP_DEBUG` | Hardcoded | `false` |
| `SESSION_DRIVER` | Hardcoded | `redis` |
| `CACHE_STORE` | Hardcoded | `redis` |
| `QUEUE_CONNECTION` | Hardcoded | `sync` |

### Service Plans

**Free Tier (Default):**
- Web Service: 512 MB RAM, shared CPU
- PostgreSQL: 256 MB RAM, 1 GB storage
- Redis: 25 MB storage

**Upgrade Options:**
- Starter: $7/month per service
- Standard: $25/month per service
- Pro: $85/month per service

### Deployment Process

1. **Build Phase:**
   ```bash
   docker build -t truvalt-api .
   composer install --no-dev --optimize-autoloader
   npm install
   npm run build
   ```

2. **Deploy Phase:**
   ```bash
   supervisord -c /etc/supervisor/conf.d/supervisord.conf
   ```

   Database migrations are not yet automated in the current Render Blueprint, and `DB_URL` must be set manually in Render because the database is external.

3. **Health Check:**
   - Endpoint: `GET /api/health`
   - Expected: `{"status":"ok"}`
   - Interval: 30 seconds

4. **Keep-Alive Route:**
   - Endpoint: `GET /api/keep-alive`
   - Use for external cron pings
   - Expected: `{"status":"ok","purpose":"keep-alive","timestamp":"..."}`

### Auto-Deployment

Every push to `main` branch triggers:
1. Docker image rebuild
2. Zero-downtime deployment
3. Health check validation

### Manual Deployment

```bash
# Trigger manual deploy
git push origin main

# Or use Render CLI
render deploy --service truvalt-api
```

### Monitoring

**Render Dashboard provides:**
- Real-time logs
- Metrics (CPU, memory, requests)
- Deploy history
- Shell access

**Access logs:**
```bash
# Via Render CLI
render logs --service truvalt-api --tail

# Via Dashboard
Services → truvalt-api → Logs
```

### Database Management

**Run migrations:**
```bash
# Via Render shell
render shell truvalt-api
php artisan migrate
```

**Database backups:**
- Automatic daily backups (Pro plan)
- Manual backups via dashboard
- Point-in-time recovery (Pro plan)

### Scaling

**Horizontal Scaling:**
```yaml
# In render.yaml
services:
  - type: web
    name: truvalt-api
    numInstances: 3  # Add this line
```

**Vertical Scaling:**
- Change `plan` in render.yaml
- Or upgrade via dashboard

### Custom Domain

1. **Add domain in Render:**
   - Services → truvalt-api → Settings
   - Custom Domains → Add
   - Enter: `api.truvalt.com`

2. **Configure DNS:**
   ```
   Type: CNAME
   Name: api
   Value: truvalt-api.onrender.com
   ```

3. **SSL Certificate:**
   - Auto-provisioned by Render
   - Let's Encrypt (free)
   - Auto-renewal

### Troubleshooting

**Build fails:**
```bash
# Check Dockerfile syntax
docker build -t test .

# Verify dependencies
composer install
```

**Health check fails:**
```bash
# Test locally
curl http://localhost:10000/api/health

# Test keep-alive route
curl http://localhost:10000/api/keep-alive

# Check logs
render logs --service truvalt-api
```

**Database connection fails:**
```bash
# Verify DB_URL is set
render env --service truvalt-api

# Test connection
php artisan tinker
DB::connection()->getPdo();
```

**External database setup:**
- Set `DB_URL` manually in the Render Dashboard for `truvalt-api`
- Keep the database credential out of `render.yaml`
- Use `DB_SSLMODE=require` for managed PostgreSQL providers that enforce SSL
- Ensure the managed PostgreSQL endpoint is reachable over IPv4 from the deployment/runtime environment; an IPv6-only hostname can stall local verification and Render-side connectivity checks

### Security

**Automatic:**
- HTTPS enforced
- Environment variables encrypted
- Private network for database/redis
- DDoS protection

**Manual:**
- Restrict database IP allow list
- Enable 2FA on Render account
- Rotate APP_KEY periodically

### Cost Optimization

**Free Tier Limits:**
- Services spin down after 15 min inactivity
- 750 hours/month free
- Shared resources

**Tips:**
- Use free tier for development
- Upgrade production to Starter+
- Monitor usage in dashboard
- Set up billing alerts

### CI/CD Integration

**GitHub Actions example:**
```yaml
name: Deploy
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Trigger Render Deploy
        run: |
          curl -X POST \
            https://api.render.com/deploy/srv-xxx \
            -H "Authorization: Bearer ${{ secrets.RENDER_API_KEY }}"
```

### Rollback

**Via Dashboard:**
1. Services → truvalt-api → Deploys
2. Find previous successful deploy
3. Click "Rollback"

**Via CLI:**
```bash
render rollback --service truvalt-api
```

### Environment-Specific Configs

**Staging:**
```yaml
# render-staging.yaml
services:
  - type: web
    name: truvalt-api-staging
    branch: develop
    plan: free
```

**Production:**
```yaml
# render.yaml
services:
  - type: web
    name: truvalt-api
    rootDir: web
    plan: starter
```

### Support

- [Render Documentation](https://render.com/docs)
- [Render Community](https://community.render.com)
- [Status Page](https://status.render.com)
