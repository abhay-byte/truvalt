#!/bin/sh
set -eu

echo "==> Installing PHP dependencies"
composer install --no-dev --optimize-autoloader --no-interaction

echo "==> Installing frontend dependencies"
npm install

echo "==> Building frontend assets"
npm run build

echo "==> Preparing Laravel writable paths"
mkdir -p storage/framework/cache storage/framework/sessions storage/framework/views bootstrap/cache
chown -R www-data:www-data /var/www/storage /var/www/bootstrap/cache
