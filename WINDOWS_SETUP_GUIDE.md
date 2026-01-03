# Hướng dẫn cài đặt và chạy FoodMoodDiary trên Windows

## Yêu cầu hệ thống
- Windows 10/11 (64-bit)
- RAM: Tối thiểu 8GB (khuyến nghị 16GB)
- Ổ cứng trống: Tối thiểu 10GB
- Kết nối Internet ổn định

---

## Bước 1: Cài đặt JDK (Java Development Kit)

### 1.1. Download JDK 17
- Truy cập: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html
- Hoặc dùng OpenJDK: https://adoptium.net/
- Chọn **Windows x64 Installer** (.msi)

### 1.2. Cài đặt JDK
1. Chạy file .msi vừa tải
2. Chọn đường dẫn cài đặt (ví dụ: `C:\Program Files\Java\jdk-17`)
3. Click **Next** → **Install** → **Finish**

### 1.3. Cấu hình biến môi trường
1. Nhấn `Win + X` → chọn **System**
2. Click **Advanced system settings** → **Environment Variables**
3. Trong **System variables**, click **New**:
   - Variable name: `JAVA_HOME`
   - Variable value: `C:\Program Files\Java\jdk-17` (đường dẫn JDK của bạn)
4. Tìm biến **Path** → click **Edit** → **New** → thêm:
   ```
   %JAVA_HOME%\bin
   ```
5. Click **OK** để lưu

### 1.4. Kiểm tra cài đặt
Mở **PowerShell** và chạy:
```powershell
java -version
javac -version
```
Kết quả phải hiện `java version "17.x.x"`

---

## Bước 2: Cài đặt Android Studio

### 2.1. Download Android Studio
- Truy cập: https://developer.android.com/studio
- Download **Android Studio** phiên bản mới nhất
- Kích thước file: ~1GB

### 2.2. Cài đặt Android Studio
1. Chạy file cài đặt (android-studio-xxx.exe)
2. Chọn **Standard Installation**
3. Chọn theme (Dark/Light)
4. Chờ download các SDK components (khoảng 5-10 phút)

### 2.3. Cấu hình Android SDK
1. Mở Android Studio
2. Click **More Actions** → **SDK Manager**
3. Trong tab **SDK Platforms**, tích chọn:
   - ✅ Android 14.0 (UpsideDownCake) - API Level 34
   - ✅ Android 8.0 (Oreo) - API Level 26
4. Trong tab **SDK Tools**, tích chọn:
   - ✅ Android SDK Build-Tools
   - ✅ Android SDK Command-line Tools
   - ✅ Android Emulator
   - ✅ Android SDK Platform-Tools
   - ✅ Google Play services
5. Click **Apply** → **OK** → chờ download

### 2.4. Cấu hình biến môi trường Android SDK
**Tìm đường dẫn SDK:**
- Android Studio → **More Actions** → **SDK Manager**
- Copy đường dẫn **Android SDK Location** (ví dụ: `C:\Users\YourName\AppData\Local\Android\Sdk`)

**Thêm biến môi trường:**
1. `Win + X` → **System** → **Advanced system settings** → **Environment Variables**
2. Trong **System variables**, click **New**:
   - Variable name: `ANDROID_HOME`
   - Variable value: `C:\Users\YourName\AppData\Local\Android\Sdk`
3. Tìm biến **Path**, click **Edit** → **New**, thêm:
   ```
   %ANDROID_HOME%\platform-tools
   %ANDROID_HOME%\emulator
   %ANDROID_HOME%\tools
   %ANDROID_HOME%\tools\bin
   ```
4. Click **OK** để lưu

### 2.5. Kiểm tra cài đặt
Mở **PowerShell mới** và chạy:
```powershell
adb version
emulator -version
```

---

## Bước 3: Cài đặt Git

### 3.1. Download Git
- Truy cập: https://git-scm.com/download/win
- Download **64-bit Git for Windows Setup**

### 3.2. Cài đặt Git
1. Chạy file cài đặt
2. Chọn các tùy chọn mặc định
3. Editor: chọn **Use Visual Studio Code** (hoặc editor bạn thích)
4. Click **Next** → **Install** → **Finish**

### 3.3. Kiểm tra cài đặt
```powershell
git --version
```

---

## Bước 4: Clone dự án

### 4.1. Clone repository
Mở **PowerShell** tại thư mục bạn muốn lưu dự án:
```powershell
cd D:\Projects  # Hoặc thư mục bạn muốn
git clone https://github.com/quynh2204/FoodMoodDiary_Mobile.git
cd FoodMoodDiary_Mobile
```

### 4.2. Chuyển sang branch Frontend (nếu cần)
```powershell
git checkout Frontend
```

---

## Bước 5: Cấu hình Firebase

### 5.1. Tải file google-services.json
1. Liên hệ với owner dự án để lấy file `google-services.json`
2. Hoặc tạo Firebase project mới:
   - Truy cập: https://console.firebase.google.com/
   - Tạo project mới
   - Thêm Android app với package name: `com.haphuongquynh.foodmooddiary`
   - Download file `google-services.json`

### 5.2. Đặt file vào đúng vị trí
Copy file `google-services.json` vào:
```
FoodMoodDiary_Mobile/app/google-services.json
```

---

## Bước 6: Tạo và cấu hình Android Virtual Device (AVD)

### 6.1. Mở AVD Manager
1. Mở Android Studio
2. Click **More Actions** → **Virtual Device Manager**
3. Click **Create Device**

### 6.2. Chọn thiết bị
1. Category: **Phone**
2. Chọn device: **Pixel 5** hoặc **Small Phone** (như trong dự án)
3. Click **Next**

### 6.3. Chọn System Image
1. Tab **Recommended**: Chọn **UpsideDownCake (API 34)**
2. Nếu chưa download, click icon **Download** bên cạnh
3. Chờ download xong (khoảng 1-2GB)
4. Click **Next**

### 6.4. Cấu hình AVD
1. AVD Name: `Small_Phone` (hoặc tên bạn thích)
2. Startup orientation: **Portrait**
3. Click **Show Advanced Settings**:
   - RAM: 2048 MB (tối thiểu)
   - VM heap: 256 MB
   - Internal Storage: 2048 MB
   - Graphics: **Hardware - GLES 2.0** (khuyến nghị) hoặc **Automatic**
4. Click **Finish**

### 6.5. Test khởi động emulator
1. Trong AVD Manager, click nút ▶️ **Play** bên cạnh AVD vừa tạo
2. Chờ emulator khởi động (lần đầu có thể mất 2-5 phút)
3. Nếu gặp lỗi OpenGL, thử đổi Graphics sang **Software - GLES 2.0**

---

## Bước 7: Build và chạy dự án

### 7.1. Mở dự án trong Android Studio
1. Mở Android Studio
2. Click **Open** → chọn thư mục `FoodMoodDiary_Mobile`
3. Chờ Gradle sync (lần đầu có thể mất 5-10 phút)
4. Nếu có lỗi sync, click **Sync Project with Gradle Files**

### 7.2. Chạy từ Android Studio (Cách 1)
1. Đảm bảo emulator đã chạy (hoặc Android Studio sẽ tự khởi động)
2. Chọn device target: **Small_Phone** trong dropdown
3. Click nút ▶️ **Run** (hoặc Shift+F10)
4. Chờ build và install (~2-3 phút lần đầu)

### 7.3. Chạy từ VS Code (Cách 2 - Khuyến nghị)

#### 7.3.1. Cài đặt VS Code
1. Download VS Code: https://code.visualstudio.com/
2. Cài đặt với tùy chọn mặc định

#### 7.3.2. Cài đặt Extensions trong VS Code
Mở VS Code, nhấn `Ctrl+Shift+X` để mở Extensions, tìm và cài đặt:

**Extensions bắt buộc:**
1. **Android iOS Emulator** (by DiemasMichiels)
   - Chạy và quản lý emulator trực tiếp trong VS Code
   - ID: `DiemasMichiels.emulate`

2. **Kotlin Language** (by mathiasfrohlich)
   - Hỗ trợ syntax highlighting cho Kotlin
   - ID: `mathiasfrohlich.Kotlin`

3. **Gradle for Java** (by Microsoft)
   - Chạy Gradle tasks từ VS Code
   - ID: `vscjava.vscode-gradle`

**Extensions tùy chọn (nâng cao trải nghiệm):**
4. **Android Full Support** (by kymdesign)
   - XML, logcat viewer
   - ID: `kymdesign.android-full-support`

5. **XML Tools** (by Josh Johnson)
   - Format XML layouts
   - ID: `DotJoshJohnson.xml`

6. **Material Icon Theme** (by Philipp Kief)
   - Icon đẹp cho file Android
   - ID: `PKief.material-icon-theme`

#### 7.3.3. Cấu hình VS Code cho Android

**Tạo file cấu hình tasks:**
Tạo thư mục `.vscode` trong thư mục dự án, tạo file `tasks.json`:

```json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Build Debug APK",
            "type": "shell",
            "command": "${workspaceFolder}/gradlew",
            "args": ["assembleDebug"],
            "group": {
                "kind": "build",
                "isDefault": true
            },
            "problemMatcher": []
        },
        {
            "label": "Install Debug APK",
            "type": "shell",
            "command": "${workspaceFolder}/gradlew",
            "args": ["installDebug"],
            "problemMatcher": []
        },
        {
            "label": "Clean Build",
            "type": "shell",
            "command": "${workspaceFolder}/gradlew",
            "args": ["clean"],
            "problemMatcher": []
        },
        {
            "label": "Build and Run",
            "type": "shell",
            "command": "${workspaceFolder}/gradlew installDebug && adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity",
            "problemMatcher": []
        },
        {
            "label": "View Logcat",
            "type": "shell",
            "command": "adb",
            "args": ["logcat", "-s", "FoodMoodDiary"],
            "isBackground": true,
            "problemMatcher": []
        }
    ]
}
```

**Tạo file settings cho workspace:**
Tạo file `.vscode/settings.json`:

```json
{
    "gradle.nestedProjects": true,
    "files.exclude": {
        "**/.gradle": true,
        "**/.idea": true,
        "**/build": true
    },
    "java.configuration.updateBuildConfiguration": "automatic"
}
```

#### 7.3.4. Chạy emulator từ VS Code

**Cách 1: Dùng Extension Android iOS Emulator**
1. Nhấn `Ctrl+Shift+P` (Command Palette)
2. Gõ: `Emulator: Start`
3. Chọn AVD đã tạo: `Small_Phone`
4. Emulator sẽ khởi động trong cửa sổ riêng

**Cách 2: Dùng Terminal trong VS Code**
1. Mở Terminal: `Ctrl+` ` (backtick) hoặc View → Terminal
2. Chạy lệnh:
```powershell
# Khởi động emulator
Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"

# Hoặc đơn giản hơn (nếu đã có trong PATH)
emulator -avd Small_Phone
```

#### 7.3.5. Build và Install App từ VS Code

**Cách 1: Dùng Tasks (Khuyến nghị)**
1. Nhấn `Ctrl+Shift+P`
2. Gõ: `Tasks: Run Task`
3. Chọn task:
   - **Build Debug APK** - Chỉ build
   - **Install Debug APK** - Build và install
   - **Build and Run** - Build, install và khởi động app
   - **Clean Build** - Clean trước khi build

**Cách 2: Dùng Terminal**
```powershell
# Build và install
.\gradlew installDebug

# Khởi động app
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity

# Hoặc gộp lại
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

**Cách 3: Dùng Gradle Extension**
1. Mở Gradle panel: View → Gradle
2. Expand: `FoodMoodDiary_Mobile → app → Tasks → install`
3. Right-click `installDebug` → **Run**

#### 7.3.6. Xem Logcat trong VS Code

**Cách 1: Dùng Task đã tạo**
1. `Ctrl+Shift+P` → `Tasks: Run Task` → `View Logcat`
2. Log sẽ hiện trong Terminal

**Cách 2: Dùng Extension Android Full Support**
1. Nhấn `Ctrl+Shift+P`
2. Gõ: `Android: View Logcat`
3. Chọn device: `emulator-5554`

**Cách 3: Terminal thủ công**
```powershell
# Xem tất cả log
adb logcat

# Chỉ xem log của app
adb logcat | Select-String "FoodMoodDiary"

# Xem log với filter
adb logcat -s FoodMoodDiary:V
```

#### 7.3.7. Shortcuts hữu ích trong VS Code

| Phím tắt | Chức năng |
|----------|-----------|
| `Ctrl+Shift+P` | Command Palette |
| `Ctrl+` ` | Toggle Terminal |
| `Ctrl+Shift+B` | Run Build Task |
| `F5` | Start Debugging (sau khi config) |
| `Ctrl+K Ctrl+S` | Keyboard Shortcuts |
| `Ctrl+,` | Settings |

#### 7.3.8. Quản lý emulator trong VS Code

**Danh sách emulator:**
```powershell
# Trong Terminal VS Code
emulator -list-avds
```

**Khởi động emulator cụ thể:**
```powershell
emulator -avd Small_Phone -no-snapshot-load
```

**Kiểm tra emulator đang chạy:**
```powershell
adb devices
```

**Dừng emulator:**
```powershell
adb -s emulator-5554 emu kill
```

**Screenshot từ emulator:**
```powershell
adb shell screencap -p /sdcard/screenshot.png
adb pull /sdcard/screenshot.png
```

#### 7.3.9. Workflow làm việc hàng ngày với VS Code

```powershell
# 1. Mở VS Code tại thư mục dự án
cd D:\Projects\FoodMoodDiary_Mobile
code .

# 2. Mở Terminal trong VS Code (Ctrl+`)

# 3. Khởi động emulator
emulator -avd Small_Phone

# 4. Đợi emulator boot, kiểm tra
adb devices

# 5. Build và chạy (dùng Task hoặc Terminal)
# Cách 1: Ctrl+Shift+P → Tasks: Run Task → Build and Run
# Cách 2: Terminal
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity

# 6. Xem log khi test
adb logcat | Select-String "FoodMoodDiary"
```

### 7.4. Chạy từ PowerShell độc lập (Cách 3)
Mở **PowerShell** tại thư mục dự án:

**Bước 1: Khởi động emulator** (trong PowerShell riêng)
```powershell
# Tìm tên AVD của bạn
emulator -list-avds

# Khởi động emulator
Start-Process -FilePath "C:\Users\YourName\AppData\Local\Android\Sdk\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"
```

**Bước 2: Đợi emulator boot xong** (chờ khoảng 1-2 phút), kiểm tra:
```powershell
adb devices
```
Kết quả phải hiện `emulator-5554    device`

**Bước 3: Build và install app**
```powershell
# Build debug APK
.\gradlew assembleDebug

# Install vào emulator
.\gradlew installDebug

# Khởi động app
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

Hoặc gộp lại thành 1 lệnh:
```powershell
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### 7.5. So sánh 3 cách chạy

| Tiêu chí | Android Studio | VS Code | PowerShell |
|----------|---------------|---------|------------|
| **Dễ setup** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Tốc độ** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **RAM sử dụng** | ~2GB | ~500MB | ~200MB |
| **Tính năng** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Phù hợp** | Beginner | Developer | Advanced |

**Khuyến nghị:**
- 🔰 **Mới học:** Dùng Android Studio (đầy đủ tính năng, hỗ trợ tốt)
- 💻 **Đã quen:** Dùng VS Code (nhẹ, nhanh, tùy biến cao)
- ⚡ **Build nhanh:** Dùng PowerShell (chỉ cần build/test)

---

## Bước 8: Xử lý lỗi thường gặp

### Lỗi 1: "JAVA_HOME is not set"
**Giải pháp:**
- Xem lại Bước 1.3, đảm bảo đã set biến `JAVA_HOME`
- Khởi động lại PowerShell sau khi set biến môi trường

### Lỗi 2: "SDK location not found"
**Giải pháp:**
Tạo file `local.properties` trong thư mục gốc dự án:
```properties
sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```
(Thay `YourName` bằng username Windows của bạn)

### Lỗi 3: Emulator không khởi động / màn hình đen
**Giải pháp:**
1. Mở AVD Manager → Edit AVD
2. Đổi Graphics: **Software - GLES 2.0**
3. Hoặc bật Virtualization trong BIOS:
   - Restart PC → vào BIOS (phím F2/Del/F10)
   - Enable **Intel VT-x** hoặc **AMD-V**
   - Enable **Hyper-V** trong Windows Features

### Lỗi 4: "adb: device offline"
**Giải pháp:**
```powershell
# Kill và restart adb server
adb kill-server
adb start-server
adb devices
```

### Lỗi 5: Gradle build quá chậm
**Giải pháp:**
Tạo/sửa file `gradle.properties` trong thư mục dự án:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.daemon=true
```

### Lỗi 6: "Execution failed for task ':app:mergeDebugResources'"
**Giải pháp:**
```powershell
# Clean build
.\gradlew clean
# Build lại
.\gradlew assembleDebug
```

### Lỗi 7: Emulator chạy nhưng app crash ngay khi mở
**Giải pháp:**
- Kiểm tra file `google-services.json` đã đúng vị trí chưa
- Xem log để debug:
```powershell
adb logcat | Select-String "FoodMoodDiary"
```

---

## Bước 9: Cấu hình tùy chọn (Optional)

### 9.1. Tăng tốc Gradle build
Edit file `gradle.properties`:
```properties
# Increase build performance
org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=1024m -XX:+HeapDumpOnOutOfMemoryError
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
kotlin.incremental=true
kotlin.compiler.execution.strategy=in-process
```

### 9.2. Tạo alias cho PowerShell
Edit file PowerShell profile:
```powershell
notepad $PROFILE
```
Thêm vào:
```powershell
# Android shortcuts
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
Sau đó chỉ cần gõ:
```powershell
emu    # Khởi động emulator
run    # Build và chạy app
```

---

## Bước 10: Kiểm tra cài đặt hoàn chỉnh

Chạy các lệnh sau để đảm bảo mọi thứ đã sẵn sàng:

```powershell
# Check Java
java -version

# Check Android SDK
adb version
emulator -version

# Check Git
git --version

# Check Gradle (trong thư mục dự án)
.\gradlew --version

# List emulators
emulator -list-avds

# Check connected devices
adb devices
```

Tất cả lệnh trên phải chạy không lỗi!

---

## Tóm tắt quy trình chạy app hàng ngày

```powershell
# 1. Mở PowerShell tại thư mục dự án
cd D:\Projects\FoodMoodDiary_Mobile

# 2. Pull code mới nhất (nếu có)
git pull origin Frontend

# 3. Khởi động emulator (trong cửa sổ riêng)
Start-Process -FilePath "$env:ANDROID_HOME\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"

# 4. Đợi emulator boot xong, kiểm tra
adb devices

# 5. Build và chạy app
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

---

## Tài nguyên hữu ích

- **Android Studio User Guide**: https://developer.android.com/studio/intro
- **Gradle Documentation**: https://docs.gradle.org/
- **Firebase Android Setup**: https://firebase.google.com/docs/android/setup
- **ADB Commands**: https://developer.android.com/studio/command-line/adb

---

## Liên hệ hỗ trợ

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra lại từng bước trong hướng dẫn
2. Tìm lỗi tương tự trong phần "Xử lý lỗi thường gặp"
3. Google lỗi cụ thể với keyword "Android Studio [tên lỗi]"
4. Liên hệ team qua GitHub Issues: https://github.com/quynh2204/FoodMoodDiary_Mobile/issues

---

**Chúc bạn setup thành công! 🎉**
