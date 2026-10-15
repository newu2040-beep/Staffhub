# 👥 StaffHub — All-in-One Staff & Workforce Management

StaffHub is a full-stack, native Android workforce management application built with Kotlin and Jetpack Compose. It empowers businesses of every size to seamlessly manage employees, attendance, shifts, leave, departments, working hours, and multi-format reports.

---

## 📥 Direct APK Download

Once published to your GitHub repository, the pre-compiled native APK is automatically distributed:

| Download Type | Permanent URL |
| :--- | :--- |
| **🚀 Direct Latest Release APK** | `https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPOSITORY_NAME>/releases/latest/download/app-debug.apk` |
| **📦 Repository Raw Mirror** | `https://github.com/<YOUR_GITHUB_USERNAME>/<YOUR_REPOSITORY_NAME>/raw/main/apk-distribution/app-debug.apk` |

*(Replace `<YOUR_GITHUB_USERNAME>` and `<YOUR_REPOSITORY_NAME>` with your GitHub repository path after pushing).*

---

## 🛡️ Pre-Compiled APK Verification

This genuine Android debug APK was compiled inside Google AI Studio's native Android build environment and is ready for immediate installation:

- **Application Name:** `StaffHub`
- **Application ID / Package:** `com.staffhub.apps`
- **Build Variant:** `debug`
- **Location:** `apk-distribution/app-debug.apk`
- **File Size:** ~29 MB
- **SHA-256 Checksum:** `27fae75ea4032481f4653f5d3a6ebaa9a3c547dd09466c21eee95b0b68e7c5f9`
- **Min SDK:** Android 7.0 (API 24)
- **Target SDK:** Android 15+ (API 36)

---

## 🤖 Automatic GitHub Release Automation

The included GitHub Actions workflow (`.github/workflows/release-apk.yml`) handles release publishing automatically:

1. **Zero-Setup Distribution:** It uses the genuine, pre-compiled APK from Google AI Studio. It does **not** recompile on GitHub Actions, avoiding long build times, Gradle setup, or environment discrepancies.
2. **Triggers:**
   - On every push to `main` or `master`
   - On release tags (e.g. `v1.0.0`)
   - Manual trigger via **Actions → Publish StaffHub APK Release → Run workflow**
3. **Artifacts Uploaded:**
   - `app-debug.apk`
   - `app-debug.apk.sha256`

---

## 📲 Installation Instructions

1. Download `app-debug.apk` onto your Android phone or tablet using either download URL above.
2. If Android prompts you that the app is from an unknown source, tap **Settings** and toggle **Allow from this source**.
3. Tap **Install** and open **StaffHub**.

---

## 🛠️ Project Configuration & Toolchain

- **Gradle Version:** `9.3.1`
- **Android Gradle Plugin (AGP):** `9.1.1`
- **Kotlin:** `2.2.10`
- **Compose Compiler BOM:** `2024.09.00`
- **Signing Configuration:** Pre-configured with Android debug key
