# Laravel Folder Structure

```
web/                           # Laravel 12 application root
├── app/                       # Application code
│   ├── Console/               # Artisan commands
│   │   └── Kernel.php        # Console kernel
│   ├── Exceptions/            # Exception handling
│   │   └── Handler.php       # Exception handler
│   ├── Http/                  # HTTP layer
│   │   ├── Controllers/       # Controllers
│   │   │   ├── Api/           # API controllers
│   │   │   │   ├── AuthController.php      # Authentication endpoints
│   │   │   │   ├── VaultController.php     # Vault CRUD endpoints
│   │   │   │   ├── FolderController.php    # Folder endpoints
│   │   │   │   ├── TagController.php       # Tag endpoints
│   │   │   │   ├── SyncController.php     # Sync endpoints
│   │   │   │   ├── BreachController.php    # Breach check endpoints
│   │   │   │   ├── AuditController.php     # Audit log endpoints
│   │   │   │   ├── SessionController.php    # Session management
│   │   │   │   └── ShareLinkController.php # Share link endpoints
│   │   │   └── Web/             # Web controllers (Blade)
│   │   │       ├── AuthController.php      # Web auth
│   │   │       └── VaultController.php     # Web vault pages
│   │   ├── Middleware/          # HTTP middleware
│   │   │   ├── Authenticate.php # Sanctum auth
│   │   │   ├── Verify2FA.php   # 2FA enforcement
│   │   │   └── RateLimit.php    # Rate limiting
│   │   ├── Requests/            # Form request validation
│   │   │   ├── StoreVaultItemRequest.php
│   │   │   ├── UpdateVaultItemRequest.php
│   │   │   └── ImportVaultRequest.php
│   │   └── Kernel.php          # HTTP kernel
│   ├── Models/                 # Eloquent models
│   │   ├── User.php            # User model
│   │   ├── VaultItem.php       # Vault item model
│   │   ├── Folder.php          # Folder model
│   │   ├── Tag.php             # Tag model
│   │   ├── Session.php         # Session model
│   │   ├── AuditLog.php        # Audit log model
│   │   ├── Passkey.php         # WebAuthn passkey model
│   │   ├── ShareLink.php       # Share link model
│   │   └── Device.php          # Device model
│   ├── Providers/              # Service providers
│   │   ├── AppServiceProvider.php
│   │   ├── AuthServiceProvider.php
│   │   └── RouteServiceProvider.php
│   ├── Services/               # Business logic
│   │   ├── CryptoService.php       # Encryption utilities
│   │   ├── SyncService.php         # Sync logic
│   │   ├── BreachService.php       # HIBP integration
│   │   ├── AuthService.php         # Auth helpers
│   │   ├── ExportService.php       # Export logic
│   │   └── ImportService.php      # Import logic
│   ├── Repositories/           # Data access abstraction
│   │   ├── UserRepository.php
│   │   ├── VaultItemRepository.php
│   │   ├── FolderRepository.php
│   │   ├── TagRepository.php
│   │   └── AuditLogRepository.php
│   ├── Jobs/                   # Queue jobs
│   │   ├── ProcessBreachCheck.php
│   │   └── FlushAuditLog.php
│   └── Events/                 # Events
│       ├── VaultItemCreated.php
│       ├── VaultItemUpdated.php
│       └── LoginAttempt.php
├── bootstrap/                  # Application bootstrap
│   ├── app.php
│   └── providers.php
├── config/                     # Configuration files
│   ├── app.php
│   ├── auth.php
│   ├── cache.php
│   ├── cors.php
│   ├── database.php
│   ├── filesystems.php
│   ├── hashing.php
│   ├── logging.php
│   ├── mail.php
│   ├── queue.php
│   ├── sanctum.php
│   ├── session.php
│   └── view.php
├── database/                   # Database files
│   ├── migrations/             # Database migrations
│   │   ├── 2024_01_01_000001_create_users_table.php
│   │   ├── 2024_01_01_000002_create_vault_items_table.php
│   │   ├── 2024_01_01_000003_create_folders_table.php
│   │   ├── 2024_01_01_000004_create_tags_table.php
│   │   ├── 2024_01_01_000005_create_sessions_table.php
│   │   ├── 2024_01_01_000006_create_audit_logs_table.php
│   │   ├── 2024_01_01_000007_create_passkeys_table.php
│   │   ├── 2024_01_01_000008_create_share_links_table.php
│   │   └── 2024_01_01_000009_create_devices_table.php
│   ├── seeders/                # Database seeders
│   │   └── DatabaseSeeder.php
│   └── factories/              # Model factories
├── lang/                       # Localization files
│   └── en/
├── public/                     # Publicly accessible
│   ├── index.php               # Entry point
│   └── .htaccess
├── resources/                  # Resources
│   ├── css/                    # Tailwind CSS
│   ├── js/                     # Alpine.js
│   └── views/                  # Blade templates
│       ├── auth/               # Auth views (login, register)
│       ├── vault/              # Vault views
│       ├── settings/           # Settings views
│       ├── layouts/            # Master layouts
│       └── vendor/             # Vendor views
├── routes/                     # Routes
│   ├── api.php                 # API routes (/api/*)
│   ├── web.php                 # Web routes
│   ├── console.php             # Console routes
│   └── channels.php            # Broadcasting channels
├── storage/                    # Storage
│   ├── app/
│   ├── framework/              # Framework cache/sessions
│   └── logs/                   # Application logs
├── tests/                      # Tests
│   ├── Feature/                # Feature tests
│   │   ├── AuthTest.php
│   │   ├── VaultTest.php
│   │   └── SyncTest.php
│   ├── Unit/                   # Unit tests
│   │   ├── CryptoServiceTest.php
│   │   └── BreachServiceTest.php
│   ├── TestCase.php
│   └── CreatesApplication.php
├── .env                        # Environment file
├── .env.example                # Environment example
├── .gitignore
├── artisan                     # Artisan CLI
├── composer.json               # PHP dependencies
├── package.json                # Node dependencies
├── phpunit.xml                 # PHPUnit config
├── vite.config.js              # Vite config
└── README.md
```
