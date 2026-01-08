# FoodMoodDiary - Food & Mood Tracking App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-SDK%2026+-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://openjdk.org/)

## 📋 Mục lục

- [Tổng quan](#overview)
- [Tính năng chính](#key-features)
- [Tech Stack](#tech-stack--architecture)
- [Cài đặt & Chạy](#getting-started)
  - [Hướng dẫn Windows](#windows-setup-guide)
  - [Hướng dẫn VS Code](#vs-code-setup)
  - [Hướng dẫn chạy nhanh](#quick-run-guide)
- [Cấu trúc dự án](#project-structure)
- [Xử lý lỗi](#troubleshooting)

---

## Overview

FoodMoodDiary is an Android mobile application that helps users track and discover the relationship between their diet and emotional state on a daily basis. By combining food journaling with mood tracking, the app provides insights into how different meals affect your emotional well-being.

## ✨ Key Features

### 🔐 Authentication
- **Email/Password Login:** Secure authentication with Firebase
- **Google Sign-In:** Quick login with Google One Tap
- **Remember Me:** 30-day session persistence with DataStore
- **Password Reset:** In-app password reset with deep link support

### 📸 Food Tracking
- **Camera Integration:** Take photos with CameraX
- **AI Color Analysis:** Automatic mood suggestions from food colors
- **Entry Management:** Add, edit, delete food entries
- **Location Tracking:** Optional geolocation for entries

### 📊 Analytics & Insights
- **Mood Trends:** Track emotional patterns over time
- **Food-Mood Correlation:** Discover which foods affect your mood
- **Calendar View:** Visual representation of daily entries
- **Statistics Dashboard:** Comprehensive data visualization

### 🤖 AI Assistant
- **Gemini 2.5 Flash Integration:** Chat with advanced AI for food and mood insights
- **Smart Suggestions:** Personalized recommendations based on your data
- **Friendly Error Messages:** User-friendly error handling with emoji and helpful tips
  - Network issues: "Mạng internet có vấn đề rồi! 📶"
  - Server overload (503): "Xin lỗi, AI đang bận quá! 😅"
  - Rate limiting (429): "Ối, bạn gửi tin nhắn hơi nhanh rồi! 😊"

### 🍜 Discovery
- **Vietnamese Meals:** Browse traditional dishes
- **Recipe Videos:** YouTube integration for cooking tutorials
- **Nutritional Info:** Calorie and ingredient information

### 👤 Profile Management
- **Streak Counter:** Track daily logging consistency
- **Theme Settings:** Light/Dark/Auto modes
- **Export Data:** Share entries via PDF/text

## Tech Stack & Architecture

FoodMoodDiary is built with modern Android development tools and follows best practices:

- **Language:** [Kotlin](https://kotlinlang.org/) 2.0.21
- **Framework:** Android SDK with Jetpack libraries
- **Architecture:** Clean Architecture with MVVM pattern
  - **Presentation Layer:** UI components built with Jetpack Compose, ViewModels managing UI state
  - **Domain Layer:** Business logic with Use Cases and repository interfaces
  - **Data Layer:** Room database, Firebase integration, and repository implementations
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material3 design
- **State Management:** StateFlow and Compose state management
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for compile-time dependency injection
- **Database:** Room (local) + Firebase Firestore (cloud sync)
- **Authentication:** Firebase Authentication with Google Sign-In
- **AI Integration:** Google Gemini API 2.5 Flash for chat assistance
- **Image Processing:** CameraX for camera integration, Palette API for color analysis
- **Async Operations:** Kotlin Coroutines and Flow
- **Background Tasks:** WorkManager for periodic reminders and sync

## 📁 Project Structure

The project follows Clean Architecture with feature-based organization:

```
FoodMoodDiary/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/haphuongquynh/foodmooddiary/
│   │   │   │   ├── data/                      # Data Layer
│   │   │   │   │   ├── local/                # Local data sources
│   │   │   │   │   │   ├── dao/             # Room DAOs
│   │   │   │   │   │   ├── entity/          # Room entities
│   │   │   │   │   │   └── preferences/     # DataStore (SessionManager)
│   │   │   │   │   └── repository/           # Repository implementations
│   │   │   │   │
│   │   │   │   ├── domain/                   # Domain Layer
│   │   │   │   │   ├── model/               # Domain models
│   │   │   │   │   ├── repository/          # Repository interfaces
│   │   │   │   │   ├── service/             # Services (AIInsightsService)
│   │   │   │   │   └── usecase/             # Business logic use cases
│   │   │   │   │
│   │   │   │   ├── presentation/            # Presentation Layer
│   │   │   │   │   ├── navigation/          # Navigation graphs
│   │   │   │   │   ├── screens/             # UI Screens (Compose)
│   │   │   │   │   │   ├── auth/           # LoginScreen, RegisterScreen, 
│   │   │   │   │   │   │                    # ForgotPasswordScreen, ResetPasswordScreen
│   │   │   │   │   │   ├── camera/         # CameraScreen (CameraX integration)
│   │   │   │   │   │   ├── detail/         # EntryDetailScreen, ModernEntryDetailScreen
│   │   │   │   │   │   ├── discovery/      # DiscoveryScreen (Vietnamese meals)
│   │   │   │   │   │   ├── entry/          # AddEntryScreen, EditEntryScreen, EntryListScreen
│   │   │   │   │   │   ├── home/           # SimpleHomeScreen (Main dashboard)
│   │   │   │   │   │   ├── main/           # MainScreen (Bottom navigation container)
│   │   │   │   │   │   ├── profile/        # ModernProfileScreen (User settings)
│   │   │   │   │   │   ├── splash/         # SplashScreen (App launcher)
│   │   │   │   │   │   ├── statistics/     # StatisticsScreen with 4 tabs:
│   │   │   │   │   │   │                    # - CalendarTab.kt (Monthly calendar view)
│   │   │   │   │   │   │                    # - ChartsTab.kt (Mood trends & analytics)
│   │   │   │   │   │   │                    # - InsightsTab.kt (Data patterns)
│   │   │   │   │   │   │                    # - AIInsightsTab.kt (Gemini AI insights)
│   │   │   │   │   │   │                    # Supporting: PieCharts.kt, TopFoodsChart.kt
│   │   │   │   │   │   └── ChatScreen.kt   # AI Chat with Gemini 2.5 Flash
│   │   │   │   │   │
│   │   │   │   │   └── viewmodel/           # ViewModels for each screen
│   │   │   │   │
│   │   │   │   ├── di/                      # Dependency Injection (Hilt modules)
│   │   │   │   ├── ui/                      # UI theme, colors, animations
│   │   │   │   ├── util/                    # Utility classes
│   │   │   │   │   ├── auth/               # GoogleSignInHelper
│   │   │   │   │   ├── color/              # Color analysis utilities
│   │   │   │   │   ├── common/             # Common helpers
│   │   │   │   │   ├── export/             # Data export utilities
│   │   │   │   │   ├── location/           # Location services
│   │   │   │   │   └── notification/       # Notification handling
│   │   │   │   │
│   │   │   │   ├── worker/                  # Background WorkManager tasks
│   │   │   │   ├── FoodMoodDiaryApp.kt     # Application class
│   │   │   │   └── MainActivity.kt          # Single activity (Compose)
│   │   │   │
│   │   │   ├── res/                         # Android resources
│   │   │   │   ├── drawable/               # Icons, images
│   │   │   │   ├── mipmap/                 # App launcher icons
│   │   │   │   └── values/                 # Strings, colors, themes
│   │   │   │
│   │   │   └── AndroidManifest.xml          # App manifest with permissions
│   │   │
│   │   ├── test/                            # Unit tests
│   │   └── androidTest/                     # Instrumentation tests
│   │
│   ├── build.gradle.kts                     # App module Gradle config
│   └── google-services.json                 # Firebase configuration (gitignored)
│
├── gradle/                                  # Gradle wrapper
│   └── libs.versions.toml                   # Centralized dependency versions
│
├── build.gradle.kts                         # Root Gradle config
├── settings.gradle.kts                      # Gradle settings
├── gradle.properties                        # Gradle properties (Java 17 config)
├── local.properties                         # Local config (API keys, SDK path)
├── .vscode/                                 # VS Code tasks and settings
└── README.md                               # This file
```

### Key Directories Explained

**📱 Presentation Layer (`presentation/`)**
- **screens/**: Each feature has its own screen file(s)
  - `auth/`: 4 authentication screens
  - `statistics/`: Complex multi-tab statistics with 7 files
  - `main/`: Bottom navigation container
  - Others: Single-file screens
- **viewmodel/**: ViewModel for each screen managing UI state
- **navigation/**: Jetpack Compose navigation setup

**💾 Data Layer (`data/`)**
- **local/dao/**: Room database Data Access Objects
- **local/entity/**: Database entities (tables)
- **local/preferences/**: DataStore for session management
- **repository/**: Implementation of domain repository interfaces

**🎯 Domain Layer (`domain/`)**
- **model/**: Core business models (independent of Android)
- **repository/**: Repository interfaces (contracts)
- **service/**: Business services (e.g., AIInsightsService)
- **usecase/**: Single-responsibility business logic units

**🔧 Utilities (`util/`)**
- **auth/**: Google Sign-In integration
- **color/**: Palette API for food color analysis
- **export/**: PDF and text export functionality
- **location/**: GPS and geolocation services
- **notification/**: Local notifications for reminders

## 🚀 Getting Started

Follow these instructions to get the project up and running on your local machine.

### Prerequisites

- Android Studio Ladybug or newer: [Installation Guide](https://developer.android.com/studio)
- **Java 17 LTS (Required):** [Download](https://adoptium.net/)
  - ⚠️ **Important:** Project is configured to use JDK 17. Using JDK 25 will cause build failures.
  - Configured in `gradle.properties` with `org.gradle.java.home`
- Android SDK 26-36
- Firebase Account: For backend services

### Installation & Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/quynh2204/FoodMoodDiary_Mobile.git
   cd FoodMoodDiary_Mobile
   ```

2. **Setup Firebase:**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json` and place it in the `app/` directory
   - Enable the following Firebase services:
     - Authentication (Email/Password and Google Sign-In)
     - Firestore Database
     - Cloud Storage (for images)

3. **Configure API Keys:**
   
   Create a `local.properties` file in the root directory and add:
   
   ```properties
   sdk.dir=/path/to/your/android/sdk
   
   # Google Maps API Key (optional, for map features)
   GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
   
   # Gemini AI API Key (required for chat feature)
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
   
   Get your Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey)
   
   **Model Used:** `gemini-2.5-flash` (Latest version with enhanced capabilities)

4. **Setup Firestore Vietnamese Meals Database (Optional):**
   
   To populate Vietnamese meals data:
   
   ```bash
   cd scripts
   pip install firebase-admin
   python3 upload_meals_to_firestore.py
   ```
   
   You'll need a Firebase service account key file (`serviceAccountKey.json`) in the `scripts/` folder.

5. **Sync & Build:**

   ```bash
   ./gradlew build
   ```

### Running the App

1. **Ensure an emulator is running or a device is connected.**
   
   Check connected devices:
   ```bash
   adb devices
   ```

2. **Run the app:**
   ```bash
   ./gradlew installDebug
   adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
   ```
   
   Or use the VS Code task: `Run FoodMoodDiary`
   
   Alternatively, run the app from Android Studio by clicking Run ▶️

## Key Features Highlight

### 📸 Smart Camera Integration
- Direct camera capture with CameraX
- Automatic color analysis using Palette API
- Mood suggestions based on food colors

### 🤖 AI-Powered Insights
- Chat with Gemini AI for food and mood advice
- Automated pattern recognition in eating habits
- Personalized suggestions for emotional well-being

### 📊 Comprehensive Analytics
- Mood trends over time with interactive charts
- Food-mood correlation analysis
- Calendar view with color-coded mood indicators

### 🍜 Vietnamese Cuisine Discovery
- Curated collection of Vietnamese dishes
- Recipes and YouTube video tutorials
- Save favorite meals for quick access

### ☁️ Cloud Sync & Offline Support
- Firebase integration for seamless cloud synchronization
- Offline-first architecture with Room database
- Data persistence across devices

## Firebase Setup Details

### Required Firebase Services

1. **Authentication:**
   - Enable Email/Password provider
   - Enable Google Sign-In provider
   - Configure OAuth consent screen

2. **Firestore Database:**
   - Create collection: `vietnameseMeals`
   - Collection stores Vietnamese dish information with fields:
     - `name` (string): Dish name
     - `category` (string): "Món nước", "Món khô", or "Tráng miệng"
     - `youtubeUrl` (string): Recipe video link
     - `imageUrl` (string): High-quality image URL
     - `calories` (number): Estimated calories
     - `description` (string): Dish description
     - `tags` (array): Related keywords

3. **Cloud Storage:**
   - Enable Storage for user-uploaded food images
   - Configure security rules for authenticated users

### Adding Vietnamese Meals to Firestore

**Method 1: Via Firebase Console (Easiest)**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: FoodMoodDiary
3. Navigate to **Firestore Database**
4. Select `vietnameseMeals` collection
5. Click **Add Document**
6. Fill in the fields as described above

**Method 2: Using Python Script**

1. Generate Firebase service account key:
   - Go to Project Settings > Service Accounts
   - Click "Generate New Private Key"
   - Save as `serviceAccountKey.json` in `scripts/` folder

2. Run the upload script:
   ```bash
   cd scripts
   pip install firebase-admin
   python3 upload_meals_to_firestore.py
   ```

## Troubleshooting

### Common Issues

**Java Version Mismatch (Critical)**
- ⚠️ **Must use Java 17 LTS** - JDK 25 causes build failures with error "25.0.1"
- Project is configured in `gradle.properties`:
  ```properties
  org.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home
  ```
- Check installed JDK versions:
  ```bash
  # macOS
  /usr/libexec/java_home -V
  
  # Windows
  dir "C:\Program Files\Java"
  ```
- If you see build error "25.0.1", uncomment the Java 17 path in `gradle.properties`
- After changing Java version, restart Gradle daemon:
  ```bash
  ./gradlew --stop
  ./gradlew build
  ```

**SDK Location Not Found**
- Edit `local.properties`:
  ```properties
  # macOS
  sdk.dir=/Users/your-username/Library/Android/sdk
  
  # Windows
  sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
  ```

**Gemini API Errors**

*403 Forbidden:*
- Your API key may be leaked or expired
- Generate a new key at [Google AI Studio](https://aistudio.google.com/app/apikey)
- Update `local.properties` with the new key

*503 Service Unavailable:*
- Gemini API is temporarily overloaded
- App now shows friendly message: "Xin lỗi, AI đang bận quá! 😅"
- Wait a few seconds and try again
- This is normal during peak usage times

*429 Too Many Requests:*
- You're sending messages too quickly
- App shows: "Ối, bạn gửi tin nhắn hơi nhanh rồi! 😊"
- Wait 1-2 minutes before retrying

**Build Dependency Issues**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

**Firebase Connection Issues**
- Verify `google-services.json` is in `app/` directory
- Check Firebase project settings match your bundle ID
- Ensure all required Firebase services are enabled

**Emulator Storage Full (INSTALL_FAILED_INSUFFICIENT_STORAGE)**
```bash
# Remove unused apps
adb shell pm list packages -3  # List user apps
adb shell pm uninstall <package_name>

# Clear cache
adb shell pm trim-caches 1G

# Or wipe emulator data
adb emu kill
emulator -avd <avd_name> -wipe-data
```

## Development Notes

### Key Android Concepts Demonstrated

1. **Jetpack Compose:** Modern declarative UI framework
2. **Clean Architecture:** Separation of concerns with layers
3. **Dependency Injection:** Hilt for managing dependencies
4. **Coroutines & Flow:** Asynchronous programming and reactive streams
5. **CameraX:** Camera integration with modern API
6. **Room Database:** Local data persistence
7. **Firebase Integration:** Authentication, Firestore, Storage
8. **WorkManager:** Background task scheduling
9. **Content Providers:** Sharing data with other apps
10. **Sensors:** Accelerometer and light sensor integration

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

## Future Enhancements

- [ ] Social features: Share entries with friends
- [ ] Advanced AI insights with personalized recommendations
- [ ] Integration with health apps (Google Fit)
- [ ] Export data to CSV/PDF formats
- [ ] Multi-language support (Vietnamese, English)
- [ ] Dark theme customization
- [ ] Widget for quick entry creation

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable/function names
- Comment complex logic
- Write unit tests for new features
- Update documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Android Jetpack](https://developer.android.com/jetpack) - Modern Android development
- [Firebase](https://firebase.google.com) - Backend as a Service
- [Google Gemini API](https://ai.google.dev/) - AI chat integration
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - Chart library
- [Lottie](https://airbnb.design/lottie/) - Animation library

---

Built with ❤️ using Kotlin & Jetpack Compose

---

# 📖 Hướng dẫn Setup & Sử dụng

## 🚀 Quick Run Guide

### Điều kiện tiên quyết
- ✅ Đã cài đặt Android Studio và Android SDK
- ✅ Đã cấu hình biến môi trường ANDROID_HOME
- ✅ Đã clone project về máy
- ✅ Đã tạo AVD (Android Virtual Device)

> ⚠️ **Nếu bạn chưa có các điều kiện trên**, vui lòng xem [Hướng dẫn cài đặt Windows đầy đủ](#windows-setup-guide) bên dưới.

### Các bước chạy app (macOS/Linux)

#### Bước 1: Mở Terminal tại thư mục dự án
```bash
cd /path/to/FoodMoodDiary
```

#### Bước 2: Kiểm tra danh sách AVD có sẵn
```bash
emulator -list-avds
```

#### Bước 3: Khởi động emulator
```bash
emulator -avd Small_Phone &
```

#### Bước 4: Kiểm tra emulator đã sẵn sàng
```bash
adb devices
```
**Kết quả mong đợi:**
```
List of devices attached
emulator-5554   device
```

#### Bước 5: Build và install app
```bash
./gradlew installDebug
```

#### Bước 6: Khởi động app
```bash
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

#### Bước 7: Cấp quyền (tùy chọn)
```bash
adb shell pm grant com.haphuongquynh.foodmooddiary android.permission.CAMERA
adb shell pm grant com.haphuongquynh.foodmooddiary android.permission.ACCESS_FINE_LOCATION
adb shell pm grant com.haphuongquynh.foodmooddiary android.permission.ACCESS_COARSE_LOCATION
```

### Lệnh gộp (All-in-one - macOS/Linux)
```bash
# Khởi động emulator
emulator -avd Small_Phone &

# Chờ emulator boot (30 giây)
sleep 30

# Build, install và chạy
./gradlew installDebug && adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### Các bước chạy app (Windows)

#### Bước 1: Mở PowerShell tại thư mục dự án
```powershell
cd D:\Path\To\FoodMoodDiary
```

#### Bước 2: Khởi động emulator
```powershell
Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"
```

#### Bước 3: Chờ và kiểm tra
```powershell
Start-Sleep -Seconds 30
adb devices
```

#### Bước 4: Build và chạy
```powershell
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### Debug Commands

**Xem log realtime:**
```bash
adb logcat | grep FoodMoodDiary
```

**Xem crash log:**
```bash
adb logcat -d | grep "AndroidRuntime"
```

**Restart app:**
```bash
adb shell am force-stop com.haphuongquynh.foodmooddiary
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

**Clear app data:**
```bash
adb shell pm clear com.haphuongquynh.foodmooddiary
```

**Uninstall app:**
```bash
adb uninstall com.haphuongquynh.foodmooddiary
```

---

## 🖥️ VS Code Setup

Hướng dẫn nhanh để chạy dự án Android trong VS Code.

### Extensions cần cài

Mở VS Code → `Ctrl+Shift+X` (Windows/Linux) hoặc `Cmd+Shift+X` (macOS) → tìm và cài:

1. **Android iOS Emulator** - DiemasMichiels.emulate
2. **Kotlin Language** - mathiasfrohlich.Kotlin  
3. **Gradle for Java** - vscjava.vscode-gradle

### Cấu hình tự động

Dự án đã có sẵn:
- ✅ `.vscode/tasks.json` - Gradle tasks
- ✅ `.vscode/settings.json` - Cấu hình workspace
- ✅ `.vscode/extensions.json` - Extensions khuyến nghị

### Chạy app nhanh

**Cách 1: Dùng Tasks (Khuyến nghị)**
1. Nhấn `Ctrl+Shift+P` (Windows/Linux) hoặc `Cmd+Shift+P` (macOS)
2. Gõ: `Tasks: Run Task`
3. Chọn: **Run FoodMoodDiary**

**Cách 2: Terminal**
```bash
# macOS/Linux
./gradlew installDebug && adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity

# Windows PowerShell
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### Quản lý Emulator trong VS Code

**Khởi động emulator:**

- **Cách 1:** `Ctrl/Cmd+Shift+P` → `Emulator: Start` → Chọn AVD

- **Cách 2:** Terminal
  ```bash
  emulator -avd Small_Phone
  ```

**Kiểm tra emulator:**
```bash
adb devices
```

### Tasks có sẵn

Nhấn `Ctrl/Cmd+Shift+P` → `Tasks: Run Task`:

| Task | Mô tả |
|------|-------|
| **Run FoodMoodDiary** | Build, install và khởi động app |
| **Build Debug APK** | Build app (không install) |
| **Install Debug APK** | Build và install |
| **Clean Build** | Xóa build cũ |
| **View Logcat** | Xem log app |
| **List Devices** | Danh sách devices |
| **Uninstall App** | Gỡ app |

### Workflow hàng ngày

```bash
# 1. Mở dự án trong VS Code
code /path/to/FoodMoodDiary

# 2. Khởi động emulator (Ctrl/Cmd+Shift+P → Tasks: Run Task → Start Emulator)

# 3. Chờ boot xong (~30s), kiểm tra
adb devices

# 4. Build và chạy (Ctrl/Cmd+Shift+P → Tasks: Run Task → Run FoodMoodDiary)

# 5. Xem log
adb logcat | grep FoodMoodDiary
```

### Shortcuts hữu ích

| Phím (Windows/Linux) | Phím (macOS) | Chức năng |
|------|-----------|-----------|
| `Ctrl+Shift+P` | `Cmd+Shift+P` | Command Palette |
| `Ctrl+` ` | `Cmd+` ` | Toggle Terminal |
| `Ctrl+Shift+B` | `Cmd+Shift+B` | Build Task |
| `Ctrl+,` | `Cmd+,` | Settings |

### Debug trong VS Code

**Xem log:**
```bash
# Trong Terminal VS Code
adb logcat | grep FoodMoodDiary
```

**Xem crash log:**
```bash
adb logcat -s AndroidRuntime:E
```

**Clear data app:**
```bash
adb shell pm clear com.haphuongquynh.foodmooddiary
```

**Reinstall app:**
```bash
./gradlew uninstallDebug installDebug
```

### Tips

1. **Build nhanh hơn:** Sửa `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx4096m
   org.gradle.parallel=true
   org.gradle.caching=true
   ```

2. **Terminal múltiple:** `Ctrl/Cmd+Shift+` ` để tạo terminal mới

3. **Emulator snapshot:** Lưu trạng thái emulator để boot nhanh hơn

---

## 🪟 Windows Setup Guide

Hướng dẫn chi tiết cài đặt môi trường phát triển Android trên Windows từ đầu.

### Yêu cầu hệ thống
- Windows 10/11 (64-bit)
- RAM: Tối thiểu 8GB (khuyến nghị 16GB)
- Ổ cứng trống: Tối thiểu 10GB
- Kết nối Internet ổn định

### Bước 1: Cài đặt JDK (Java Development Kit)

#### 1.1. Download JDK 17
- Truy cập: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
- Hoặc dùng OpenJDK: https://adoptium.net/
- Chọn **Windows x64 Installer** (.msi)

#### 1.2. Cài đặt JDK
1. Chạy file .msi vừa tải
2. Chọn đường dẫn cài đặt (ví dụ: `C:\Program Files\Java\jdk-17`)
3. Click **Next** → **Install** → **Finish**

#### 1.3. Cấu hình biến môi trường
1. Nhấn `Win + X` → chọn **System**
2. Click **Advanced system settings** → **Environment Variables**
3. Trong **System variables**, click **New**:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-17`
4. Tìm biến **Path** → click **Edit** → **New** → thêm:
   ```
   %JAVA_HOME%\bin
   ```
5. Click **OK** để lưu

#### 1.4. Kiểm tra cài đặt
```powershell
java -version
javac -version
```

### Bước 2: Cài đặt Android Studio

#### 2.1. Download Android Studio
- Truy cập: https://developer.android.com/studio
- Download **Android Studio** phiên bản mới nhất

#### 2.2. Cài đặt Android Studio
1. Chạy file cài đặt
2. Chọn **Standard Installation**
3. Chọn theme (Dark/Light)
4. Chờ download các SDK components (5-10 phút)

#### 2.3. Cấu hình Android SDK
1. Mở Android Studio
2. Click **More Actions** → **SDK Manager**
3. Trong tab **SDK Platforms**, tích chọn:
   - ✅ Android 14.0 (API 34)
   - ✅ Android 8.0 (API 26)
4. Trong tab **SDK Tools**, tích chọn:
   - ✅ Android SDK Build-Tools
   - ✅ Android SDK Command-line Tools
   - ✅ Android Emulator
   - ✅ Android SDK Platform-Tools
5. Click **Apply** → **OK**

#### 2.4. Cấu hình biến môi trường Android SDK

**Tìm đường dẫn SDK:**
- Android Studio → **SDK Manager**
- Copy đường dẫn **Android SDK Location**

**Thêm biến môi trường:**
1. `Win + X` → **System** → **Environment Variables**
2. Trong **System variables**, click **New**:
   - Variable name: `ANDROID_HOME`
   - Variable value: `C:\Users\YourName\AppData\Local\Android\Sdk`
3. Tìm biến **Path**, thêm:
   ```
   %ANDROID_HOME%\platform-tools
   %ANDROID_HOME%\emulator
   %ANDROID_HOME%\tools
   ```

#### 2.5. Kiểm tra
```powershell
adb version
emulator -version
```

### Bước 3: Cài đặt Git

#### 3.1. Download Git
- Truy cập: https://git-scm.com/download/win
- Download **64-bit Git for Windows Setup**

#### 3.2. Cài đặt Git
1. Chạy file cài đặt với tùy chọn mặc định
2. Editor: chọn **Visual Studio Code**

#### 3.3. Kiểm tra
```powershell
git --version
```

### Bước 4: Clone dự án

```powershell
cd D:\Projects
git clone https://github.com/quynh2204/FoodMoodDiary_Mobile.git
cd FoodMoodDiary_Mobile
```

### Bước 5: Cấu hình Firebase

1. Liên hệ owner để lấy file `google-services.json`
2. Hoặc tạo Firebase project mới tại https://console.firebase.google.com/
3. Copy file vào: `FoodMoodDiary_Mobile/app/google-services.json`

### Bước 6: Tạo Android Virtual Device (AVD)

#### 6.1. Mở AVD Manager
1. Android Studio → **More Actions** → **Virtual Device Manager**
2. Click **Create Device**

#### 6.2. Chọn thiết bị
1. Category: **Phone**
2. Chọn: **Pixel 5** hoặc **Small Phone**
3. Click **Next**

#### 6.3. Chọn System Image
1. Tab **Recommended**: **UpsideDownCake (API 34)**
2. Click **Download** nếu chưa có
3. Click **Next**

#### 6.4. Cấu hình AVD
1. AVD Name: `Small_Phone`
2. Click **Show Advanced Settings**:
   - RAM: 2048 MB
   - Graphics: **Hardware - GLES 2.0**
3. Click **Finish**

#### 6.5. Test emulator
Click ▶️ **Play** để khởi động emulator

### Bước 7: Build và chạy

#### Từ Android Studio
1. Open project
2. Chọn device: **Small_Phone**
3. Click ▶️ **Run**

#### Từ PowerShell
```powershell
# Khởi động emulator
Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"

# Chờ boot
Start-Sleep -Seconds 30

# Build và chạy
.\gradlew installDebug
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

#### Từ VS Code
1. Cài extensions (xem [VS Code Setup](#vs-code-setup))
2. `Ctrl+Shift+P` → `Tasks: Run Task` → **Run FoodMoodDiary**

### Xử lý lỗi thường gặp

**"JAVA_HOME is not set"**
- Kiểm tra lại Bước 1.3
- Khởi động lại PowerShell

**"SDK location not found"**
- Tạo file `local.properties`:
  ```properties
  sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
  ```

**Emulator không khởi động**
- Đổi Graphics sang **Software - GLES 2.0**
- Bật Virtualization trong BIOS (Intel VT-x / AMD-V)

**"adb: device offline"**
```powershell
adb kill-server
adb start-server
```

**Gradle build chậm**
- Edit `gradle.properties`:
  ```properties
  org.gradle.jvmargs=-Xmx4096m
  org.gradle.parallel=true
  org.gradle.caching=true
  ```

**App crash khi mở**
- Kiểm tra file `google-services.json`
- Xem log:
  ```powershell
  adb logcat | Select-String "FoodMoodDiary"
  ```

### Tối ưu hiệu suất

#### Tăng tốc Gradle
Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
kotlin.incremental=true
```

#### Tạo alias PowerShell
```powershell
notepad $PROFILE
```
Thêm:
```powershell
function Start-Emulator {
    Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"
}

function Install-App {
    .\gradlew installDebug
    adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
}

Set-Alias emu Start-Emulator
Set-Alias run Install-App
```

Sau đó:
```powershell
emu    # Khởi động emulator
run    # Build và chạy
```

### Workflow hàng ngày (Windows)

```powershell
# 1. Mở PowerShell tại thư mục dự án
cd D:\Projects\FoodMoodDiary_Mobile

# 2. Pull code mới
git pull origin main

# 3. Khởi động emulator
Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"

# 4. Chờ boot và kiểm tra
Start-Sleep -Seconds 30
adb devices

# 5. Build và chạy
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

---
