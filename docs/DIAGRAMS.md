# Diagrams

> **Agent: update the relevant diagram whenever architecture, data, flows, or components change.**

---

## 1. Architecture Overview

```mermaid
flowchart TB
    subgraph Android["Android App"]
        UI["Compose UI"]
        VM["ViewModels"]
        UC["Use Cases"]
        Repo["Repositories"]
        Room["Room DB"]
        Crypto["Crypto Module"]
    end
    
    subgraph Web["Web Browser"]
        WebUI["Blade + Alpine"]
    end
    
    subgraph Backend["Laravel Backend"]
        API["REST API"]
        Controllers["Controllers"]
        Services["Services"]
        DB["PostgreSQL"]
    end
    
    subgraph External["External Services"]
        HIBP["HIBP API"]
        Push["Push Service"]
        FIDO["FIDO2 Server"]
    end
    
    UI --> VM
    VM --> UC
    UC --> Repo
    Repo --> Room
    Crypto --> Repo
    
    WebUI -->|HTTPS/REST| API
    API --> Controllers
    Controllers --> Services
    Services --> DB
    
    UC -->|HTTPS/REST| API
    Services --> HIBP
    Services --> Push
    Services --> FIDO
```

---

## 2. ER Diagram

```mermaid
erDiagram
    USERS ||--o{ SESSIONS : has
    USERS ||--o{ DEVICES : has
    USERS ||--o{ PASSKEYS : has
    USERS ||--o{ VAULT_ITEMS : owns
    USERS ||--o{ FOLDERS : owns
    USERS ||--o{ TAGS : owns
    USERS ||--o{ AUDIT_LOG : generates
    USERS ||--o{ SHARE_LINKS : creates
    VAULT_ITEMS ||--o{ VAULT_ITEM_TAGS : has
    FOLDERS ||--o{ FOLDERS : contains
    FOLDERS ||--o{ VAULT_ITEMS : contains
    TAGS ||--o{ VAULT_ITEM_TAGS : assigned_to
    
    USERS {
        string id PK
        string email
        string auth_key_hash
        string two_factor_secret
        datetime two_factor_confirmed_at
        datetime created_at
    }
    
    VAULT_ITEMS {
        string id PK
        string type
        string name
        string folder_id FK
        blob encrypted_data
        bool favorite
        int created_at
        int updated_at
        int deleted_at
        string sync_status
    }
    
    FOLDERS {
        string id PK
        string user_id FK
        string name
        string icon
        string parent_id FK
        int updated_at
    }
    
    TAGS {
        string id PK
        string user_id FK
        string name
    }
    
    VAULT_ITEM_TAGS {
        string item_id FK
        string tag_id FK
    }
    
    SESSIONS {
        string id PK
        string device_name
        int last_active_at
    }
    
    AUDIT_LOG {
        string id PK
        string action
        string item_id FK
        int performed_at
    }
    
    PASSKEYS {
        string id PK
        string user_id FK
        string credential_id
        string public_key
        int sign_count
    }
    
    SHARE_LINKS {
        string id PK
        string item_id FK
        blob encrypted_item_blob
        datetime expires_at
        int max_views
        int view_count
    }
    
    DEVICES {
        string id PK
        string user_id FK
        string name
        string platform
        string push_token
        datetime last_seen_at
    }
```

---

## 3. App Flow Flowchart

```mermaid
flowchart TD
    Start["App Launch"] --> CheckSession{Has valid session?}
    CheckSession -->|Yes| CheckBiometric{Biometric enabled?}
    CheckSession -->|No| SetupMode{First launch?}
    
    CheckBiometric -->|Yes| BiometricPrompt
    CheckBiometric -->|No| LoginScreen
    
    SetupMode -->|Yes| Onboarding
    SetupMode -->|No| ServerSetup
    
    Onboarding --> ServerSetup
    ServerSetup --> Register
    ServerSetup --> LoginScreen
    
    BiometricPrompt -->|Success| VaultHome
    BiometricPrompt -->|Fail| LoginScreen
    
    Register --> TwoFactorSetup
    Register --> VaultHome
    
    LoginScreen --> LoginVerify
    LoginVerify -->|No 2FA| VaultHome
    LoginVerify -->|Has 2FA| TwoFactorVerify
    TwoFactorVerify -->|Success| VaultHome
    TwoFactorVerify -->|Fail| LoginScreen
    
    VaultHome --> Search
    VaultHome --> CreateItem
    VaultHome --> OpenItem
    VaultHome --> Generator
    VaultHome --> Health
    VaultHome --> Settings
    
    Settings --> Profile
    Settings --> Security
    Settings --> Sessions
    Settings --> AuditLog
    Settings --> Appearance
    Settings --> SyncSettings
    Settings --> Logout
    
    Logout --> LoginScreen
    
    subgraph Offline["Offline Mode"]
        OfflineCheck{Offline detected?}
        OfflineCheck -->|Yes| LocalVault
        OfflineCheck -->|No| CloudSync
    end
    
    VaultHome --> OfflineCheck
```

---

## 4. Component Diagram

```mermaid
classDiagram
    class CryptoManager {
        +deriveKey(email, password): ByteArray
        +encrypt(data, key): EncryptedBlob
        +decrypt(blob, key): ByteArray
        +generateVaultKey(): ByteArray
    }
    
    class VaultRepository {
        +getItems(): Flow~List~VaultItem~~
        +saveItem(item): Result
        +deleteItem(id): Result
        +sync(): Result
    }
    
    class SyncManager {
        +pushChanges(): Result
        +pullChanges(): Result
        +resolveConflicts(): Result
    }
    
    class BiometricManager {
        +authenticate(): Result
        +storeKey(key): Result
        +retrieveKey(): ByteArray
    }
    
    class ClipboardManager {
        +copy(value, timeout)
        +clear()
    }
    
    class VaultItemViewModel {
        +uiState: UiState
        +loadItems()
        +saveItem()
        +deleteItem()
    }
    
    CryptoManager --> VaultRepository
    SyncManager --> VaultRepository
    VaultItemViewModel --> VaultRepository
    VaultItemViewModel --> CryptoManager
    VaultItemViewModel --> BiometricManager
    VaultItemViewModel --> ClipboardManager
```

---

## 5. Class Diagram

```mermaid
classDiagram
    class VaultItem {
        +id: String
        +type: VaultItemType
        +name: String
        +folderId: String?
        +encryptedData: ByteArray
        +favorite: Boolean
        +createdAt: Long
        +updatedAt: Long
        +syncStatus: SyncStatus
    }
    
    class Folder {
        +id: String
        +name: String
        +icon: String?
        +parentId: String?
        +updatedAt: Long
    }
    
    class Tag {
        +id: String
        +name: String
    }
    
    class VaultRepository {
        +getAllItems(): Flow~List~VaultItem~~
        +getItemById(id): VaultItem
        +saveItem(item): Result
        +deleteItem(id): Result
    }
    
    class CryptoManager {
        +deriveKeyFromPassword(): ByteArray
        +encryptVaultItem(): ByteArray
        +decryptVaultItem(): ByteArray
    }
    
    class SyncManager {
        +syncWithServer(): Result
        +pushChanges(): Result
        +pullChanges(): Result
    }
    
    VaultItem --> Folder: belongs to
    VaultItem --> "*" Tag: has
    VaultRepository --> VaultItem
    VaultRepository --> Folder
    VaultRepository --> CryptoManager
    SyncManager --> VaultRepository
```

---

## 6. Object Diagram

```mermaid
classDiagram
    class VaultItem {
        <<Login Item Example>>
        id: "550e8400-e29b-41d4-a716-446655440000"
        type: "LOGIN"
        name: "GitHub Account"
        encryptedData: "[AES-256-GCM encrypted blob]"
    }
    
    class EncryptedData {
        iv: "a1b2c3d4e5f6"
        ciphertext: "..."
        authTag: "..."
    }
    
    class Folder {
        id: "660e8400-e29b-41d4-a716-446655440001"
        name: "Development"
        icon: "code"
    }
    
    class Tag {
        id: "770e8400-e29b-41d4-a716-446655440002"
        name: "work"
    }
    
    VaultItem --> EncryptedData: contains
    VaultItem --> Folder: in
    VaultItem --> Tag: tagged with
```

---

## 7. Sequence - Login Flow

```mermaid
sequenceDiagram
    participant User
    participant LoginScreen
    participant LoginViewModel
    participant AuthRepository
    participant CryptoManager
    participant Retrofit
    participant LaravelAPI
    participant Database
    
    User->>LoginScreen: Enter email + password
    LoginScreen->>LoginViewModel: login(email, password)
    
    LoginViewModel->>CryptoManager: deriveKey(email, password)
    CryptoManager-->>LoginViewModel: authKey, masterKey
    
    LoginViewModel->>AuthRepository: login(email, authKey)
    AuthRepository->>Retrofit: POST /auth/login
    Retrofit->>LaravelAPI: HTTPS Request
    LaravelAPI->>Database: Verify auth key hash
    Database-->>LaravelAPI: User record
    LaravelAPI-->>Retrofit: {token, requires2FA}
    Retrofit-->>AuthRepository: Response
    AuthRepository-->>LoginViewModel: AuthResult
    
    alt requires 2FA
        LoginViewModel->>LoginScreen: Navigate to 2FA
        User->>LoginScreen: Enter TOTP code
        LoginScreen->>LoginViewModel: verify2FA(code)
        LoginViewModel->>AuthRepository: verify2FA(token, code)
        AuthRepository-->>LoginViewModel: Success
    end
    
    LoginViewModel->>LoginScreen: Navigate to VaultHome
    LoginViewModel->>CryptoManager: storeMasterKey(masterKey)
```

---

## 8. Sequence - Vault Sync

```mermaid
sequenceDiagram
    participant AndroidApp
    participant SyncManager
    participant VaultRepository
    participant RoomDAO
    participant Retrofit
    participant LaravelAPI
    participant PostgreSQL
    
    AndroidApp->>SyncManager: triggerSync()
    SyncManager->>VaultRepository: getLastSyncTimestamp()
    VaultRepository->>RoomDAO: getSyncTimestamp()
    RoomDAO-->>VaultRepository: timestamp
    VaultRepository-->>SyncManager: timestamp
    
    SyncManager->>Retrofit: POST /vault/sync {since: timestamp}
    Retrofit->>LaravelAPI: HTTPS Request
    LaravelAPI->>PostgreSQL: Query changes since timestamp
    PostgreSQL-->>LaravelAPI: Updated items
    LaravelAPI-->>Retrofit: {items: [...], serverTimestamp}
    Retrofit-->>SyncManager: SyncResponse
    
    SyncManager->>VaultRepository: mergeChanges(items)
    VaultRepository->>RoomDAO: upsertItems(items)
    RoomDAO-->>VaultRepository: Success
    
    SyncManager->>Retrofit: POST /vault/sync {items: [pending]}
    Retrofit->>LaravelAPI: Upload pending changes
    LaravelAPI->>PostgreSQL: Insert/Update items
    PostgreSQL-->>LaravelAPI: Success
    LaravelAPI-->>Retrofit: Success
    Retrofit-->>SyncManager: Upload complete
    
    SyncManager->>AndroidApp: SyncComplete
```

---

## 9. Use Case Diagram

```mermaid
flowchart TB
    subgraph Actors
        User["Unauthenticated User"]
        Owner["Vault Owner"]
        Contact["Emergency Contact"]
        Admin["Server Admin"]
    end
    
    subgraph UseCases
        Register["Register Account"]
        Login["Login"]
        Biometric["Biometric Unlock"]
        Passkey["Passkey Login"]
        ViewVault["View Vault"]
        CreateItem["Create Vault Item"]
        EditItem["Edit Vault Item"]
        DeleteItem["Delete Vault Item"]
        GeneratePassword["Generate Password"]
        ImportData["Import Data"]
        ExportData["Export Data"]
        CheckBreach["Check Breaches"]
        ShareItem["Share Item"]
        ViewAudit["View Audit Log"]
        ManageSessions["Manage Sessions"]
        RequestAccess["Request Emergency Access"]
        GrantAccess["Grant Emergency Access"]
    end
    
    User --> Register
    User --> Login
    User --> Passkey
    
    Owner --> Biometric
    Owner --> ViewVault
    Owner --> CreateItem
    Owner --> EditItem
    Owner --> DeleteItem
    Owner --> GeneratePassword
    Owner --> ImportData
    Owner --> ExportData
    Owner --> CheckBreach
    Owner --> ShareItem
    Owner --> ViewAudit
    Owner --> ManageSessions
    
    Contact --> RequestAccess
    Owner --> GrantAccess
```

---

## 10. Activity Diagram

```mermaid
activityDiagram
    start
    :Launch App;
    
    if First Launch? then
        -->[Yes] :Show Onboarding
        :Navigate to Server Setup
    else
        -->[No] :Check Session
    end
    
    if Valid Session? then
        -->[Yes] :Check Biometric
        if Biometric Available and Enabled? then
            -->[Yes] :Prompt Biometric
            if Success? then
                -->[Yes] :Open Vault Home
            else
                -->[No] :Show Login
            end
        else
            -->[No] :Show Login
        end
    else
        -->[No] :Show Login
    end
    
    :Login or Register;
    
    if Requires 2FA? then
        -->[Yes] :Verify 2FA
    else
        -->[No] :Open Vault
    end
    
    :Vault Home;
    
    if Offline Mode? then
        -->[Yes] :Use Local Only
    else
        -->[No] :Check Sync
    end
    
    if Sync Available? then
        -->[Yes] :Sync with Server
    else
        -->[No] :Continue Offline
    end
    
    stop
```

---

## 11. State Machine - Network Request

```mermaid
stateDiagram-v2
    [*] --> Idle
    Idle --> Loading : execute()
    Loading --> Success : data received
    Loading --> Error : network error
    Error --> Retrying : retry()
    Retrying --> Loading : attempt++
    Retrying --> Error : max retries
    Error --> Idle : dismiss
    Success --> Idle : complete
    
    state Loading {
        [*] --> Requesting
        Requesting --> Processing
    }
    
    state Error {
        [*] --> NetworkError
        NetworkError --> ServerError
        ServerError --> AuthError
        AuthError --> UnknownError
    }
```

---

## 12. Deployment Diagram

```mermaid
flowchart TB
    subgraph Clients
        Android["Android Device"]
        Browser["Web Browser"]
        Cron["External Cron / Uptime Ping"]
    end
    
    subgraph Cloud["Render"]
        RenderWeb["truvalt-api Web Service"]
        
        subgraph Docker["Docker Container"]
            Nginx["Nginx"]
            PHP["PHP-FPM"]
            Laravel["Laravel App"]
        end
        
        KV["Render Key Value"]
        DB["Render PostgreSQL"]
    end
    
    subgraph ExternalServices["External Services"]
        HIBP["HIBP API"]
        PlayStore["Google Play"]
        FDroid["F-Droid"]
    end
    
    Android -->|HTTPS| RenderWeb
    Browser -->|HTTPS| RenderWeb
    Cron -->|GET /api/keep-alive| RenderWeb
    
    Nginx --> PHP
    PHP --> Laravel
    Laravel --> KV
    Laravel --> DB
    
    Laravel --> HIBP
    Android --> PlayStore
    Android --> FDroid
```

---

## 13. Data Flow - Vault Item Save

```mermaid
sequenceDiagram
    participant User
    participant UI
    participant ViewModel
    participant UseCase
    participant CryptoManager
    participant Repository
    participant RoomDAO
    participant SyncManager
    participant Retrofit
    participant LaravelAPI
    participant PostgreSQL
    
    User->>UI: Tap Save
    UI->>ViewModel: saveItem(itemData)
    ViewModel->>UseCase: SaveVaultItem(itemData)
    
    UseCase->>CryptoManager: encrypt(itemData, masterKey)
    CryptoManager-->>UseCase: encryptedBlob
    
    UseCase->>Repository: save(item)
    Repository->>RoomDAO: upsert(item)
    RoomDAO-->>Repository: Success
    
    Repository->>SyncManager: queueForSync(item)
    
    SyncManager->>Retrofit: POST /vault/items
    Retrofit->>LaravelAPI: Upload encrypted item
    LaravelAPI->>PostgreSQL: Insert/Update
    PostgreSQL-->>LaravelAPI: Success
    LaravelAPI-->>Retrofit: 201 Created
    Retrofit-->>SyncManager: Success
    
    SyncManager->>Repository: markSynced(itemId)
    Repository->>RoomDAO: updateSyncStatus(itemId, SYNCED)
    
    UseCase-->>ViewModel: Success
    ViewModel-->>UI: Navigate to VaultHome
    UI-->>User: Item saved
```
