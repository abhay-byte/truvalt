#!/bin/bash
# Round 2 Fixes - Build and Deploy Script

set -e

echo "=== Building Truvalt APK ==="
cd /home/flux/repos/Truvalt/android
./gradlew assembleDebug

echo "=== Copying APK to Downloads ==="
TIMESTAMP=$(date +%Y%m%d-%H%M)
cp app/build/outputs/apk/debug/app-debug.apk /sdcard/Download/Truvalt-${TIMESTAMP}.apk
ls -lh /sdcard/Download/Truvalt-*.apk | tail -1

echo "=== Committing Changes ==="
cd /home/flux/repos/Truvalt
git add -A
git commit -m "feat: Complete Round 2 critical fixes (FIX-003)

- TASK-R2-01: MainActivity→AppCompatActivity for BiometricPrompt compatibility
- TASK-R2-02: BiometricPromptManager with auto-trigger in LaunchedEffect
- TASK-R2-03: PinDotsRow with dynamic dots and shake animation
- TASK-R2-04: Removed back arrows from Generator/Health/Settings tab screens
- TASK-R2-05: Fixed content clipping with proper innerPadding/WindowInsets
- TASK-R2-06: Security section verified in SettingsScreen
- TASK-R2-07: Moved PinSetup to outer NavHost, isolated auth screens
- TASK-R2-08: SeedDataInserter with first-launch tracking in VaultViewModel

Files created: BiometricPromptManager.kt, PinDotsRow.kt, SeedDataInserter.kt
Files modified: 15 (MainActivity, themes, auth screens, tab screens, navigation, preferences)

All 8 tasks from fix.md specification completed."

echo "=== Pushing to Remote ==="
git push

echo "=== COMPLETE ==="
echo "APK: /sdcard/Download/Truvalt-${TIMESTAMP}.apk"
