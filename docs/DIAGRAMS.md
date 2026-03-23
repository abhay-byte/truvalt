# Diagrams

> Updated for the Firebase Auth + Firestore backend pivot on 2026-03-23.

---

## 1. Architecture Overview

```mermaid
flowchart TB
    subgraph Android["Android App"]
        UI["Compose UI"]
        VM["ViewModels"]
        Repo["Repositories"]
        Room["Room DB"]
        Prefs["DataStore / Prefs"]
        Remote["Retrofit API"]
        Crypto["Crypto"]
    end

    subgraph Web["Laravel Backend"]
        API["REST API"]
        Middleware["Firebase Auth Middleware"]
        Services["Firebase Services"]
        FirestoreRepo["Firestore Repository"]
    end

    subgraph Firebase["Firebase / Google Cloud"]
        Auth["Firebase Auth"]
        Store["Cloud Firestore"]
    end

    UI --> VM
    VM --> Repo
    Repo --> Room
    Repo --> Prefs
    Repo --> Remote
    Crypto --> Repo

    Remote -->|HTTPS/REST| API
    API --> Middleware
    Middleware --> Services
    Services --> Auth
    Services --> FirestoreRepo
    FirestoreRepo --> Store
```

---

## 2. Firestore Data Model

```mermaid
flowchart TD
    Users["users/{uid}"]
    Profile["profile fields:
    id
    email
    providers[]
    auth_key_hash?
    email_verified
    created_at
    updated_at
    last_login_at"]
    VaultItems["users/{uid}/vault_items/{itemId}"]
    Folders["users/{uid}/folders/{folderId}"]
    Tags["users/{uid}/tags/{tagId}"]

    Users --> Profile
    Users --> VaultItems
    Users --> Folders
    Users --> Tags
```

---

## 3. Email/Password Auth Flow

```mermaid
sequenceDiagram
    participant Client
    participant Laravel
    participant IdentityToolkit as Firebase Auth REST
    participant Admin as Firebase Admin SDK
    participant Firestore

    Client->>Laravel: POST /login {email, password, auth_key_hash?}
    Laravel->>IdentityToolkit: accounts:signInWithPassword
    IdentityToolkit-->>Laravel: idToken, refreshToken, localId
    Laravel->>Admin: getUser(localId)
    Admin-->>Laravel: Firebase user record
    Laravel->>Firestore: upsert users/{uid}
    Firestore-->>Laravel: profile document
    Laravel-->>Client: user + token + refresh_token + expires_in
```

---

## 4. Protected Route Verification

```mermaid
sequenceDiagram
    participant Client
    participant Middleware as AuthenticateWithFirebase
    participant AuthService as FirebaseAuthService
    participant Admin as Firebase Admin SDK
    participant Controller
    participant Firestore

    Client->>Middleware: Authorization: Bearer <firebase_id_token>
    Middleware->>AuthService: authenticate(idToken)
    AuthService->>Admin: verifyIdToken(idToken, checkRevoked=true)
    Admin-->>AuthService: verified token claims
    AuthService->>Admin: getUser(uid)
    Admin-->>AuthService: user record
    AuthService-->>Middleware: authenticated user
    Middleware->>Controller: request with resolved user
    Controller->>Firestore: read/write user-scoped documents
```

---

## 5. Sync Conflict Handling

```mermaid
sequenceDiagram
    participant Client
    participant Laravel
    participant Firestore

    Client->>Laravel: POST /vault/sync {items[]}
    loop for each item
        Laravel->>Firestore: load users/{uid}/vault_items/{id}
        Firestore-->>Laravel: existing item or null
        alt existing.updated_at > incoming.updated_at
            Laravel-->>Client: add existing item to conflicts[]
        else incoming is newest
            Laravel->>Firestore: upsert incoming item
            Laravel-->>Client: add saved item to synced[]
        end
    end
```

---

## 6. Logout / Revocation Flow

```mermaid
sequenceDiagram
    participant Client
    participant Laravel
    participant Admin as Firebase Admin SDK

    Client->>Laravel: POST /logout with bearer token
    Laravel->>Admin: revokeRefreshTokens(uid)
    Admin-->>Laravel: success
    Laravel-->>Client: revocation confirmation message
```

---

## 7. Render Runtime Flow

```mermaid
flowchart LR
    Blueprint["render.yaml"] --> Docker["web/Dockerfile"]
    Docker --> Build["render-build.sh"]
    Docker --> Run["render-run.sh"]
    Run --> Supervisor["Supervisor + Nginx + PHP-FPM"]
    Supervisor --> Laravel["Laravel API"]
    Laravel --> Firebase["Firebase Auth + Firestore"]
```
