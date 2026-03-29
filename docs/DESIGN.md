# Design System Strategy: The Fortified Sanctuary

## 1. Overview & Creative North Star
This design system moves beyond the utility of a standard password manager to create **"The Fortified Sanctuary."** In an era of digital chaos, Truvalt must feel like a high-end, architectural vault—silent, immovable, and impeccably organized. 

The "Creative North Star" is **Editorial Security.** We reject the "data-grid" aesthetic of traditional security tools in favor of an editorial layout characterized by aggressive white space, sophisticated tonal layering, and intentional asymmetry. We communicate "Secure" not through heavy borders and padlocks, but through the precision of our typography and the depth of our surfaces.

---

## 2. Colors & Tonal Depth
We utilize the Material Design 3 palette to create a "monochromatic-plus" environment. The primary purple (#5850bd) is used as a surgical strike—only for critical actions—while the rest of the UI breathes through tonal variations of the surface.

### The "No-Line" Rule
**Explicit Instruction:** 1px solid borders are strictly prohibited for sectioning. Structural boundaries must be defined solely through background color shifts.
*   *Implementation:* Use `surface-container-low` for secondary content areas sitting on a `surface` background. The change in luminance is the divider.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical, stacked layers of fine paper. 
*   **Base:** `surface` (#fcf8fe)
*   **Level 1 (Sections):** `surface-container-low` (#f6f2fa)
*   **Level 2 (Interactive Cards):** `surface-container-lowest` (#ffffff)
*   **Level 3 (Popovers/Overlays):** `surface-bright` (#fcf8fe)

### The "Glass & Gradient" Rule
To elevate the "Premium" feel, floating elements (like Bottom Sheets or Navigation Bars) should use a backdrop-blur effect.
*   **Token:** `surface-container-highest` at 80% opacity with a 20px blur.
*   **Signature Texture:** Use a subtle linear gradient from `primary` (#5850bd) to `primary-container` (#958dff) for the main "Add Entry" FAB or CTA to give it a "jewel-like" presence in a matte world.

---

## 3. Typography: Editorial Precision
We use **Manrope** as our typographic backbone. It is a modern, geometric sans-serif that balances the technicality of a secure app with the approachability of a premium brand.

*   **Display (Large/Medium):** Used for "Vault Value" totals or onboarding headers. These should be set with tight letter-spacing (-0.02em) to feel authoritative.
*   **Headline (Small):** Used for category titles (e.g., "Financial," "Social").
*   **Title (Large/Medium):** Use `title-lg` for individual account names (e.g., "GitHub," "Chase Bank").
*   **Body (Large/Medium):** Reserved for secondary data like usernames or timestamps.
*   **Label (Medium/Small):** Used for micro-data (e.g., "Password Strength: High").

**The Hierarchy Rule:** Never use two different font weights of the same size next to each other. Contrast should be achieved by jumping at least two levels in the scale (e.g., a `title-lg` header paired with a `body-sm` description).

---

## 4. Elevation & Depth
In this system, depth is organic, not artificial.

*   **The Layering Principle:** Avoid shadows for static elements. A `surface-container-lowest` card placed on a `surface-container-low` background creates a natural, soft lift.
*   **Ambient Shadows:** For floating modals, use a "Tinted Ambient" shadow. 
    *   *Spec:* `0px 12px 32px rgba(51, 49, 58, 0.06)`. The shadow color is derived from `on-surface` to make it feel like the object is blocking real light.
*   **The "Ghost Border" Fallback:** If a container requires more definition (e.g., a search bar), use the `outline-variant` token at **15% opacity**. It should be felt, not seen.

---

## 5. Components

### Buttons
*   **Primary (Filled):** Use `primary` background with `on-primary` text. Use `xl` (0.75rem) roundedness. No shadows; the color density provides the affordance.
*   **Text Buttons:** Use `primary` text. Ensure the hit target is a minimum of 48dp, even if the text is small.
*   **Page Indicators:** Active state uses `primary` with a width of 24dp (pill shape); inactive states use `outline-variant` as 8dp circles.

### Input Fields
*   **Visual Style:** Forgo the "filled box" or "underlined" look. Use a `surface-container-highest` background with a `sm` (0.125rem) corner radius.
*   **States:** On focus, the background remains, but a 2px `primary` "Ghost Border" appears at 40% opacity.

### Cards & Lists
*   **Forbid Dividers:** Do not use line separators between passwords in the vault. 
*   **Separation:** Use `spacing-4` (1rem) of vertical white space or alternate the background color of the list item subtly using `surface-container-low`.

### Specialized Components
*   **Strength Meter:** A custom linear progress bar using `tertiary` for "Weak" and `primary` for "Strong," housed in a `surface-container-highest` track.
*   **Biometric Trigger:** A large, central `surface-container-lowest` circle with a subtle `primary` glow (8% opacity) to invite interaction.

---

## 6. Do’s and Don’ts

### Do
*   **DO** use asymmetry. In a list of passwords, right-align the "Time Since Last Change" using `label-sm` to create a sophisticated editorial rhythm.
*   **DO** use "Breathing Room." If you think a container needs more padding, add `spacing-2` (0.5rem).
*   **DO** use `surface-dim` for transitions between major app sections to signal a change in context.

### Don’t
*   **DON'T** use pure black (#000000) for text. Always use `on-surface` (#33313a) to maintain the "premium paper" feel.
*   **DON'T** use standard Material shadows. They are too "heavy" for a sanctuary; stick to tonal layering.
*   **DON'T** crowd the UI. If a screen feels busy, move secondary information (like URL or "Created Date") into a progressive disclosure (tap to expand).

### Accessibility Note
While we prioritize a "No-Line" aesthetic, ensure the contrast between `surface` and `surface-container` tiers meets WCAG AA standards (minimum 3:1 for large graphical elements) to ensure the UI remains navigable for all users.