# 🚀 HƯỚNG DẪN CHẠY TRÊN VS CODE (macOS + Windows)

## 0. Yêu cầu
- Java 21 (JDK). Kiểm tra bằng `java -version` phải ra 21.x.
- Android SDK + Platform Tools (adb) trong PATH.
- VS Code với Kotlin/Gradle plugin (khuyến nghị) và Android Studio để tạo emulator.

## 1. Kiểm tra Java
- macOS (zsh):
```bash
java -version
```
- Windows (PowerShell):
```powershell
java -version
```

## 2. Chuẩn bị thư mục dự án
- macOS: `cd /Users/haphuongquynh/Desktop/Mobile`
- Windows: `cd D:\FoodMoodDiary_Mobile`

## 3. Build hoặc chạy từ VS Code Task
- Build nhanh (không cài):
```bash
./gradlew assembleDebug
```
- Cài và mở app (dùng adb, thiết bị đã nối USB hoặc emulator đang chạy):
```bash
./gradlew installDebug
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```
- Trong VS Code có sẵn task: `Run FoodMoodDiary` (chạy `./gradlew installDebug` rồi start Activity).

## 4. Kết nối thiết bị / emulator
- Thiết bị thật: bật USB Debugging, xác nhận fingerprint, kiểm tra `adb devices` thấy trạng thái `device`.
- Emulator: mở Android Studio > AVD Manager > Start; hoặc dùng lệnh `emulator -list-avds` rồi `emulator -avd <name>`.

## 5. Cài APK thủ công (nếu cần)
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## 6. Lưu ý Google Maps API Key
- App chạy được nhưng Map sẽ trắng nếu thiếu key.
- Thêm key vào `local.properties`:
```
MAPS_API_KEY=your-key-here
```

## 7. Troubleshooting
- JAVA_HOME macOS (zsh):
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```
- JAVA_HOME Windows (PowerShell):
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.x.x"
$env:Path += ";$env:JAVA_HOME\bin"
```
- SDK location not found: chỉnh `local.properties` (đường dẫn đúng OS).
```
# macOS
sdk.dir=/Users/your-username/Library/Android/sdk

# Windows
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```
- Build lỗi phụ thuộc:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```
