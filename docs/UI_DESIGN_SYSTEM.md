# UI Design System

---

## 1. Design Philosophy

1. **Zero-Knowledge Clarity** — The UI never shows that security is happening in the background; it should feel effortless, not paranoid.

2. **Minimum Exposure** — Sensitive data is hidden by default. Every reveal is intentional and timed.

3. **Precision over Decoration** — Every element has a reason. No decorative UI that competes with the vault content.

4. **Platform Native** — Android follows Material You. Web follows standard browser conventions. No forced cross-platform sameness.

5. **Accessible by Default** — Touch targets ≥ 48dp, contrast ≥ 4.5:1, all sensitive field reveals announced via accessibility semantics.

---

## 2. Material 3 Color System

### 2.1 Seed Color

- **Seed:** `#0D7377` (teal-cyan — evokes security, trust, and clarity)
- **Dynamic color:** enabled for Android 12+ (Monet)
- **Static fallback:** for Android < 12 and web

### 2.2 Color Roles

| Role | Light Hex | Dark Hex |
|---|---|---|
| Primary | #0D7377 | #4CD9E4 |
| On Primary | #FFFFFF | #003739 |
| Primary Container | #9EF1ED | #004F50 |
| On Primary Container | #002021 | #9EF1ED |
| Secondary | #4A6364 | #B0CCCD |
| On Secondary | #FFFFFF | #1C3536 |
| Secondary Container | #CCE8E9 | #324B4C |
| On Secondary Container | #062021 | #B0CCCD |
| Tertiary | #4B607C | #B4C8EA |
| On Tertiary | #FFFFFF | #1C314B |
| Tertiary Container | #D3E4FF | #334863 |
| On Tertiary Container | #041C35 | #D3E4FF |
| Error | #BA1A1A | #FFB4AB |
| On Error | #FFFFFF | #690005 |
| Error Container | #FFDAD6 | #93000A |
| On Error Container | #410002 | #FFB4AB |
| Background | #FAFDFC | #191C1C |
| On Background | #191C1C | #E1E3E3 |
| Surface | #FAFDFC | #191C1C |
| On Surface | #191C1C | #E1E3E3 |
| Surface Variant | #DAE5E4 | #3F4948 |
| On Surface Variant | #3F4948 | #BEC9C8 |
| Outline | #6F7978 | #899392 |
| Outline Variant | #BEC9C8 | #3F4948 |
| Inverse Surface | #2D3130 | #EFF1F0 |
| Inverse On Surface | #EFF1F0 | #2D3130 |
| Inverse Primary | #84D5D7 | #006B6D |
| Surface Tint | #0D7377 | #4CD9E4 |
| Scrim | #000000 | #000000 |

### 2.3 AMOLED Dark Palette

| Role | Hex |
|---|---|
| Background | #000000 |
| Surface | #0A0A0A |
| Surface Variant | #1A1A1A |
| On Background | #E1E3E3 |
| On Surface | #E1E3E3 |

---

## 3. Typography

### 3.1 Type Scale (Material 3)

| Style | Font | Size | Weight | Line Height | Letter Spacing |
|---|---|---|---|---|---|
| Display Large | System | 57sp | 400 | 64sp | -0.25 |
| Display Medium | System | 45sp | 400 | 52sp | 0 |
| Display Small | System | 36sp | 400 | 44sp | 0 |
| Headline Large | System | 32sp | 400 | 40sp | 0 |
| Headline Medium | System | 28sp | 400 | 36sp | 0 |
| Headline Small | System | 24sp | 400 | 32sp | 0 |
| Title Large | System | 22sp | 400 | 28sp | 0 |
| Title Medium | System | 16sp | 500 | 24sp | 0.15 |
| Title Small | System | 14sp | 500 | 20sp | 0.1 |
| Body Large | System | 16sp | 400 | 24sp | 0.5 |
| Body Medium | System | 14sp | 400 | 20sp | 0.25 |
| Body Small | System | 12sp | 400 | 16sp | 0.4 |
| Label Large | System | 14sp | 500 | 20sp | 0.1 |
| Label Medium | System | 12sp | 500 | 16sp | 0.5 |
| Label Small | System | 11sp | 500 | 16sp | 0.5 |

### 3.2 Kotlin Theme Example

```kotlin
val Typography = Typography(
    displayLarge = TextStyle(...),
    displayMedium = TextStyle(...),
    displaySmall = TextStyle(...),
    headlineLarge = TextStyle(...),
    headlineMedium = TextStyle(...),
    headlineSmall = TextStyle(...),
    titleLarge = TextStyle(...),
    titleMedium = TextStyle(...),
    titleSmall = TextStyle(...),
    bodyLarge = TextStyle(...),
    bodyMedium = TextStyle(...),
    bodySmall = TextStyle(...),
    labelLarge = TextStyle(...),
    labelMedium = TextStyle(...),
    labelSmall = TextStyle(...),
)
```

---

## 4. Spacing Scale

| Token | Value |
|---|---|
| spacing-xs | 4dp |
| spacing-sm | 8dp |
| spacing-md | 16dp |
| spacing-lg | 24dp |
| spacing-xl | 32dp |
| spacing-xxl | 48dp |

Base unit: 4dp

---

## 5. Shape System

| Token | Value | Usage |
|---|---|---|
| none | 0dp | |
| extraSmall | 4dp | Chips, small buttons |
| small | 8dp | Text fields, cards |
| medium | 12dp | FAB, dialogs |
| large | 16dp | Bottom sheets |
| extraLarge | 28dp | |
| full | 50% | |

---

## 6. Elevation

| Level | Surface | Elevation |
|---|---|---|
| 0 | Background | 0dp |
| 1 | Surface | 1dp |
| 2 | Surface Elevated | 3dp |
| 3 | FAB | 6dp |
| 4 | Modal | 24dp |
| 5 | Navigation Drawer | 24dp |

---

## 7. Iconography

- **Family:** Material Symbols Rounded
- **Min tap target:** 48dp
- **Sizes:** 20dp (small), 24dp (medium), 32dp (large)
- **Color:** On Surface (inherit from context)

---

## 8. Motion

| Animation | Duration | Easing |
|---|---|---|
| Shared axis (nav) | 300ms | Standard (easeInOut) |
| Fade through (tabs) | 200ms | FastOutSlowIn |
| Quick actions | 200ms | Standard |
| Destructive actions | 300ms | Standard |

---

## 9. Accessibility Requirements

| Requirement | Target |
|---|---|
| Touch targets | ≥ 48dp |
| Contrast ratio | ≥ 4.5:1 (normal text), ≥ 3:1 (large text) |
| Focus indicators | Visible focus ring |
| Screen reader | All elements have content descriptions |
| Motion reduction | Respect `prefers-reduced-motion` |
| Text scaling | Support up to 200% |

---

## 10. Component States

| Component | Default | Pressed | Disabled | Focused |
|---|---|---|---|---|
| Button | Primary color | 12% black overlay | 38% opacity | Focus ring |
| Text Field | Outline | Filled primary | Grayed out | Focus ring |
| Card | Surface + elevation | Slight scale | N/A | Focus ring |
| Chip | Outlined | Filled | Grayed out | Focus ring |
