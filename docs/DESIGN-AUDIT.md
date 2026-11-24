# LUNMED TV — Design Review & Polish Audit

A design-quality review of the 10-foot UI (Home, App Library, Device Center, Settings)
measured against Android TV / tvOS interaction standards, plus the fixes applied as a
result of that review.

## 1. What the design system already does well

| Area | Implementation | Verdict |
|------|----------------|---------|
| Token layer | `LunmedColors`, `LunmedTypography`, `LunmedSpacing`, `LunmedShapes`, `LunmedElevation`, `LunmedFocusStyle`, `LunmedAnimationSpecs` exposed through `LunmedTheme` | Genuine, not decorative: every screen reads from it |
| Accent presets | Champagne Gold / Ice Cyan / Platinum Silver / Obsidian Amber | Real token switching (now including the focus rim) |
| Density presets | Comfortable / Compact (`overscan*`, `sectionGap`, `cardGap`) | Shipped and consumed |
| Motion | `FULL` / `REDUCED` / `MINIMAL`, spring-based focus scale (`damping 0.72`, `stiffness 380`), Apple-TV style specular sheen sweep, staggered spotlight transition | Above typical OEM launcher quality |
| Focus language | Spring lift + metallic rim + inner top-light + 22% inner artwork zoom + `zIndex` promotion | Cinematic, reads well at distance |
| Adaptive artwork | `AdaptiveAuraEngine` per-app aura feeding card glow, spotlight bloom and wallpaper canvas | Signature touch, rarely seen in third-party launchers |
| Focus memory | `rememberFocusMemoryRegistry` + overlay anchor restore + `lunmedFocusGroupRestorer` | No lost focus after dialogs / back navigation |

## 2. Defects found in this review

| # | Defect | Impact | Status |
|---|--------|--------|--------|
| D1 | The focus rim was hardcoded champagne gold (`0xFFFFF8E7 -> accentPrimary -> 0xFFC49A45`) in 5 components | Switching to Ice Cyan / Platinum / Amber still produced a **gold** focus ring — the single most visible brand inconsistency | **Fixed** — `LunmedGradients.focusRimStops()` derives the rim from the active accent only |
| D2 | `pressedScale` (0.98) token existed but was never wired | OK press gave no tactile confirmation before an app launched | **Fixed** — D-pad press now dips the card via `interactionSource` + `collectIsPressedAsState()` |
| D3 | Focus shadow was a raw pixel constant (`22f`) | Depth differed per screen density / panel | **Fixed** — animated from `LunmedTheme.elevation` (dp -> px) |
| D4 | `overscanVertical` token was defined but never used; top bar used `spacing.md` | Navigation dock could clip on TVs with heavy overscan | **Fixed** — top bar now pads `overscanVertical` (28 dp) |
| D5 | `labelMeta` = 11sp, `bodySecondary` = 13sp, plus overrides down to **9sp/10sp** (top bar "4K HDR", wallpaper "ACTIVE", spotlight badges/keycaps) | Illegible at 2.5-4 m — the classic 10-foot failure mode | **Fixed** — floor raised to 12sp, body/label tiers lifted one step, 9/10sp overrides promoted to 11-13sp |
| D6 | `textMuted` = `#6B768A` -> 4.58:1 on the canvas | Passed WCAG AA only barely; weak in a bright living room | **Fixed** — `#7E8A9E` -> 5.71:1 |
| D7 | Status toast rendered every message in warning amber, including hard launch failures | Semantics inverted (warning colour for errors) | **Fixed** — `statusError` |
| D8 | Glass hairlines were ad-hoc literals (`0x1FFFFFFF`, `0x18FFFFFF`, `0x33FFFFFF`) scattered across components | Drift risk, no single source of truth | **Fixed** — new `borderGlass` token |

## 3. Measured contrast (WCAG 2.1, sRGB)

| Text token | ObsidianCanvas `#07090E` | SurfaceCard `#171E2E` |
|------------|--------------------------|------------------------|
| `textPrimary` `#F3F5F8` | ~18.2 : 1 | ~15.2 : 1 |
| `textSecondary` `#A7B1C2` | ~9.2 : 1 | ~7.7 : 1 |
| `textMuted` `#7E8A9E` | ~5.7 : 1 | ~4.8 : 1 |

`LunmedDesignSystemTest` now asserts >= 4.5 : 1 for all three text tokens on both dark
surfaces and for all four accent presets, so a future palette tweak cannot silently
regress legibility.


## 4. Type scale (after)

| Tier | Size | Use |
|------|------|-----|
| `displayHero` | 36sp / 44lh | Spotlight hero app title, idle brand headline |
| `headlineSection` | 22sp | Rail titles, screen titles |
| `titleLarge` | 20sp | Clock, empty-state title |
| `titleCard` | 16sp | App labels under cards, wallpaper titles |
| `bodyPrimary` | 16sp | Primary body copy, capsule app names |
| `bodySecondary` | 14sp | Descriptions, keycap hints |
| `labelAction` | 14sp | Top-bar pills, primary actions |
| `labelMeta` | 12sp | Badges, metadata (floor: `MIN_LEGIBLE_SP = 12`) |

## 5. Focus / interaction contract

1. Unfocused card: `surfaceElevated` gradient, 1dp glass hairline, elevation `subtle`.
2. Focused: spring scale `1.10`, accent-derived 2.5dp rim, top-light hairline, 45 deg
   specular sweep (440 ms), elevation `focused`, `zIndex 50`.
3. Pressed: `pressedScale` (0.98) multiplied by the focused scale (~1.08), so OK reads as a
   tactile dip instead of a jarring shrink back to card size.
4. Long-press (450 ms) opens the app context menu; the KeyUp is swallowed so releasing
   OK never launches the app accidentally.

## 6. Remaining gaps (deliberately not changed here)

| Gap | Why it matters | Suggested next step |
|-----|----------------|---------------------|
| UI text is hardcoded English (no `stringResource`) while `values-ar` exists | Arabic / RTL users see English chrome; resellers cannot localise | Extract strings to `res/values/strings.xml` + `values-ar`, then test RTL with `LayoutDirection.Rtl` |
| No brand typeface (`FontFamily.SansSerif`) | A luxury launcher usually pairs a display face with the neutral UI face | Ship one `ttf` display face for the wordmark + hero only (keep the neutral face for body) |
| Rail cards are fixed dp (168-240dp wide) | On very large panels the grid can feel sparse | Consider `widthIn(max = ...)` + weight-based rails for 4K |
| No screenshot / golden tests | Visual regressions are caught only by eye | Add Paparazzi or Roborazzi goldens for Home rail + focus state |

## 7. Verification

```
.\gradlew.bat :core:core-design:testDebugUnitTest :app:assembleDebug --offline
.\gradlew.bat testDebugUnitTest --offline
```

* `LunmedDesignSystemTest`: 7 tests / 0 failures (4 existing + rim-coherence,
  typography-floor, WCAG-contrast).
* `:app:assembleDebug`: BUILD SUCCESSFUL.
