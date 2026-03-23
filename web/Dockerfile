FROM php:8.4-fpm-alpine

RUN apk add --no-cache \
    zip \
    unzip \
    git \
    curl \
    nodejs \
    npm \
    nginx \
    supervisor

RUN docker-php-ext-install pdo

COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

WORKDIR /var/www

COPY . .

RUN chmod +x /var/www/render-build.sh /var/www/render-run.sh
RUN /var/www/render-build.sh

COPY docker/nginx.conf /etc/nginx/nginx.conf
COPY docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 10000

CMD ["/var/www/render-run.sh"]
