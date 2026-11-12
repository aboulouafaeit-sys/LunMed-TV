# Terms of Service & End-User License Agreement (EULA)

**Last Updated:** November 12, 2026  
**Effective Date:** November 12, 2026  
**Project:** LUNMED TV Launcher (`com.lunmed.tv`)  
**Repository:** [https://github.com/aboulouafaeit-sys/LunMed-TV](https://github.com/aboulouafaeit-sys/LunMed-TV)

---

## ملخص تنفيذي بالعربية (Executive Summary in Arabic)

تحدد هذه الوثيقة الشروط والأحكام القانونية الحاكمة لاستخدام وتوزيع مشغل **LUNMED TV** على أجهزة التلفاز الذكية (Android TV و Google TV و TV Boxes):
1. **طبيعة التطبيق**: LUNMED TV هو واجهة مستخدم رسومية ومشغل نظام (Home Launcher) مفتوح المصدر بموجب ترخيص MIT، صُمم لإدارة التطبيقات وإتاحة تجربة تصفح تلفزيونية سلسة بالريموت كنترول.
2. **إخلاء المسؤولية عن المحتوى**: التطبيق **لا يحتوي، ولا يستضيف، ولا يوزع، ولا يبث أي قنوات أو أفلام أو مسلسلات أو وسائط مقرصنة أو غير مصرح بها**. المستخدم والموزع مسؤولان تماماً عن أي محتوى أو تطبيقات طرف ثالث يتم تثبيتها على الجهاز.
3. **صلاحيات الروت (Root Privileges)**: ميزة تفعيل المشغل في أجهزة الروت تعمل محلياً بطلب صريح من المستخدم عبر أمر النظام المباشر، ويتحمل المستخدم وحده مسؤولية استخدام صلاحيات الروت على نظامه.
4. **الخصوصية التامة**: لا يقوم التطبيق بجمع أو إرسال أي بيانات شخصية أو سجلات تصفح إلى أي خادم خارجي نهائياً (100% On-Device & Zero Telemetry).

---

## 1. Acceptance of Terms

By downloading, installing, sideloading, compiling from source, or using **LUNMED TV** (hereinafter referred to as the "Software", "Launcher", or "Service"), you agree to be legally bound by these Terms of Service ("Terms") and our [Privacy Policy](PRIVACY.md). If you do not agree to these Terms, do not install or use the Software.

If you are deploying this Software on behalf of an organization, enterprise, or commercial reseller network, you represent and warrant that you possess the authority to bind such entity to these Terms.

---

## 2. License Grant & Open Source Governance

LUNMED TV is open-source software licensed under the **MIT License**.

- **Grant:** You are granted a worldwide, non-exclusive, royalty-free, transferable license to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software in accordance with the terms of the MIT License contained in the repository root [LICENSE](LICENSE).
- **Attribution:** Any redistribution, fork, or derivative work must retain the original copyright notice, this list of conditions, and the disclaimer of warranty in all substantial portions of the Software.

---

## 3. Scope of Software Functionality

LUNMED TV functions strictly as:
1. **An Android TV Home Launcher**: Facilitates application launching, category sorting, remote D-Pad navigation, and home screen management.
2. **A Hardware Diagnostics Utility**: Displays genuine, unmocked device telemetry (RAM usage, internal flash storage, display resolution/refresh rate, SoC identification, and network signal status).
3. **An Accessibility & System Management Tool**: Provides optional services (`LunmedHomeWatchdogService`, `RootLauncherSwitcher`) to maintain default launcher persistence across low-tier AOSP hardware and aggressive OEM launchers.

---

## 4. Media Content Disclaimer & Strict Copyright Policy

> [!IMPORTANT]
> **LUNMED TV DOES NOT PROVIDE, HOST, STORE, STREAM, INDEX, DISTRIBUTE, OR TRANSMIT ANY AUDIO, VIDEO, TELEVISION BROADCASTS, OR IPTV CONTENT.**

1. **Independent Shell Interface:** LUNMED TV is solely a navigation shell and launcher interface. It contains zero streaming servers, zero IPTV playlists, zero M3U generators, and zero proprietary media scrapers.
2. **Third-Party Applications:** Any video player, streaming application, live broadcast utility, or media portal accessed via LUNMED TV is developed and maintained independently by third parties. LUNMED TV exercises no editorial control, affiliation, or ownership over external software.
3. **User Responsibility:** You acknowledge and warrant that you will not utilize LUNMED TV to facilitate copyright infringement, circumvent digital rights management (DRM), or access unauthorized content in violation of applicable international copyright treaties and domestic laws.

---

## 5. Root Access, Device Owner & System Level Modifications

LUNMED TV provides optional advanced integration capabilities designed for Android TV boxes, including root-level default launcher configuration (`su -c pm set-home-activity`) and Device Owner provisioning (`dpm set-device-owner`):

1. **Explicit User Initiation:** Superuser commands are never executed silently or autonomously. Any execution of root commands is strictly gated behind direct, user-confirmed actions within Settings.
2. **Hardware & Firmware Disclaimer:** Altering system package configurations, disabling OEM vendor home activities, or granting Device Owner status can cause instability on non-standard Android firmware. LUNMED TV developers shall not be liable for bootloops, system freezes, voided warranties, or hardware malfunctions resulting from root commands or enterprise policy modifications.
3. **Reversibility:** Users maintain full control to restore stock launcher behavior at any time via standard Android settings, ADB recovery, or factory reset procedures.

---

## 6. Zero Telemetry & Privacy Guarantee

As detailed in our [Privacy Policy](PRIVACY.md):
- **No Remote Tracking:** LUNMED TV does not integrate analytics SDKs, advertising beacons, tracking pixels, or remote telemetry frameworks.
- **Local Device Confinement:** All application caches, user preferences, wallpaper selections, and diagnostic readings remain 100% contained within the local sandbox of your Android TV device.
- **Offline Reliability:** LUNMED TV is fully functional without any active internet connection.

---

## 7. Third-Party Trademarks & Non-Affiliation Disclaimer

- "Android", "Android TV", "Google TV", and the Google logo are registered trademarks of Google LLC.
- "Apple", "tvOS", and the Apple TV design language are registered trademarks of Apple Inc.
- "Amlogic", "Rockchip", "Allwinner", "MediaTek", "Qualcomm", and "NVIDIA" are trademarks of their respective semiconductor manufacturers.

The reference to these design styles (such as Apple tvOS 18 Bento Grid aesthetics or Google TV rail interfaces) and hardware chipsets is purely descriptive of compatibility, UI layouts, and hardware diagnostics. LUNMED TV is an independent open-source project and is **not affiliated with, endorsed by, sponsored by, or associated with Google LLC, Apple Inc., or any device manufacturer**.

---

## 8. Disclaimer of Warranties

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT.

IN NO EVENT SHALL THE AUTHORS, COPYRIGHT HOLDERS, OR CONTRIBUTORS BE LIABLE FOR ANY CLAIM, DAMAGES, OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

---

## 9. Limitation of Liability

To the maximum extent permitted by applicable law, in no event shall the authors or project maintainers be liable for any special, incidental, indirect, consequential, or punitive damages whatsoever (including, without limitation, damages for loss of business profits, business interruption, loss of television functionality, or data corruption) arising out of the use of or inability to use LUNMED TV.

---

## 10. Commercial Resellers & Enterprise Provisioning

Commercial distributors, hardware vendors, and TV box provisioning technicians are authorized under the MIT License to pre-install LUNMED TV on retail devices, provided that:
1. The original MIT copyright notices and this agreement remain accessible within the deployment or source distribution.
2. Resellers do not misrepresent LUNMED TV as an official product of Google, Apple, or TV OEM manufacturers.
3. Resellers assume sole legal and customer-support responsibility for any third-party streaming services or proprietary modifications bundled alongside the launcher.

Refer to the [Reseller Provisioning Guide](docs/RESELLER-PROVISIONING-GUIDE.md) for detailed deployment specifications.

---

## 11. Amendments & Updates

We reserve the right to revise or update these Terms of Service at our discretion. The most current revision will always be maintained within the official GitHub repository at:  
`https://github.com/aboulouafaeit-sys/LunMed-TV/blob/main/TERMS_OF_SERVICE.md`

---

## 12. Contact & Governance

For inquiries regarding licensing, technical governance, or enterprise deployment:
- **Repository Issues:** [https://github.com/aboulouafaeit-sys/LunMed-TV/issues](https://github.com/aboulouafaeit-sys/LunMed-TV/issues)
- **Security Disclosures:** Consult [SECURITY.md](SECURITY.md)
- **Email:** `dev@lunmed.tv` / `aboulouafae.it@gmail.com`

