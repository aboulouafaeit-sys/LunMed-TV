# LUNMED TV — Commercial Android TV & TV Box Launcher

<div align="center">

**Cinematic Luxury • Ultra Minimal • TV Remote Control-First • 10-Foot Architecture**

[![Android Version](https://img.shields.io/badge/Android-7.0%2B%20(API%2024%2B)-2ea44f?logo=android&logoColor=white)](https://developer.android.com)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35%20(Android%2015)-blue?logo=android)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20Modular%20(10%20Modules)-orange)](https://developer.android.com)
[![R8 Minified APK](https://img.shields.io/badge/APK%20Size-1.9%20MB-brightgreen)](https://github.com/aboulouafaeit-sys/LunMed-TV)
[![Tests](https://img.shields.io/badge/Tests-46%2F46%20Passing-success)](https://github.com/aboulouafaeit-sys/LunMed-TV)

</div>

---

## Overview

**LUNMED TV** is a commercial-grade, luxury launcher engineered exclusively for Android TV, Google TV, and AOSP TV Boxes (Amlogic, Rockchip, Allwinner, FireOS). Designed around a **Cinematic Luxury & Ultra-Minimal** aesthetic, it delivers velvet obsidian surfaces, ambient lighting, fluid D-pad navigation, authentic zero-mock system telemetry, and ironclad defenses against OEM vendor launcher takeovers.

---

## Key Highlights

- **Cinematic Luxury Visual Design**: Deep obsidian canvas with dynamic radial lighting responsive to 5 tailored wallpaper profiles (`Obsidian Noir`, `Cosmic Amber`, `Cyber Azure`, `Nordic Platinum`, and `Obsidian Jade`).
- **Remote-First Focus Engine**: Zero touch dependencies. Comprehensive D-pad navigation with vertical focus memory restoration, boundary bounce prevention, and WCAG AAA compliant contrast ratios.
- **Persistent Default Launcher Defenses**: Overcomes aggressive OEM vendor locks on cheap TV boxes through a triple-layer mechanism:
  1. Official `RoleManager.ROLE_HOME` for certified Android TV / Google TV.
  2. High-performance `AccessibilityService` (`LunmedHomeWatchdogService`) that filters `KEYCODE_HOME` and intercepts vendor launchers within 50ms.
  3. `DirectBootAware` multi-event broadcast receiver (`LunmedBootReceiver`) catching cold boot, fast boot (QuickBoot), and reboots.
  4. Enterprise Device Owner provisioning (`LunmedDeviceAdminReceiver`) via ADB (`dpm set-device-owner`) with `addPersistentPreferredActivity` for permanent factory-level default status.
- **Genuine Device Diagnostics (Zero Mock Data)**: Real-time queries of physical RAM (`ActivityManager.MemoryInfo`), flash storage (`StatFs`), physical display modes (4K UHD detection, refresh rate, HDR10/Dolby Vision/HLG support), and network link performance (Wi-Fi dBm, link speed, local IPv4 address).
- **Internationalization & Full Arabic RTL**: Seamless bidirectional rendering with complete Arabic localization, native right-to-left layout mirroring, and in-app language switching (Arabic, English, French, Spanish, Italian).
- **Low-RAM Performance Architecture**: Automatically adapts on $\le$ 2 GB RAM TV boxes by scaling down LRU caches, capping bitmap resolutions, reducing animation intensity, and honoring `onTrimMemory` lifecycle events. Packaged into an ultra-lean 1.9 MB release binary.

---

## Architecture

LUNMED TV adheres strictly to Android modular architecture guidelines with complete separation of concerns across 10 Gradle modules:

```mermaid
graph TD
    APP[":app (Shell, Watchdog, Device Admin, Receivers)"]
    
    subgraph Feature Modules
        F_HOME[":feature:feature-home\n(Spotlight Stage, Rails, Clock)"]
        F_APPS[":feature:feature-apps\n(Library, Categories, Search)"]
        F_SETTINGS[":feature:feature-settings\n(Customization, Roles, ADB)"]
        F_DEVICE[":feature:feature-device\n(Diagnostics, Wallpapers)"]
    end
    
    subgraph Core Modules
        C_NAV[":core:core-navigation\n(D-pad Mapper, Focus Restorer)"]
        C_UI[":core:core-ui\n(TopBar, Artwork Cache, Ambient Canvas)"]
        C_DATA[":core:core-data\n(AppRepo, PreferencesRepo, DeviceRepo)"]
        C_DB[":core:core-database\n(Room App Database, Favorites)"]
        C_DS[":core:core-datastore\n(Proto DataStore Preferences)"]
        C_DESIGN[":core:core-design\n(Tokens, Palette, Shapes, Specs)"]
        C_MODEL[":core:core-model\n(Domain Models, Pure Kotlin)"]
        C_COMMON[":core:core-common\n(Hardware Capabilities, Coroutines)"]
    end

    APP --> F_HOME
    APP --> F_APPS
    APP --> F_SETTINGS
    APP --> F_DEVICE
    
    F_HOME --> C_UI & C_DATA & C_NAV & C_DESIGN
    F_APPS --> C_UI & C_DATA & C_NAV & C_DESIGN
    F_SETTINGS --> C_UI & C_DATA & C_NAV & C_DESIGN
    F_DEVICE --> C_UI & C_DATA & C_NAV & C_DESIGN
    
    C_DATA --> C_DB & C_DS & C_MODEL & C_COMMON
    C_UI --> C_MODEL & C_DESIGN & C_NAV
```

---

## Build & Installation

### Requirements
- **JDK 17+** (JBR 21/25 supported)
- **Android SDK**: Compile SDK 35, Min SDK 24 (Android 7.0+)
- **Gradle**: 9.3.1 + AGP 9.1.0

### Compiling Debug APK
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Compiling Minified Release APK
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk` (Size: ~1.9 MB)

### Executing Full Unit Test Suite
```bash
./gradlew testDebugUnitTest :core:core-model:test
```

---

## Permanent Default Launcher Setup (Reseller / ADB Guide)

### 1. Standard Role Request (Android TV / Google TV)
Run in ADB or click **Set Default** from the Top Bar:
```bash
adb shell cmd role add-role-holder android.app.role.HOME com.lunmed.tv
```

### 2. Enable Accessibility Watchdog (Fire TV / Amlogic / Rockchip / Allwinner)
Grants the background watchdog permission to intercept `KEYCODE_HOME` away from vendor launchers:
```bash
adb shell settings put secure enabled_accessibility_services com.lunmed.tv/.service.LunmedHomeWatchdogService
adb shell settings put secure accessibility_enabled 1
```

### 3. Factory-Level Device Owner Lock (Enterprise STBs)
Binds LUNMED TV as the permanent OS default launcher across all boots and factory resets:
```bash
adb shell dpm set-device-owner com.lunmed.tv/.receiver.LunmedDeviceAdminReceiver
```

For complete technical steps and troubleshooting, see the [Reseller Provisioning Guide](docs/RESELLER-PROVISIONING-GUIDE.md).

---

## License

Copyright © 2026 LUNMED TV Systems. All rights reserved.
Commercial proprietary software engineered for premium television hardware deployments.

