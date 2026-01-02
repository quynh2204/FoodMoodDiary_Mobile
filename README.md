# Food Mood Diary

> **Ứng dụng Nhật ký Ăn uống & Cảm xúc - Kết nối giữa thực phẩm và tâm trạng**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-SDK%2026+-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://openjdk.org/)
[![Firebase](https://img.shields.io/badge/Firebase-Enabled-yellow.svg)](https://firebase.google.com)

---

## 📋 MỤC LỤC

- [Giới thiệu](#-giới-thiệu)
- [Tính năng](#-tính-năng)
- [Technical Stack](#-technical-stack)
- [Kiến trúc](#-kiến-trúc)
- [Screenshots](#-screenshots)
- [Cài đặt](#-cài-đặt)
- [Tài liệu](#-tài-liệu)
- [Đóng góp](#-đóng-góp)
- [License](#-license)

---

## 🎯 GIỚI THIỆU

**Food Mood Diary** là ứng dụng mobile Android giúp người dùng theo dõi và khám phá mối quan hệ giữa chế độ ăn uống và trạng thái cảm xúc hằng ngày.

### Điểm nổi bật

- 📸 **Visual-First**: Chụp hoặc chọn ảnh món ăn đẹp mắt
- 🎨 **Smart Color Analysis**: Tự động phân tích màu sắc để gợi ý tâm trạng (Palette API)
- ☁️ **Cloud Sync**: Đồng bộ dữ liệu đa thiết bị qua Firebase
- 📊 **AI Insights**: Phân tích xu hướng ăn uống theo cảm xúc
- 🌍 **Discovery**: Khám phá món ăn mới từ API bên ngoài

### Đối tượng người dùng

1. **Gen Z / Foodies**: Thích chụp ảnh đồ ăn, quan tâm đến aesthetic
2. **Emotional Eaters**: Muốn hiểu và điều chỉnh thói quen ăn uống theo cảm xúc
3. **Người bận rộn**: Cần ghi chép nhanh gọn về lịch sử thực đơn

---

## ✨ TÍNH NĂNG

### 🔐 Quản lý Tài khoản
- Đăng ký/Đăng nhập bằng Email/Password
- Đăng nhập nhanh với Google Sign-In
- Đồng bộ dữ liệu tự động qua Firebase
- Profile với streak counter

### 📝 Ghi Nhật ký Food & Mood
- **Chụp ảnh** từ camera hoặc **chọn từ thư viện**
- **Phân tích màu sắc** tự động bằng Palette API
- **Gợi ý tâm trạng** dựa trên màu sắc món ăn
- Thông tin đầy đủ: Tên món, Cảm xúc (emoji), Loại bữa, Rating, Notes

### 📊 Thống kê & Báo cáo
- **Overview Tab**:
  - Summary cards (Total entries, Streak, Most common mood)
  - Mood Calendar View (color-coded by mood)
- **Charts Tab**:
  - Line Chart: Mood trend overtime
  - Bar Chart: Food-Mood frequency
  - Pie Chart: Meal type & Color palette distribution
- **Insights Tab**:
  - AI-generated patterns
  - "Bạn thường ăn gì khi buồn?"
  - "Khung giờ nào bạn vui nhất?"
  - Suggestions dựa trên data

### 🌍 Discovery (External API)
- "Hôm nay ăn gì?" 
- Save meal 


### 🎨 Advanced Features

- **Smooth Animations**: Property Animation, Lottie animations
- **Content Provider**: Chia sẻ data sang app khác
- **Offline-First**: Hoạt động tốt cả khi không có mạng

---

## 🚀 TECHNICAL STACK

**Kotlin** • **Jetpack Compose** • **Material3** • **Hilt** • **Room** • **Firebase** • **Coroutines & Flow**

> 📖 Xem chi tiết trong [ARCHITECTURE.md](ARCHITECTURE.md)

### Kiến trúc
- **Clean Architecture** (Data/Domain/Presentation layers)
- **MVVM** pattern với Use Cases
- **Offline-First** strategy (Room ↔ Firebase sync)
- **Dependency Injection** với Hilt

---

## 📸 SCREENSHOTS

### 1. Camera Capture & AI Mood Detection
![Add Entry - Camera](docs/screenshots/add_entry_camera.png)

### 2. Entry Form
![Add Entry - Form](docs/screenshots/add_entry_form.png)


### 4. Home Views (Grid/List/Calendar)
![Home Grid](docs/screenshots/home_grid.png)
![Home List](docs/screenshots/home_list.png)
![Home Calendar](docs/screenshots/home_calendar.png)

### 5. Statistics
![Statistics Overview](docs/screenshots/statistics_overview.png)
![Statistics Charts](docs/screenshots/statistics_charts.png)
![Statistics Insights](docs/screenshots/statistics_insights.png)

### 6. Profile & Settings
![Profile](docs/screenshots/profile.png)

---

## 🛠️ CÀI ĐẶT

### Prerequisites
- Android Studio Ladybug or newer
- Java 21 LTS
- Android SDK 26-36
- Firebase account

### Quick Start

1. **Clone repository**
   ```bash
   git clone https://github.com/quynh2204/FoodMoodDiary_Mobile.git
   cd FoodMoodDiary_Mobile
   ```

2. **Setup Firebase**
   - Create Firebase project tại [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json` và đặt vào `app/`
   - Enable Authentication, Firestore, Storage, Messaging


4. **Sync & Build**
   ```bash
   ./gradlew build
   ```

5. **Run**
   - Connect device or start emulator
   - Click Run ▶️ in Android Studio

---

## 📚 TÀI LIỆU


### Android Topics Covered

2. ✅ Threading & Background Tasks - Coroutines, WorkManager, Services
3. ✅ Multimedia - CameraX, Palette API, Image processing
4. ✅ Content Provider - Share data to other apps
5. ✅ Jetpack Compose - Modern declarative UI
7. ✅ RESTful API - Retrofit with external API
8. ✅ Performance Optimization - Profiler, LeakCanary, indexes
9. ✅ Animation - Property, Compose, Lottie animations
10. ✅ Sensors - Accelerometer, Light sensor

---

## 👥 ĐÓNG GÓP

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

---

## 📄 LICENSE

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 ACKNOWLEDGMENTS

- [Android Jetpack](https://developer.android.com/jetpack) - Modern Android development
- [Firebase](https://firebase.google.com) - Backend as a Service
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - Chart library
- [Lottie](https://airbnb.design/lottie/) - Animation library

---

## 📧 CONTACT

**Ha Phuong Quynh**
- GitHub: [@quynh2204](https://github.com/quynh2204)
- Project Link: [https://github.com/quynh2204/FoodMoodDiary_Mobile](https://github.com/quynh2204/FoodMoodDiary_Mobile)

---

## 🌟 SHOW YOUR SUPPORT

Give a ⭐️ if you like this project!

---

**Built with ❤️ using Kotlin & Jetpack Compose**

**Version**: 1.0  
**Last Updated**: December 17, 2025
