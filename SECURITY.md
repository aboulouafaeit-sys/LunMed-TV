# Security Policy

## Supported Versions

LUNMED TV is actively maintained on the `main` branch. Security updates and patches are issued for the following versions:

| Version | Supported | Recommended SDK Target |
| :--- | :--- | :--- |
| `1.0.x` (`main`) | :white_check_mark: | Android 7.0+ (API 24 to 35) |
| `< 1.0.0` | :x: | Deprecated |

---

## Threat Model & Security Posture

LUNMED TV is designed with security-in-depth principles tailored for Android TV:
1. **Zero Attack Surface via Network:** LUNMED TV does not open local listening sockets, does not run embedded web servers, and does not accept remote network connections.
2. **Hardened Intent Filters:** All internal BroadcastReceivers, Services, and Activities enforce explicit export controls (`android:exported="false"`) unless strictly required for launcher system contracts (`android.intent.category.HOME`, accessibility service, or device admin).
3. **Root Safety Safeguards:** Root execution commands (`su`) are sanitized, execute only static system package manager commands, and cannot be triggered by external applications or untrusted intents.
4. **Sandboxed Local Data:** User configuration is stored in private internal storage inaccessible to non-root applications.

---

## Reporting a Vulnerability

If you discover a security vulnerability within LUNMED TV, please do **NOT** open a public issue. We appreciate your efforts to responsibly disclose findings.

### Reporting Procedure

1. **Email:** Send full technical details, proof-of-concept steps, and affected device models to:
   - `security@lunmed.tv`
   - Or `aboulouafae.it@gmail.com`
2. **Subject Line:** Please format the subject as `[SECURITY DISCLOSURE] LunMed-TV - <Brief Vulnerability Description>`.
3. **Contents:**
   - Detailed description of the vulnerability.
   - Exact steps or script to reproduce.
   - Affected Android TV OS version, chipset, and launcher build commit.
   - Any proposed patch or mitigation if available.

### Response SLA

- **Acknowledgment:** Within 24-48 hours.
- **Triage & Assessment:** Within 5 business days.
- **Fix & Public Advisory:** Coordinated release once a patch has been compiled and validated.

