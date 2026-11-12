# Contributing to LUNMED TV

Thank you for your interest in contributing to **LUNMED TV**! This project aims to deliver the most fluid, luxurious, and reliable launcher experience for Android TV, Google TV, and AOSP TV Box ecosystems.

---

## 1. Development Principles

All contributions must adhere to our core architectural invariants:

1. **Strict Binary Size Budget:**
   - The minified release APK (`app-release.apk`) must strictly remain **$\le$ 2.0 MB (2,097,152 bytes)**.
   - Do NOT introduce bulky dependencies, heavy image assets, or unnecessary runtime libraries. Use vector drawables or procedural GPU shaders.
2. **10-Foot UI & Remote-First Navigation:**
   - Touch input is optional; D-Pad navigation is mandatory.
   - Every interactive element must be fully navigable with `DPAD_UP`, `DPAD_DOWN`, `DPAD_LEFT`, `DPAD_RIGHT`, and `DPAD_CENTER` / `ENTER`.
   - Visual focus states must provide high contrast (WCAG AAA) and fluid transition animations.
   - Guard against long-press keyup bounce.
3. **Zero-Mock Telemetry:**
   - System diagnostics (RAM, Storage, Display, Network, SoC) must query real Android platform APIs. Mock or fake data is strictly prohibited.
4. **Low-Memory Resilience:**
   - The launcher must perform smoothly on devices with $\le$ 2 GB RAM. Observe memory trim notifications (`onTrimMemory`) and cap bitmap cache sizes.
5. **Multi-Module Separation:**
   - Domain models reside in `:core:core-model`.
   - No cyclic dependencies between feature modules.

---

## 2. Getting Started

### Prerequisites
- **JDK 17+** (JDK 21 recommended)
- **Android SDK** with Command-line Tools & Platform SDK 35
- **Git**

### Cloning the Repository
```bash
git clone https://github.com/aboulouafaeit-sys/LunMed-TV.git
cd LunMed-TV
```

### Running Tests Locally
```bash
./gradlew test
```
All unit tests must pass before opening a Pull Request.

### Compiling Release APK
```bash
./gradlew :app:assembleRelease
```
Verify the generated APK in `app/build/outputs/apk/release/app-release.apk` is under 2.0 MB:
```powershell
(Get-Item app/build/outputs/apk/release/app-release.apk).Length
```

---

## 3. Commit Message Conventions

We enforce [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Adds a new user-facing capability or UI module.
- `fix:` Resolves a bug, crash, focus defect, or performance issue.
- `docs:` Documentation changes, guides, or README updates.
- `refactor:` Code restructuring without functional behavior changes.
- `perf:` Performance optimizations, memory cache tuning, or APK size reduction.
- `test:` Unit tests, instrumented tests, or test harness adjustments.
- `chore:` Dependency bumps, Gradle build configurations, or CI scripts.

*Example:* `feat(telemetry): add authentic Rockchip SoC hardware detection`

---

## 4. Submitting a Pull Request

1. Fork the repository and create your feature branch from `main`:
   ```bash
   git checkout -b feature/my-new-enhancement
   ```
2. Write clean, readable Kotlin code adhering to standard Kotlin style guides.
3. Ensure all existing tests pass and write new unit tests for any new business logic.
4. Verify your change builds cleanly without compiler warnings.
5. Push your branch to GitHub and open a Pull Request using our [PR Template](.github/pull_request_template.md).

