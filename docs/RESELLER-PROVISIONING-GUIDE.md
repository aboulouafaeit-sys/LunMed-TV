# LUNMED TV — Reseller & Technician STB Provisioning Guide

This guide provides step-by-step technical instructions for technicians, distributors, and TV box resellers to install and permanently lock **LUNMED TV** as the default home launcher on customer devices, completely eliminating vendor stock launchers (Amlogic, Rockchip, Allwinner, FireOS, Xiaomi PatchWall).

---

## 1. Prerequisites

1. Connect the TV Box to your computer via USB OTG cable or Wi-Fi ADB.
2. Enable **Developer Options** and **USB Debugging** on the TV box (Settings → About → Click "Build Number" 7 times).
3. Connect via ADB:
   ```bash
   adb connect <TV_BOX_IP_ADDRESS>:5555
   adb devices
   ```

---

## 2. Installation

Install the production-optimized, minified LUNMED TV release APK:
```bash
adb install -r -g app-release.apk
```
*(The `-g` flag automatically grants runtime permissions).*

---

## 3. Persistent Launcher Configuration Methods

### Method 1: Official Role Manager (Certified Google TV & Android TV)
Applies official Home role status:
```bash
adb shell cmd role add-role-holder android.app.role.HOME com.lunmed.tv
```

### Method 2: Accessibility Watchdog (Amlogic, Rockchip, Allwinner, FireOS)
Enables LUNMED TV's built-in `LunmedHomeWatchdogService` which intercepts `KEYCODE_HOME` presses and blocks vendor launcher takeovers:
```bash
# Grant and enable accessibility service
adb shell settings put secure enabled_accessibility_services com.lunmed.tv/.service.LunmedHomeWatchdogService
adb shell settings put secure accessibility_enabled 1
```

### Method 3: Factory-Level Device Owner Lock (Enterprise & Permanent Solution)
*The most powerful method.* Binds LUNMED TV directly to Android's kernel and ActivityManager as the permanent default launcher:
```bash
adb shell dpm set-device-owner com.lunmed.tv/.receiver.LunmedDeviceAdminReceiver
```
> [!NOTE]
> Device Owner must be set before user accounts (e.g. Google accounts) are added to the device, or on a freshly formatted/reset box. Once set, LUNMED TV cannot be overridden by vendor firmware.

### Method 4: Disabling Stock OEM Bloatware Launchers
On budget Chinese boxes that stubbornly try to restart their proprietary home apps, disable the vendor package:

- **Amlogic Stock Launcher**:
  ```bash
  adb shell pm disable-user --user 0 com.droidlogic.tvlauncher
  adb shell pm disable-user --user 0 com.droidlogic.launcher
  ```

- **Rockchip Stock Launcher**:
  ```bash
  adb shell pm disable-user --user 0 com.rockchip.launcher
  ```

- **Allwinner Stock Launcher**:
  ```bash
  adb shell pm disable-user --user 0 com.allwinner.launcher
  ```

- **Xiaomi Mi TV / PatchWall**:
  ```bash
  adb shell pm disable-user --user 0 com.mitv.tvhome
  ```

- **Amazon Fire TV**:
  ```bash
  adb shell pm disable-user --user 0 com.amazon.tv.launcher
  ```

---

## 4. Verification & Testing

Verify that Android resolves LUNMED TV as the single active Home activity:
```bash
adb shell cmd package resolve-activity -c android.intent.category.HOME
```
*Expected Output:*
```text
priority=1 preferredOrder=0 match=0x108000 specificIndex=-1 isDefault=true
com.lunmed.tv/.MainActivity
```

Reboot the TV box to verify cold-boot behavior:
```bash
adb reboot
```
Upon startup, the TV Box will boot directly into **LUNMED TV** with zero vendor dialogs or home picker prompts.
