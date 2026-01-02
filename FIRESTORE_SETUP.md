# Firestore Vietnamese Meals Setup

## 📋 Bước 1: Cài đặt Firebase Admin SDK

```bash
pip install firebase-admin
```

## 🔑 Bước 2: Tạo Service Account Key

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project **FoodMoodDiary**
3. Vào **Project Settings** (⚙️) > **Service Accounts**
4. Click **Generate New Private Key**
5. Tải file JSON về và đổi tên thành `serviceAccountKey.json`
6. Đặt file vào thư mục `scripts/`

## 🚀 Bước 3: Upload dữ liệu lên Firestore

```bash
cd /Users/haphuongquynh/Desktop/Mobile/scripts
python3 upload_meals_to_firestore.py
```

## ✅ Kết quả mong đợi

```
Starting Vietnamese meals upload to Firestore...

✓ Uploaded: Phở bò (ID: pho_bo)
✓ Uploaded: Bún bò Huế (ID: bun_bo_hue)
✓ Uploaded: Bún riêu cua (ID: bun_rieu)
...
✓ Uploaded: Bánh bèo Huế (ID: banh_beo)

==================================================
Upload complete!
Success: 22
Errors: 0
==================================================
```

## 📊 Cấu trúc Firestore

```
vietnameseMeals (collection)
  ├── pho_bo (document)
  │   ├── name: "Phở bò"
  │   ├── category: "Món nước"
  │   ├── youtubeUrl: "https://..."
  │   ├── imageUrl: "https://..."
  │   ├── calories: 350
  │   ├── description: "..."
  │   └── tags: ["breakfast", "popular", "noodles"]
  │
  ├── bun_bo_hue (document)
  │   └── ...
```

## 🔥 Kiểm tra trên Firebase Console

1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn **Firestore Database**
3. Xem collection `vietnameseMeals`
4. Kiểm tra dữ liệu đã được upload

## 📱 Trong App

Sau khi upload:
1. Chạy lại app: `./gradlew installDebug`
2. Vào tab **Discovery** (Khám phá)
3. Dữ liệu món ăn sẽ tự động load từ Firestore

## ➕ Thêm món ăn mới

Để thêm món ăn mới sau này:

### Cách 1: Qua Firebase Console (Đơn giản nhất)
1. Vào Firestore Database
2. Click vào collection `vietnameseMeals`
3. Click **Add Document**
4. Điền các fields:
   - Document ID: `mon_moi_id`
   - name: "Tên món"
   - category: "Món nước" / "Món khô" / "Tráng miệng"
   - youtubeUrl: "https://..."
   - imageUrl: "https://..."
   - calories: 400
   - description: "Mô tả"
   - tags: ["tag1", "tag2"]

### Cách 2: Thêm vào script và chạy lại
1. Mở file `upload_meals_to_firestore.py`
2. Thêm món mới vào list `meals`
3. Chạy lại script

## 🛠️ Troubleshooting

### Lỗi: "Could not find serviceAccountKey.json"
→ Kiểm tra file `serviceAccountKey.json` có trong thư mục `scripts/`

### Lỗi: "Permission denied"
→ Kiểm tra Firestore Rules cho phép write:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /vietnameseMeals/{mealId} {
      allow read: if true;
      allow write: if request.auth != null;
    }
  }
}
```

### App không hiển thị món ăn
→ Kiểm tra:
1. Firestore có dữ liệu chưa
2. Internet connection
3. Logcat: `adb logcat | grep Vietnamese`
