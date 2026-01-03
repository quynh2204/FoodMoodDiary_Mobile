# Hướng dẫn chạy FoodMoodDiary - Tóm tắt nhanh

## 📋 Điều kiện tiên quyết
- ✅ Đã cài đặt Android Studio và Android SDK
- ✅ Đã cấu hình biến môi trường ANDROID_HOME
- ✅ Đã clone project về máy
- ✅ Đã tạo AVD (Android Virtual Device)

> ⚠️ **Nếu bạn chưa có các điều kiện trên**, vui lòng xem hướng dẫn setup đầy đủ tại:
> ### 👉 [WINDOWS_SETUP_GUIDE.md](./WINDOWS_SETUP_GUIDE.md) - Hướng dẫn cài đặt từ đầu
> 
> Hướng dẫn bao gồm:
> - Cài đặt JDK 17
> - Cài đặt Android Studio và SDK
> - Cấu hình biến môi trường
> - Clone dự án từ GitHub
> - Tạo và cấu hình AVD (Android Virtual Device)
> - Cấu hình Firebase
> 
> **Thời gian:** ~1-2 giờ cho lần setup đầu tiên

---

## 🚀 Các bước chạy app (Chi tiết)

### Bước 1: Mở PowerShell tại thư mục dự án
```powershell
cd D:\Test\FoodMoodDiary_Mobile
```

### Bước 2: Kiểm tra danh sách AVD có sẵn
```powershell
D:\SDK\emulator\emulator.exe -list-avds
```
**Kết quả:** Sẽ hiện danh sách các emulator (ví dụ: Small_Phone, FoodMoodEmulator, etc.)

### Bước 3: Khởi động emulator
```powershell
Start-Process -FilePath "D:\SDK\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"
```
**Lưu ý:** Thay `Small_Phone` bằng tên AVD của bạn

**Thời gian chờ:** 30-60 giây để emulator boot lần đầu

### Bước 4: Kiểm tra emulator đã sẵn sàng
```powershell
D:\SDK\platform-tools\adb.exe devices
```
**Kết quả mong đợi:**
```
List of devices attached
emulator-5554   device
```

### Bước 5: Sửa lỗi compilation (nếu có sau khi merge code)

Kiểm tra và sửa file `FoodMoodDiaryNavigation.kt`:
- Xóa parameters không tồn tại trong các composable
- Xóa duplicate Screen declarations

### Bước 6: Build debug APK
```powershell
.\gradlew assembleDebug
```
**Thời gian:** ~1-2 phút lần đầu

**Nếu gặp lỗi "Room schema mismatch"**, thực hiện Bước 7 trước.

### Bước 7: Xóa dữ liệu cũ của app (quan trọng sau khi merge)
```powershell
D:\SDK\platform-tools\adb.exe shell pm clear com.haphuongquynh.foodmooddiary
```
**Kết quả:** `Success`

**Lý do:** Database schema thay đổi sau khi merge backend code

### Bước 8: Install app vào emulator
```powershell
.\gradlew installDebug
```
**Thời gian:** ~10-20 giây

### Bước 9: Khởi động app
```powershell
D:\SDK\platform-tools\adb.exe shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### Bước 10: Cấp quyền tự động (tùy chọn)
```powershell
D:\SDK\platform-tools\adb.exe shell pm grant com.haphuongquynh.foodmooddiary android.permission.CAMERA
D:\SDK\platform-tools\adb.exe shell pm grant com.haphuongquynh.foodmooddiary android.permission.ACCESS_FINE_LOCATION
D:\SDK\platform-tools\adb.exe shell pm grant com.haphuongquynh.foodmooddiary android.permission.ACCESS_COARSE_LOCATION
D:\SDK\platform-tools\adb.exe shell pm grant com.haphuongquynh.foodmooddiary android.permission.READ_EXTERNAL_STORAGE
```

### Bước 11: Kiểm tra app đang chạy
```powershell
D:\SDK\platform-tools\adb.exe shell dumpsys window | Select-String "mCurrentFocus"
```
**Kết quả mong đợi:**
```
mCurrentFocus=Window{...com.haphuongquynh.foodmooddiary/.MainActivity}
```

---

## ⚡ Lệnh gộp (All-in-one)

### Lần đầu tiên chạy:
```powershell
# 1. Khởi động emulator (chạy trong cửa sổ riêng)
Start-Process -FilePath "D:\SDK\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"

# 2. Chờ emulator boot (45 giây)
Start-Sleep -Seconds 45

# 3. Xóa data cũ (quan trọng!)
D:\SDK\platform-tools\adb.exe shell pm clear com.haphuongquynh.foodmooddiary

# 4. Build, install và chạy
.\gradlew installDebug; D:\SDK\platform-tools\adb.exe shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity

# 5. Cấp quyền (nếu cần)
D:\SDK\platform-tools\adb.exe shell pm grant com.haphuongquynh.foodmooddiary android.permission.CAMERA
D:\SDK\platform-tools\adb.exe shell pm grant com.haphuongquynh.foodmooddiary android.permission.ACCESS_FINE_LOCATION
```

### Lần sau (khi emulator đã chạy):
```powershell
# Chỉ cần build và install
.\gradlew installDebug; D:\SDK\platform-tools\adb.exe shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

---

## 🐛 Xử lý lỗi thường gặp

### Lỗi 1: "Room cannot verify the data integrity"
**Nguyên nhân:** Database schema thay đổi sau khi merge code

**Giải pháp:**
```powershell
D:\SDK\platform-tools\adb.exe shell pm clear com.haphuongquynh.foodmooddiary
.\gradlew installDebug
D:\SDK\platform-tools\adb.exe shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### Lỗi 2: "No devices/emulators found"
**Nguyên nhân:** Emulator chưa khởi động hoặc chưa boot xong

**Giải pháp:**
```powershell
# Kiểm tra emulator
D:\SDK\platform-tools\adb.exe devices

# Nếu rỗng, đợi thêm hoặc restart emulator
```

### Lỗi 3: App crash ngay khi mở
**Giải pháp:**
```powershell
# Xem log chi tiết
D:\SDK\platform-tools\adb.exe logcat -d | Select-String "AndroidRuntime|FATAL" | Select-Object -Last 20

# Thường là do thiếu file google-services.json hoặc database error
# → Clear data và chạy lại
```

### Lỗi 4: Compilation errors
**Nguyên nhân:** Conflict sau khi merge branches

**Giải pháp:** Sửa các lỗi trong file được báo, thường là:
- Parameters không tồn tại
- Duplicate declarations
- Import thiếu

### Lỗi 5: "adb: command not found"
**Giải pháp:** Dùng đường dẫn đầy đủ:
```powershell
D:\SDK\platform-tools\adb.exe [command]
```

Hoặc thêm vào PATH: `%ANDROID_HOME%\platform-tools`

---

## 📊 Workflow hàng ngày

```powershell
# Morning routine
cd D:\Test\FoodMoodDiary_Mobile
git pull origin Frontend

# Khởi động emulator (nếu chưa chạy)
Start-Process -FilePath "D:\SDK\emulator\emulator.exe" -ArgumentList "-avd", "Small_Phone"

# Chờ 30s, sau đó build & run
Start-Sleep -Seconds 30
.\gradlew installDebug; D:\SDK\platform-tools\adb.exe shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

---

## 🔍 Debug commands

### Xem log realtime:
```powershell
D:\SDK\platform-tools\adb.exe logcat | Select-String "FoodMoodDiary"
```

### Xem log crash:
```powershell
D:\SDK\platform-tools\adb.exe logcat -d | Select-String "AndroidRuntime" | Select-Object -Last 30
```

### Screenshot từ emulator:
```powershell
D:\SDK\platform-tools\adb.exe shell screencap -p /sdcard/screenshot.png
D:\SDK\platform-tools\adb.exe pull /sdcard/screenshot.png
```

### Restart app:
```powershell
D:\SDK\platform-tools\adb.exe shell am force-stop com.haphuongquynh.foodmooddiary
D:\SDK\platform-tools\adb.exe shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
```

### Uninstall app:
```powershell
D:\SDK\platform-tools\adb.exe uninstall com.haphuongquynh.foodmooddiary
```

---

## 💡 Tips

1. **Build nhanh hơn:** Sau lần đầu, Gradle sẽ cache → build chỉ còn ~10-20s

2. **Emulator chậm:** 
   - Tăng RAM trong AVD Manager (khuyến nghị 2048MB)
   - Chọn Graphics: Hardware - GLES 2.0

3. **Hot reload:** Thay đổi UI nhỏ → chỉ cần chạy `.\gradlew installDebug`

4. **Multiple emulators:** Chỉ định device cụ thể:
   ```powershell
   D:\SDK\platform-tools\adb.exe -s emulator-5554 [command]
   ```

5. **Save snapshot:** Để boot nhanh lần sau, save state emulator trước khi tắt

---

## 📚 Tài liệu tham khảo

- **Setup đầy đủ:** [WINDOWS_SETUP_GUIDE.md](./WINDOWS_SETUP_GUIDE.md)
- **VS Code setup:** [VSCODE_SETUP.md](./VSCODE_SETUP.md)
- **ADB commands:** https://developer.android.com/studio/command-line/adb

---

## ✅ Checklist nhanh

Trước khi chạy app, đảm bảo:
- [ ] Emulator đã boot xong (`adb devices` hiện `device`)
- [ ] File `google-services.json` đã có trong `app/`
- [ ] Đã clear data nếu vừa merge code mới
- [ ] Đã cấp đủ permissions (Camera, Location, Storage)

---

**Thời gian tổng:** ~3-5 phút từ lúc mở PowerShell đến app chạy (lần đầu)

**Lần sau:** ~1-2 phút (nếu emulator đã chạy)
