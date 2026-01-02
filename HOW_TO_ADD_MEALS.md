# ➕ Hướng dẫn thêm món ăn Việt Nam mới

## Cách nhanh nhất: Firebase Console (Không cần code)

### Bước 1: Vào Firebase Console
1. Truy cập https://console.firebase.google.com/
2. Chọn project: **FoodMoodDiary**
3. Click vào **Firestore Database** ở sidebar

### Bước 2: Thêm document mới
1. Click vào collection **vietnameseMeals**
2. Click nút **Add document** ở góc trên
3. Điền thông tin:

```
Document ID: com_ga_xoi_mo    (id món ăn, dùng snake_case)

Fields:
┌────────────┬────────┬────────────────────────────────────────────┐
│ Field      │ Type   │ Value                                      │
├────────────┼────────┼────────────────────────────────────────────┤
│ name       │ string │ Cơm gà xối mỡ                             │
│ category   │ string │ Món khô                                    │
│ youtubeUrl │ string │ https://www.youtube.com/watch?v=XXXXX     │
│ imageUrl   │ string │ https://example.com/image.jpg             │
│ calories   │ number │ 500                                        │
│ description│ string │ Cơm gà Hải Nam truyền thống               │
│ tags       │ array  │ ["chicken", "rice", "popular"]            │
└────────────┴────────┴────────────────────────────────────────────┘
```

### Bước 3: Save
Click **Save** → Món ăn mới sẽ xuất hiện ngay trong app!

---

## Chi tiết các fields:

### 📌 `name` (string) - **BẮT BUỘC**
Tên món ăn hiển thị trong app
- Ví dụ: "Phở bò", "Bánh mì thịt"

### 📂 `category` (string) - **BẮT BUỘC**
Danh mục món ăn, chỉ chọn 1 trong 3:
- `"Món nước"` - Phở, bún, mì...
- `"Món khô"` - Cơm, bánh mì, gỏi cuốn...
- `"Tráng miệng"` - Chè, bánh ngọt...

### 🎥 `youtubeUrl` (string) - **BẮT BUỘC**
Link video YouTube hướng dẫn nấu món
- Format: `https://www.youtube.com/watch?v=VIDEO_ID`
- Tìm video: Vào YouTube → Search "cách làm [tên món]" → Copy link

### 🖼️ `imageUrl` (string) - **BẮT BUỘC**
Link ảnh món ăn chất lượng cao
- Nguồn đề xuất:
  - Unsplash: https://unsplash.com/s/photos/vietnamese-food
  - Pexels: https://www.pexels.com/search/vietnamese%20food/
  - Google Images (chọn "Usage rights" → "Creative Commons")
- Format: Link trực tiếp đến file ảnh (.jpg, .png)

### 🔥 `calories` (number) - Tùy chọn
Số calories ước tính
- Ví dụ: 350, 500, 180
- Để trống nếu không biết (mặc định: 0)

### 📝 `description` (string) - Tùy chọn
Mô tả ngắn gọn về món ăn
- Ví dụ: "Món phở truyền thống của Việt Nam với nước dùng thơm ngon"
- Để trống nếu không có

### 🏷️ `tags` (array of strings) - Tùy chọn
Các từ khóa liên quan (tiếng Anh)
- Ví dụ: `["breakfast", "popular", "noodles", "beef"]`
- Tags gợi ý:
  - Thời gian: `breakfast`, `lunch`, `dinner`, `snack`
  - Loại: `noodles`, `rice`, `soup`, `seafood`, `beef`, `pork`, `chicken`
  - Vùng miền: `northern-vietnam`, `central-vietnam`, `southern-vietnam`
  - Tính chất: `spicy`, `sweet`, `healthy`, `street-food`, `traditional`

---

## ✅ Ví dụ đầy đủ

```
Document ID: banh_cuon

Fields:
name:        Bánh cuốn Thanh Trì
category:    Món nước
youtubeUrl:  https://www.youtube.com/watch?v=abc123
imageUrl:    https://images.unsplash.com/photo-xyz
calories:    200
description: Bánh cuốn mỏng dai với nhân thịt băm thơm ngon
tags:        ["breakfast", "hanoi", "northern-vietnam", "pork", "steamed"]
```

---

## 🎨 Tìm ảnh chất lượng cao

### Unsplash (Miễn phí, không cần credit)
```
1. Vào https://unsplash.com/
2. Search: "vietnamese food" hoặc tên món (tiếng Anh)
3. Click ảnh → Click chuột phải → "Copy image address"
4. Paste vào field imageUrl
```

### Google Images
```
1. Vào https://images.google.com/
2. Search tên món
3. Tools → Usage rights → Creative Commons licenses
4. Click ảnh → View image → Copy URL
```

---

## 🚀 Kiểm tra trong app

Sau khi thêm món mới:
1. Mở app trên máy ảo
2. Vào tab **Discovery** (Khám phá)
3. Món mới sẽ xuất hiện ngay lập tức! ✨

**Lưu ý:** Không cần build lại app, dữ liệu load real-time từ Firestore!

---

## ⚠️ Lỗi thường gặp

### Món ăn không hiển thị
- ✅ Kiểm tra `category` có đúng 1 trong 3: "Món nước", "Món khô", "Tráng miệng"
- ✅ Kiểm tra `name`, `category`, `youtubeUrl`, `imageUrl` không để trống
- ✅ Kiểm tra Document ID không trùng với món khác
- ✅ Reload app: Force stop → Open lại

### Ảnh không load
- ✅ Kiểm tra `imageUrl` là link trực tiếp đến file ảnh (kết thúc .jpg hoặc .png)
- ✅ Thử mở link trong browser xem có load được không

### YouTube không mở được
- ✅ Kiểm tra format: `https://www.youtube.com/watch?v=VIDEO_ID`
- ✅ Video không bị xóa/private

---

## 📞 Cần trợ giúp?

1. Kiểm tra log: `adb logcat | grep Vietnamese`
2. Xem Firestore Rules có cho phép read không
3. Test thử query trên Firebase Console
