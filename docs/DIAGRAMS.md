# Diagrams

> Updated for the Firebase-direct architecture on 2026-04-28.
> There is **no intermediate backend server**. The Android app communicates directly with Firebase.

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
        Crypto["Crypto"]
    end

    subgraph Firebase["Firebase / Google Cloud"]
        Auth["Firebase Auth"]
        Store["Cloud Firestore"]
    end

    UI --> VM
    VM --> Repo
    Repo --> Room
    Repo --> Prefs
    Repo -->|Firebase Android SDK| Auth
    Repo -->|Firebase Android SDK| Store
    Crypto --> Repo
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
    participant Client as Android App
    participant FirebaseSDK as Firebase Auth SDK
    participant Firestore

    Client->>FirebaseSDK: createUserWithEmailAndPassword(email, password)
    FirebaseSDK-->>Client: FirebaseUser + idToken
    Client->>Firestore: upsert users/{uid} profile
    Firestore-->>Client: profile document
    Client->>Client: derive vault key from password + email (Argon2id)
    Client->>Client: encrypt vault key with Android Keystore
```

---

## 4. Firestore Security Rules

```mermaid
sequenceDiagram
    participant Client as Android App
    participant FirestoreRules as Firestore Security Rules
    participant FirestoreDB as Cloud Firestore

    Client->>FirestoreRules: read/write request with Firebase Auth token
    FirestoreRules->>FirestoreRules: verify request.auth != null && request.auth.uid == uid
    FirestoreRules->>FirestoreDB: allow operation if rule passes
    FirestoreDB-->>Client: data response
```

---

## 5. Sync Conflict Handling

```mermaid
sequenceDiagram
    participant Client as Android App
    participant Firestore

    Client->>Firestore: getVaultItems(uid, updatedAfterSeconds)
    Firestore-->>Client: remote items
    loop for each item
        alt server.updated_at > local.updated_at
            Client->>Client: overwrite local with remote
        else local is newer
            Client->>Firestore: setDocument(remote path, local item)
        end
    end
```

---

## 6. Logout / Revocation Flow

```mermaid
sequenceDiagram
    participant Client as Android App
    participant FirebaseSDK as Firebase Auth SDK

    Client->>FirebaseSDK: signOut()
    FirebaseSDK-->>Client: local session cleared
    Client->>Client: clear local vault key from memory
    Client->>Client: wipe DataStore auth tokens
```

---

## 7. Firebase Project Setup

```mermaid
flowchart LR
    Android["Android App"] -->|Firebase BOM| Auth["Firebase Auth"]
    Android -->|Firebase BOM| Firestore["Cloud Firestore"]
    Auth -->|ID Token| FirestoreRules["Firestore Security Rules"]
    FirestoreRules --> Firestore
```
