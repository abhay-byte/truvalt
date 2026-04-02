# Deployment Guide

## Architecture

Truvalt has **no backend server**. The Android app talks directly to Firebase:

- **Firebase Authentication** — account identity, sign-in, sign-up, Google OAuth
- **Cloud Firestore** — encrypted vault data, folders, tags, user profile
- **Firebase Hosting** — static account-deletion policy page (Google Play requirement)

**Firebase project:** `tru-valt`

---

## Android App

No deployment steps — ship the APK/AAB via Google Play or install directly via ADB.

### Build

```bash
cd android
./gradlew assembleDebug          # debug APK
./gradlew bundleRelease          # release AAB for Play Store
```

### Install on device

```bash
adb install android/app/build/outputs/apk/debug/app-debug.apk
```

---

## Static Account Deletion Website

A static site is hosted on Firebase Hosting to satisfy Google Play's account-deletion URL requirement.

**Location:** `delete-account-site/`

### Deploy

```bash
cd delete-account-site
firebase deploy --only hosting --project tru-valt
```

> If you get a permission error, run `firebase login` first with the Google account that owns the `tru-valt` Firebase project.

### Site URL

After deploy, the site will be live at:
```
https://tru-valt.web.app/
```
(or the custom domain if configured)

Use this URL in the Google Play Console → App content → Data safety → "Account deletion instructions URL".

---

## Firebase Security Rules

Firestore security rules must enforce that users can only read/write their own data. Example:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{uid}/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }
  }
}
```

Deploy rules:

```bash
firebase deploy --only firestore:rules --project tru-valt
```

---

## Security Notes

- No service-account keys or backend secrets to manage.
- Firebase project credentials (`google-services.json`) are bundled in the Android app — this is standard practice and does not expose sensitive secrets.
- Vault data is end-to-end encrypted client-side before reaching Firestore.
- Rotate Firestore security rules and review Firebase Auth settings periodically.
