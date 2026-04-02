# API Reference — ARCHIVED

> **This document is archived.** The Laravel REST backend has been removed.
>
> Truvalt now communicates **directly with Firebase** — there is no intermediate backend server or REST API.

---

## Current Data Access

All data operations are performed via the **Firebase Android SDK**:

| Operation | Firebase Service | Android Code |
|---|---|---|
| Sign up / Sign in | Firebase Authentication | `AuthRepositoryImpl` |
| Google sign-in | Firebase Auth + Credential Manager | `AuthRepositoryImpl.signInWithGoogle()` |
| Sign out | Firebase Auth | `AuthRepositoryImpl.logout()` |
| Delete account | Firestore SDK + Firebase Auth | `AuthRepositoryImpl.deleteAccount()` |
| Vault items CRUD | Cloud Firestore | `FirestoreVaultRepository` |
| Folders CRUD | Cloud Firestore | `FirestoreVaultRepository` |
| Tags CRUD | Cloud Firestore | `FirestoreVaultRepository` |
| Sync | Cloud Firestore | `SyncRepositoryImpl` |

## Firestore Layout

```
users/{uid}                        ← user profile
users/{uid}/vault_items/{itemId}   ← encrypted vault items
users/{uid}/folders/{folderId}     ← folders
users/{uid}/tags/{tagId}           ← tags
```

See [SDD.md](./SDD.md) for full schema details.
