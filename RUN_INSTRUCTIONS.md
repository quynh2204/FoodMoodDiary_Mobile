# 🚀 HƯỚNG DẪN CHẠY TRÊN VS CODE

## Sau khi cài Java 21 xong:

### 1. Mở PowerShell MỚI và verify Java:
```powershell
java -version
# Phải hiển thị: openjdk version "21.x.x"
```

### 2. Build project:
```powershell
cd D:\FoodMoodDiary_Mobile
./gradlew build
```

### 3. Kết nối thiết bị Android:
- Bật USB Debugging trên điện thoại
- Kết nối USB với máy tính

### 4. Cài APK lên thiết bị:
```powershell
# Install debug APK
./gradlew installDebug

# Hoặc nếu có Android SDK:
adb install app\build\outputs\apk\debug\app-debug.apk
```

### 5. Khởi chạy app:
```powershell
# Nếu có adb:
adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity

# Hoặc mở app thủ công trên điện thoại
```

---

## 🎯 LƯU Ý:

### Nếu thiếu Google Maps API Key:
- App vẫn chạy được nhưng Maps sẽ không hoạt động
- Để lấy key: https://console.cloud.google.com/

### Nếu muốn chạy emulator:
- Cài Android Studio để dùng AVD Manager
- Hoặc dùng Genymotion/BlueStacks

---

## 🐛 TROUBLESHOOTING:

### "JAVA_HOME not set":
```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-21.x.x"
$env:Path += ";$env:JAVA_HOME\bin"
```

### "SDK location not found":
Thêm vào `local.properties`:
```
sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
```

### Build thất bại:
```powershell
./gradlew clean
./gradlew build --refresh-dependencies
```
