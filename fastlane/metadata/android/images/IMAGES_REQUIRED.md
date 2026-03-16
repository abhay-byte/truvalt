# Android App Store Images

## Required Images

| Image Type | Size | Quantity | Purpose |
|---|---|---|---|
| App Icon | 512x512 PNG | 1 | Play Store listing |
| Feature Graphic | 1024x500 PNG | 1 | Play Store banner |
| Phone Screenshots | 1080x1920 PNG | 2-8 | App preview |
| 7-inch Tablet Screenshot | 1200x1920 PNG | 0-8 | Tablet preview |
| 10-inch Tablet Screenshot | 1920x1200 PNG | 0-8 | Large tablet preview |
| TV Banner | 1280x720 PNG | 0-1 | Android TV |

## Screengrab Setup

To capture screenshots automatically:

1. Add to `Gemfile`:
   ```ruby
   gem 'screengrab'
   ```

2. Create `Screengrabfile`:
   ```ruby
   package_name("com.ivarna.truvalt")
   app_apk_path("android/app/build/outputs/apk/debug/app-debug.apk")
   locales(["en-US"])
   ```

3. Run:
   ```bash
   fastlane screenshot
   ```

Screenshots are saved to `fastlane/metadata/android/en-US/images/`.

## Notes

- All images must be PNG format
- Feature graphic must be 1024x500 pixels
- Screenshots should show the app in action, not mockups
- Include variety: vault list, item detail, generator, settings
- Remove status bar timestamps in production screenshots
