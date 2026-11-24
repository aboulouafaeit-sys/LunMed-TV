# HOME Persistence & Default-Launcher Hardening

This document describes how LUNMED TV claims and keeps the Android HOME role across
reboots, remote Home presses and vendor firmware takeovers, which parts are real
OS-level enforcement and which are best-effort workarounds.

## 1. Persistence layers (strongest → weakest)

| # | Layer | Mechanism | Survives reboot | Survives ROM OTA | Needs user action | Notes |
|---|-------|-----------|-----------------|------------------|-------------------|-------|
| 1 | **Device Owner pin** | `DevicePolicyManager.addPersistentPreferredActivity(admin, HOME filter, MainActivity)` | Yes | Yes | One-time ADB / root provisioning | Kernel/AMS-level, cannot be overridden by the user or by setting a different launcher. |
| 2 | **Root enforcement** | `su -c "cmd package set-home-activity …"`, `dpm set-device-owner …`, `pm disable-user` of known OEM launchers | Yes | Usually (survives until data wipe) | One-tap if `su` present | Destructive on vendor packages; `revertRootEnforcement()` re-enables them. |
| 3 | **RoleManager role** | `RoleManager.createRequestRoleIntent(ROLE_HOME)` (API 29+) → system dialog | Yes (role is stored by the OS) | Yes, unless the ROM resets defaults | One dialog confirmation | The only *officially supported* way for a normal (non-privileged) app. |
| 4 | **HOME intent-filter default** | `ACTION_MAIN` + `CATEGORY_HOME` + `DEFAULT` on `MainActivity`, user picks "Always" | Yes | Vendor ROMs may reset to stock | One chooser interaction | Works on every Android version; `android:priority` is ignored by HOME resolution but harmless. |
| 5 | **Accessibility watchdog** | `AccessibilityService.onKeyEvent(KEYCODE_HOME)` + `TYPE_WINDOW_STATE_CHANGED` on foreign launchers | Yes (service re-binds at boot) | Yes | Enable in Settings → Accessibility | Catch-all for ROMs that ignore the role API or hard-wire a vendor launcher. Requires key-event filtering. |
| 6 | **Boot receiver** | `BOOT_COMPLETED` / `QUICKBOOT_POWERON` → start `MainActivity` | n/a | n/a | none | Best effort only: on Android 10+ background activity starts from a receiver are blocked, so it is a no-op when the ROM starts the default HOME itself (which it already does). |

`DefaultLauncherHelper.getState()` reports layers 1–5 so the Settings UI can show the
real status of each one.

## 2. What is enforced vs. best effort

**Really enforced by the OS**

* `addPersistentPreferredActivity` when provisioned as Device Owner. Re-asserted in
  `LunmedDeviceAdminReceiver.onEnabled`, in `LunmedBootReceiver` on every boot and in
  `MainActivity.onCreate` (cold start), so an OTA or re-install cannot silently drop it.
* `clearPackagePersistentPreferredActivities` in `onDisabled` — removes the pin before the
  admin is removed, preventing a "Home does nothing" soft-brick on some ROMs.
* RoleManager role, once granted: the platform stores it in
  `PackageManager` preferred activities and re-uses it after reboot without any code running.

**Best effort (documented, not guaranteed)**

* `MainActivity` started from `LunmedBootReceiver`. Blocked by the Android 10+
  background-activity-start rules on many devices; wrapped in `runCatching` so it can never
  crash the boot sequence. The receiver still performs the useful work: re-asserting the
  device-owner pin.
* `startActivity()` from the accessibility watchdog on `KEYCODE_HOME`. Foreground
  accessibility *can* start activities on stock Android, but some vendor ROMs log it as a
  background start; when it fails the OS default-Home behaviour still applies.
* Root path: depends on the `su` implementation and on the vendor not re-installing its
  launcher on the next OTA.

**Removed as non-functional**

* `android.intent.action.REBOOT` in the boot intent-filter. It is a signature-protected
  broadcast that is never delivered to third-party apps, so it was dead weight.

## 3. Device Owner provisioning

Device Admin alone cannot pin HOME — only the **Device Owner** may call
`addPersistentPreferredActivity`. Neither can be granted from the UI: Android requires an
empty user (or a factory reset), an ADB/root shell and no existing accounts.

1. Factory-reset the box (or use a fresh, account-less profile).
2. Install the APK.
3. On a PC:

   ```bash
   adb shell dpm set-device-owner com.lunmed.tv/com.lunmed.tv.receiver.LunmedDeviceAdminReceiver
   ```

   On-device with root:

   ```bash
   su -c "dpm set-device-owner com.lunmed.tv/com.lunmed.tv.receiver.LunmedDeviceAdminReceiver"
   ```

4. Re-open the app — `MainActivity.onCreate` immediately pins HOME to LUNMED TV.
5. Verify: `adb shell dumpsys package preferred-activities | grep -i lunmed` and
   Settings → Boot Persistence Layers → *Device Owner: active*.

The declared admin policies are intentionally empty (`res/xml/device_admin_receiver.xml`):
HOME pinning needs **no** policy at all. Earlier revisions requested `wipe-data`,
`reset-password`, `force-lock`, … which is a needless privacy/security risk for a TV
launcher and blocks Play Store distribution.

## 4. Reboot risk matrix

| Situation | Result |
|-----------|--------|
| Device Owner + power cycle | LUNMED TV is HOME, no prompt (`addPersistentPreferredActivity`). |
| Role held (API 29+) + power cycle | LUNMED TV is HOME, no prompt (role persisted). |
| HOME default picked manually + reboot | Usually LUNMED TV; some vendor ROMs restore their launcher (`BOOT_COMPLETED` + watchdog recover it). |
| Watchdog enabled + vendor launcher hijack after boot | Watchdog re-opens the Home screen within ~600 ms of the foreign launcher window appearing. |
| Root enforcement done | Vendor launcher packages disabled, so nothing can take over. |
| Fresh install, nothing granted | Android shows the Home chooser; the Home-screen banner and Settings → Default Launcher prompt the user. |
| Device Owner removed | `onDisabled` clears the pin and Home falls back to the normal chooser instead of dead-ending. |

## 5. Verification checklist (already automated)

* `:app:assembleDebug` — clean build, all modules compile.
* `:app:testDebugUnitTest` — `LunmedHomeWatchdogServiceTest` asserts the boot receiver no
  longer listens for `ACTION_REBOOT` and still handles `BOOT_COMPLETED`,
  `LOCKED_BOOT_COMPLETED`, `QUICKBOOT_POWERON` and HTC QuickBoot.
* Manifest assertions (by inspection): HOME+DEFAULT+LAUNCHER+LEANBACK filter,
  `RECEIVE_BOOT_COMPLETED`, `directBootAware` receiver, `device_admin` receiver with
  `BIND_DEVICE_ADMIN` + meta-data, accessibility service with
  `BIND_ACCESSIBILITY_SERVICE` + config, and the `<queries>` HOME intent required on
  Android 11+ for the launcher to resolve who currently owns HOME.

## 6. Known gaps

* `SettingsDetailPane` shows the literal `dpm set-device-owner` hint; it cannot execute it
  (no root shell in the UI thread, and `dpm` requires ADB/root).
* Android 11+ restricts accessibility key-event filtering to services the user explicitly
  enabled; the watchdog is silently inert when disabled, which `getState().isWatchdogEnabled`
  surfaces in Settings.
* Package visibility on Android 11+: the watchdog only sees HOME candidates declared in
  `<queries>`/the app's own filters. Unknown vendor launchers are still caught through the
  hard-coded `KNOWN_VENDOR_LAUNCHERS` list.
* No Play Store path for the Device Owner layer (enterprise/reseller sideload only); the
  RoleManager + HOME-default layers are store-safe.

