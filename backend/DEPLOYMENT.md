# Deployment Guide

## Render Blueprint

The repository now includes a Render Blueprint at `/render.yaml` for the Laravel API in `/web`.

It provisions:

- A Docker-based web service for the Laravel API
- A Render Key Value instance for sessions and cache
- An external managed PostgreSQL database supplied via `DB_URL`

The Blueprint is configured for this monorepo layout, uses `/api/health` for health checks, deploys the backend from the `web/` subdirectory, and expects the production database URL to be provided as a secret environment variable.

## Recommended Hosts

- DigitalOcean
- Hetzner
- Any VPS with Ubuntu 22.04

---

## Prerequisites

- Docker 24+
- Docker Compose 2+
- Git
- Domain with SSL certificate

---

## Docker Configuration

### Dockerfile

```dockerfile
FROM php:8.3-fpm

# Install system dependencies
RUN apt-get update && apt-get install -y \
    git \
    curl \
    libpng-dev \
    libonig-dev \
    libxml2-dev \
    libpq-dev \
    libzip-dev \
    unzip \
    redis-tools \
    nodejs \
    npm

# Clear cache
RUN apt-get clean && rm -rf /var/lib/apt/lists/*

# Install PHP extensions
RUN docker-php-ext-install pdo pdo_pgsql mbstring exif pcntl bcmath gd

# Install Redis extension
RUN pecl install redis && docker-php-ext-enable redis

# Get Composer
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www

# Copy application
COPY . .

# Install dependencies
RUN composer install --no-dev --optimize-autoloader

# Set permissions
RUN chown -R www-data:www-data /var/www \
    && chmod -R 755 /var/www/storage \
    && chmod -R 755 /var/www/bootstrap/cache

# Expose port 9000
EXPOSE 9000

CMD ["php-fpm"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: truvalt-app
    restart: unless-stopped
    volumes:
      - .:/var/www
      - ./storage:/var/www/storage
    environment:
      - APP_ENV=production
      - APP_DEBUG=false
      - DB_HOST=postgres
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
    networks:
      - truvalt

  nginx:
    image: nginx:alpine
    container_name: truvalt-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - .:/var/www
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - app
    networks:
      - truvalt

  postgres:
    image: postgres:16-alpine
    container_name: truvalt-postgres
    restart: unless-stopped
    environment:
      - POSTGRES_DB=truvalt
      - POSTGRES_USER=truvalt
      - POSTGRES_PASSWORD=your_secure_password
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - truvalt

  redis:
    image: redis:alpine
    container_name: truvalt-redis
    restart: unless-stopped
    volumes:
      - redis_data:/data
    networks:
      - truvalt

networks:
  truvalt:
    driver: bridge

volumes:
  postgres_data:
  redis_data:
```

### nginx.conf

```nginx
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    sendfile on;
    keepalive_timeout 65;

    server {
        listen 80;
        server_name your-domain.com;
        return 301 https://$server_name$request_uri;
    }

    server {
        listen 443 ssl http2;
        server_name your-domain.com;

        ssl_certificate /etc/nginx/ssl/cert.pem;
        ssl_certificate_key /etc/nginx/ssl/key.pem;
        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;

        root /var/www/public;
        index index.php index.html;

        location / {
            try_files $uri $uri/ /index.php?$query_string;
        }

        location ~ \.php$ {
            fastcgi_pass app:9000;
            fastcgi_index index.php;
            fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
            include fastcgi_params;
        }

        location ~ /\.ht {
            deny all;
        }

        location /storage {
            alias /var/www/storage/app/public;
        }
    }
}
```

---

## GitHub Actions CI/CD

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: shivammathur/setup-php@v2
        with:
          php-version: '8.3'
      - uses: actions/cache@v3
        with:
          path: vendor
          key: ${{ runner.os }}-composer-${{ hashFiles('**/composer.lock') }}
      - run: composer install --no-dev
      - run: cp .env.example .env
      - run: php artisan key:generate
      - name: Run tests
        run: ./vendor/bin/pest --coverage

  deploy:
    needs: test
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    steps:
      - name: Deploy to server
        uses: appleboy/ssh-action@v1
        with:
          host: ${{ secrets.HOST }}
          username: ${{ secrets.USERNAME }}
          key: ${{ secrets.SSH_KEY }}
          script: |
            cd /var/www/truvalt
            git pull origin main
            docker-compose build
            docker-compose up -d
            docker-compose exec -T app php artisan migrate --force
            docker-compose exec -T app php artisan optimize
```

---

## Database Migration

```bash
# Run migrations
php artisan migrate --force

# Seed database (if needed)
php artisan db:seed
```

---

## Environment Configuration

Set these in your production `.env`:

```env
APP_NAME=truvalt
APP_ENV=production
APP_DEBUG=false
APP_URL=https://your-domain.com

DB_CONNECTION=pgsql
DB_HOST=postgres
DB_PORT=5432
DB_DATABASE=truvalt
DB_USERNAME=truvalt
DB_PASSWORD=your_secure_password

CACHE_STORE=redis
QUEUE_CONNECTION=redis
SESSION_DRIVER=redis
REDIS_HOST=redis
REDIS_PORT=6379
```

---

## Health Check Endpoint

GET `/api/health` returns:

```json
{
  "status": "ok",
  "version": "1.0.0"
}
```

Add to `routes/api.php`:

```php
Route::get('/health', function () {
    return response()->json([
        'status' => 'ok',
        'version' => config('app.version', '1.0.0')
    ]);
});
```

---

## Monitoring

### Laravel Telescope (Development)

```bash
composer require laravel/telescope --dev
php artisan telescope:install
php artisan migrate
```

### Sentry (Production)

```bash
composer require sentry/sentry-laravel
```

Add to `config/logging.php`:

```php
'channels' => [
    'sentry' => [
        'driver' => 'sentry',
        'level' => 'error',
    ],
],
```

### Log Files

Logs are written to `/var/log/truvalt/`. Configure log rotation:

```bash
sudo vim /etc/logrotate.d/truvalt
```

```
/var/log/truvalt/*.log {
    daily
    missingok
    rotate 14
    compress
    delaycompress
    notifempty
    create 0640 www-data www-data
}
```

---

## SSL Certificate

Using Let's Encrypt:

```bash
# Install Certbot
sudo apt install certbot python3-certbot-nginx

# Generate certificate
sudo certbot --nginx -d your-domain.com

# Auto-renewal
sudo certbot renew --dry-run
```

---

## Initial Setup Steps

1. **Deploy the application:**
   ```bash
   git clone your-repo.git
   cp .env.example .env
   docker-compose build
   docker-compose up -d
   ```

2. **Configure environment:**
   ```bash
   docker-compose exec -T app php artisan key:generate
   ```

3. **Run migrations:**
   ```bash
   docker-compose exec -T app php artisan migrate --force
   ```

4. **Create first user:**
   Visit `https://your-domain.com/register` or use API endpoint.

5. **Test health check:**
   ```bash
   curl https://your-domain.com/api/health
   ```
