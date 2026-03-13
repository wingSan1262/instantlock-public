# InstantLock - AI-Powered Screen Content Guardian

InstantLock is an Android application that helps you maintain focus and control by automatically detecting and locking your device when it detects potentially distracting or inappropriate content on your screen.

## 🎥 How It Works

Watch the demo video to see InstantLock in action:
**[View Demo on YouTube Shorts](https://www.youtube.com/shorts/8wPF8V_tQS8)**

The app uses on-device AI (TensorFlow Lite) to analyze screen content and automatically lock your phone when it detects:
- NSFW/inappropriate content
- Incognito browsing patterns (blank screens suggesting hidden activity)
- Configurable lock modes based on your preferences

## 📥 Download

**Get the latest release here:**
**[Download InstantLock APK](https://github.com/wingSan1262/instantlock-public/releases)**

Choose from architecture-specific APKs for optimal performance:
- `app-arm64-v8a-release.apk` - For modern 64-bit ARM devices (recommended)
- `app-armeabi-v7a-release.apk` - For 32-bit ARM devices
- `app-x86_64-release.apk` - For 64-bit x86 devices (emulators/tablets)
- `app-x86-release.apk` - For 32-bit x86 devices
- `app-universal-release.apk` - Works on all devices (larger file size)

## ✨ Features

### 🤖 AI-Powered Content Detection
- **On-device NSFW detection** using TensorFlow Lite
- **MLKit Pose Detection** for body position analysis
- **Incognito behavior detection** via blank screen pattern analysis
- **Smart fuzzy logic algorithm** that adapts to usage patterns

### 🔒 Lock Modes
- **NSFW Lock Mode** - Blocks inappropriate content
- **Sexy Lock Mode** - Enhanced sensitivity for adult content
- **Night Time Lock Mode** - Scheduled protection during sleep hours
- **Panic 1-Hour Lock** - Emergency long-term lockdown with 30-second intervals

### 🛡️ Required Permissions
- **Device Admin** - Enables phone locking capability
- **Accessibility Service** - Captures screen content for AI analysis
- **Notification Access** - Displays lock notifications
- **Overlay Permission** - Shows lock screen overlays
- **Storage Access** (Android 10 only) - Reads screen activity metadata

### ⚙️ Additional Features
- Temporary accessibility service toggle
- Manufacturer-specific autostart configuration (Xiaomi, Vivo, Oppo, Honor, etc.)
- Easy uninstall helper (opens device admin settings)

## 🚀 Getting Started

1. **Download** the appropriate APK from [Releases](https://github.com/wingSan1262/instantlock-public/releases)
2. **Install** the APK on your Android 11+ device (minSdk 30)
3. **Grant permissions** - Follow the in-app setup wizard
4. **Configure lock modes** - Choose which detection modes to enable
5. **Stay focused** - The app runs in the background and protects you automatically

## 🔓 How to Uninstall

Because InstantLock uses Device Administrator privileges, you need to deactivate it first:

1. Open InstantLock app
2. Scroll to the **Uninstall** section at the bottom
3. Tap **"Open Settings"** button
4. Find InstantLock in the Device Administrators list
5. Deactivate it
6. Now you can uninstall normally from Android Settings

## 📱 Requirements

- **Android 11 (API 30) or higher** - Required for `AccessibilityService.takeScreenshot()`
- ~50MB storage space
- Device admin capability

## 🛠️ Technical Stack

- **Kotlin** - Primary language
- **Jetpack Compose** - Modern UI framework
- **TensorFlow Lite** - On-device ML inference
- **MLKit** - Pose detection
- **Coroutines** - Asynchronous operations
- **Material Design 3** - UI components

## 📄 License

This project is released for public use. See the repository for more details.

## 🤝 Contributing

Issues and pull requests are welcome! Feel free to report bugs or suggest features.

## ⚠️ Privacy Notice

**All AI processing happens entirely on your device.** No screen content or personal data is ever transmitted to external servers. InstantLock respects your privacy while helping you maintain focus and control.

---

**Current Version:** 0.6.0 (Build 11)

**Made with ❤️ for digital wellbeing**
