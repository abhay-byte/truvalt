# Fastlane Setup Guide

## Prerequisites

- Ruby 3.2+
- Bundler
- Fastlane 2.220+

## Installation

```bash
cd /path/to/truvalt
bundle install
```

## Environment Variables

Create a `.env` file or set these environment variables:

| Variable | Description | Required For |
|---|---|---|
| `KEYSTORE_PATH` | Path to Java keystore | `build_release`, `deploy_*` |
| `KEYSTORE_PASSWORD` | Keystore password | `build_release`, `deploy_*` |
| `KEY_ALIAS` | Key alias name | `build_release`, `deploy_*` |
| `KEY_PASSWORD` | Key password | `build_release`, `deploy_*` |
| `PLAY_STORE_JSON_KEY` | Google Play service account JSON | `deploy_*` |
| `SLACK_WEBHOOK_URL` | Slack webhook for notifications | `send_slack_notification` |

## Available Lanes

| Lane | Command | Description |
|---|---|---|
| test | `bundle exec fastlane test` | Run all Android unit tests |
| build_debug | `bundle exec fastlane build_debug` | Assemble debug APK |
| build_release | `bundle exec fastlane build_release` | Assemble signed release AAB |
| deploy_internal | `bundle exec fastlane deploy_internal` | Upload to internal track |
| deploy_alpha | `bundle exec fastlane deploy_alpha` | Upload to alpha track |
| deploy_beta | `bundle exec fastlane deploy_beta` | Upload to beta track |
| deploy_production | `bundle exec fastlane deploy_production` | Upload to production (confirms first) |
| deploy_fdroid | `bundle exec fastlane deploy_fdroid` | Run lint + test + git tag |
| increment_version | `bundle exec fastlane increment_version` | Bump versionCode and create git tag |
| screenshot | `bundle exec fastlane screenshot` | Capture screenshots (placeholder) |
| send_slack_notification | `bundle exec fastlane send_slack_notification` | Post to Slack |

## Play Store Service Account Setup

1. Go to Google Play Console → Release → Setup → API Access
2. Create a new service account
3. Download JSON key file
4. Set `PLAY_STORE_JSON_KEY` environment variable to the JSON content
5. Grant appropriate roles (Release Manager, etc.)

## Signing Configuration

For release builds, configure signing in `android/app/build.gradle.kts`:

```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KEYSTORE_PATH"))
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

## F-Droid Process

F-Droid builds automatically from tagged commits:

1. Make sure all changes are committed
2. Run `bundle exec fastlane deploy_fdroid`
3. This runs lint + test, creates git tag `vX.Y.Z`
4. F-Droid picks up tagged commits automatically
5. Maintain `metadata/com.ivarna.truvalt.yml` in F-Droid data repository

## Adding Locales

1. Create new folder: `fastlane/metadata/android/{locale}/`
2. Add required files:
   - `title.txt`
   - `short_description.txt`
   - `full_description.txt`
   - `changelogs/{version}.txt`
3. Update locales in Fastfile if needed

## Adding New Changelog Version

1. Create new file: `fastlane/metadata/android/en-US/changelogs/{version}.txt`
2. Add changelog content
3. Update version in `android/app/build.gradle.kts`
4. Deploy with appropriate lane

## Troubleshooting

### Build fails with signing error
- Verify keystore exists at `KEYSTORE_PATH`
- Check `KEY_ALIAS` matches the key alias in keystore
- Ensure passwords are correct

### Play Store upload fails
- Verify service account has proper permissions
- Check JSON key format is correct
- Ensure app is published in the correct track

### Fastlane not found
- Run `bundle exec fastlane` instead of `fastlane`
- Verify Gemfile includes fastlane gem
