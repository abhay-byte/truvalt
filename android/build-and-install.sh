#!/bin/bash
set -e

# NOTE: AAPT2 Issue on ARM64 Linux
# ================================
# AGP downloads x86 aapt2 by default, which fails on ARM64 Linux.
# Fix: Add to gradle.properties:
#   android.aapt2FromMavenOverride=/opt/android-sdk/build-tools/36.0.0/aapt2
# This forces use of the SDK's native ARM64 aapt2 binary.

# PERFORMANCE TUNING (gradle.properties)
# ======================================
# For maximum build speed on ARM64, ensure these settings:
#
#   org.gradle.daemon=true          # Keep JVM warm (~19s vs ~1.5min)
#   org.gradle.parallel=true        # Parallel task execution
#   org.gradle.workers.max=4        # Use multiple CPU cores
#   org.gradle.caching=true         # Enable build cache
#   android.buildcache.enabled=true # Android build cache
#
# With daemon enabled:
#   - First build: ~1.5min (cold JVM)
#   - Subsequent builds: ~19s (warm daemon)
#
# Without daemon (old settings):
#   - Every build: ~1.5-2min (fresh JVM each time)

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
