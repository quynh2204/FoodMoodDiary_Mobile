# VS Code Setup cho FoodMoodDiary Android

Hướng dẫn nhanh để chạy dự án Android trong VS Code.

## 📦 Extensions cần cài

Mở VS Code → `Ctrl+Shift+X` → tìm và cài:

1. **Android iOS Emulator** - DiemasMichiels.emulate
2. **Kotlin Language** - mathiasfrohlich.Kotlin  
3. **Gradle for Java** - vscjava.vscode-gradle

VS Code sẽ tự gợi ý cài các extension trong file `.vscode/extensions.json`.

## ⚙️ Cấu hình tự động

Dự án đã có sẵn:
- ✅ `.vscode/tasks.json` - Gradle tasks
- ✅ `.vscode/settings.json` - Cấu hình workspace
- ✅ `.vscode/extensions.json` - Extensions khuyến nghị

## 🚀 Chạy app nhanh

### Cách 1: Dùng Tasks (Khuyến nghị)
1. Nhấn `Ctrl+Shift+P`
2. Gõ: `Tasks: Run Task`
3. Chọn: **Build and Run**

### Cách 2: Terminal
```powershell
# Mở Terminal: Ctrl+`
.\gradlew installDebug; adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

## 📱 Quản lý Emulator

### Khởi động emulator

**Cách 1: Dùng Extension**
- `Ctrl+Shift+P` → `Emulator: Start` → Chọn `Small_Phone`

**Cách 2: Terminal**
```powershell
emulator -avd Small_Phone
```

**Cách 3: Task**
- `Ctrl+Shift+P` → `Tasks: Run Task` → `Start Emulator (Small_Phone)`

### Kiểm tra emulator
```powershell
adb devices
```

## 🔧 Tasks có sẵn

Nhấn `Ctrl+Shift+P` → `Tasks: Run Task`:

| Task | Mô tả |
|------|-------|
| **Build Debug APK** | Build app (không install) |
| **Install Debug APK** | Build và install |
| **Build and Run** | Build, install và khởi động |
| **Clean Build** | Xóa build cũ |
| **Clean Build and Run** | Clean, build, install và chạy |
| **View Logcat** | Xem log app |
| **Clear Logcat** | Xóa log |
| **List Devices** | Danh sách devices |
| **Uninstall App** | Gỡ app |
| **Start Emulator** | Khởi động emulator |
| **Kill All Emulators** | Tắt tất cả emulator |

## 🎯 Workflow hàng ngày

```powershell
# 1. Mở dự án
code .

# 2. Khởi động emulator (Ctrl+Shift+P → Tasks: Run Task → Start Emulator)

# 3. Chờ boot xong (~30s), kiểm tra
adb devices

# 4. Build và chạy (Ctrl+Shift+P → Tasks: Run Task → Build and Run)

# 5. Xem log (Ctrl+Shift+P → Tasks: Run Task → View Logcat)
```

## ⌨️ Shortcuts hữu ích

| Phím | Chức năng |
|------|-----------|
| `Ctrl+Shift+P` | Command Palette |
| `Ctrl+` ` | Toggle Terminal |
| `Ctrl+Shift+B` | Build Task |
| `Ctrl+K Ctrl+S` | Keyboard Shortcuts |
| `Ctrl+,` | Settings |
| `Ctrl+Shift+E` | Explorer |
| `Ctrl+Shift+F` | Search |

## 🐛 Debug

### Xem log của app
```powershell
# Trong Terminal VS Code
adb logcat | Select-String "FoodMoodDiary"
```

### Xem crash log
```powershell
adb logcat -s AndroidRuntime:E
```

### Clear data app
```powershell
adb shell pm clear com.haphuongquynh.foodmooddiary
```

### Reinstall app
```powershell
.\gradlew uninstallDebug installDebug
```

## 💡 Tips

1. **Build nhanh hơn:** Sửa `gradle.properties`:
   ```properties
   org.gradle.jvmargs=-Xmx4096m
   org.gradle.parallel=true
   org.gradle.caching=true
   ```

2. **Terminal múltiple:** `Ctrl+Shift+` ` để tạo terminal mới

3. **Emulator snapshot:** Lưu trạng thái emulator để boot nhanh hơn

4. **Hot reload:** Sau khi thay đổi code, chỉ cần chạy task **Build and Run**

## ❓ Troubleshooting

### Lỗi "gradlew: command not found"
```powershell
# Chạy với đường dẫn đầy đủ
.\gradlew installDebug
```

### Emulator không boot
```powershell
# Thử cold boot
emulator -avd Small_Phone -no-snapshot-load
```

### App không cài được
```powershell
# Gỡ app cũ trước
adb uninstall com.haphuongquynh.foodmooddiary
.\gradlew installDebug
```

### Task không chạy
- Đảm bảo đã mở workspace (folder dự án)
- Kiểm tra file `.vscode/tasks.json` tồn tại

---

**Xem hướng dẫn đầy đủ tại:** [WINDOWS_SETUP_GUIDE.md](./WINDOWS_SETUP_GUIDE.md)
