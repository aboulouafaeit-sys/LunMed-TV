# Remote Control Keymap & 10-Foot Ergonomics Guide

LUNMED TV is engineered from the ground up for **zero-touch 10-foot television ergonomics**. Every screen, rail, dialog, and control overlay is fully controllable using standard IR, Bluetooth LE, and RF 2.4G TV remote controls.

---

## 1. Standard D-Pad Navigation Map

| Remote Key | Android KeyCode | Action in LUNMED TV |
| :--- | :--- | :--- |
| **DPAD_UP** | `KEYCODE_DPAD_UP` (19) | Move focus to previous row, rail, or top bar navigation tabs. |
| **DPAD_DOWN** | `KEYCODE_DPAD_DOWN` (20) | Move focus to next row, rail, or content cards. |
| **DPAD_LEFT** | `KEYCODE_DPAD_LEFT` (21) | Move focus left within rail/grid. In App Library, steps to the Alphabet Rail. |
| **DPAD_RIGHT** | `KEYCODE_DPAD_RIGHT` (22) | Move focus right within rail/grid. Exits Alphabet Rail into the app grid. |
| **DPAD_CENTER / OK** | `KEYCODE_DPAD_CENTER` (23) / `KEYCODE_ENTER` (66) | Launch focused app or activate selected toggle/button. |
| **LONG-PRESS OK** | `KEYCODE_DPAD_CENTER` (>500ms) | Open App Quick Actions (Pin/Unpin Favorite, App Info, Uninstall). |
| **BACK / RETURN** | `KEYCODE_BACK` (4) | Dismiss overlay/modal dialog, close Apple Control Center, or return to Home. |
| **HOME** | `KEYCODE_HOME` (3) | Intercepted by `LunmedHomeWatchdogService` to return immediately to LUNMED TV Home. |
| **MENU** | `KEYCODE_MENU` (82) | Toggle Apple tvOS 18 Control Center overlay from any screen. |
| **PLAY / PAUSE** | `KEYCODE_MEDIA_PLAY_PAUSE` (85) | Contextual play/pause toggle in media spotlight stage. |

---

## 2. Advanced Remote Shortcuts

### Apple tvOS 18 Control Center Overlay
- **From Top Bar:** Click or press OK on the **Control Center** icon in the header.
- **From Remote:** Press the **MENU** key on your TV remote at any time.
- **Dismissal:** Press **BACK** or navigate to any outside tile and press OK.

### Fast Alphabet Rail Navigation (App Library)
On TV remotes without a full QWERTY keyboard or voice search:
1. From the first column of the App Grid, press **DPAD_LEFT**.
2. Focus enters the vertical **A-Z Alphabet Rail**.
3. Press **DPAD_UP** or **DPAD_DOWN** to navigate between letters.
4. The grid automatically fast-scrolls to the target letter without any manual typing.
5. Press **DPAD_RIGHT** to re-enter the app grid at that letter.

### App Context Menu & Release Protection
- Press and hold **DPAD_CENTER** on any application card for 500ms.
- The Card Context Menu smoothly ascends with options:
  - **Pin to Favorites / Unpin from Favorites**
  - **Open Android System App Info**
  - **Uninstall Application**
- **Defect Protection:** LUNMED TV debounces the trailing key-release event, preventing unintended app launches when lifting your thumb from the OK button.

---

## 3. Remote Control Types & Compatibility

| Remote Technology | Tested Hardware | Latency & Support |
| :--- | :--- | :--- |
| **Bluetooth LE Voice Remote** | Xiaomi Mi Voice, Google G10/G20, Shield Remote | $\le$ 16ms, full voice intent & D-Pad support. |
| **Infrared (IR) Standard Remote** | TX9 Pro, X96 Max, Tanix, OEM STB remotes | Responsive single-step and auto-repeat navigation. |
| **2.4G RF Air Mouse / Dongle** | G20S, Wechip W1, MX3 Air Mouse | Hybrid air mouse pointer + directional D-Pad supported. |
| **HDMI-CEC TV Remote** | Samsung Anynet+, LG SimpLink, Sony BRAVIA Sync | Directional arrows and Select commands passed directly via HDMI. |

