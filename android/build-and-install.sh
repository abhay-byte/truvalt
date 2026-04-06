#!/bin/bash
set -e

# NOTE: AAPT2 Issue on ARM64 Linux
# ================================
# AGP downloads x86 aapt2 by default, which fails on ARM64 Linux.
# Fix: Add to gradle.properties:
#   android.aapt2FromMavenOverride=/opt/android-sdk/build-tools/36.0.0/aapt2
# This forces use of the SDK's native ARM64 aapt2 binary.

cd "$(dirname "$0")"

echo "Building debug APK..."
./gradlew assembleDebug

# Check for connected devices
DEVICES=$(adb devices | grep -v "List of devices" | grep "device$" | wc -l)

if [ "$DEVICES" -gt 0 ]; then
    echo "Found $DEVICES connected device(s), installing..."
    ./gradlew installDebug
    echo "Done!"
else
    echo "No devices connected. APK built at: app/build/outputs/apk/debug/app-debug.apk"
fi
