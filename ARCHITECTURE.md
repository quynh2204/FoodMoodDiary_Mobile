# 🏗️ FoodMoodDiary Architecture

> Tài liệu kiến trúc gọn, bám đúng mock UI hiện tại để backend phát triển đúng luồng chức năng.

## Mục tiêu
- Chuẩn hóa hợp đồng giữa UI (mock) và backend: dữ liệu, dòng chảy, trạng thái.
- Mô tả nhanh kiến trúc lớp (Clean Architecture: UI ↔ ViewModel ↔ Use Case ↔ Repository ↔ Data Source).
- Chỉ giữ nội dung cần thiết từ RULE.md và BACKEND_INTEGRATION_STATUS.md, bỏ trùng lặp.

## Stack chính
- **UI**: Jetpack Compose + Material3, Navigation, Coil, MPAndroidChart.
- **DI**: Hilt.
- **Async**: Coroutines + Flow.
- **Data**: Room (offline-first), Firebase (Auth/Firestore/Storage), Retrofit (TheMealDB), DataStore (preferences), Sensors (light, accelerometer).
- **Maps**: Google Maps SDK + Fused Location.

## Clean Architecture
- **Presentation (UI + ViewModel)**: Compose screens quan sát StateFlow; xử lý intent, hiện loading/empty/error.
- **Domain (Use Cases + Models)**: Business rules; ViewModel gọi qua use case thay vì repository trực tiếp.
- **Data (Repositories + Data Sources)**: Repository là nguồn sự thật, hợp nhất Room, Firebase, API.

## Hợp đồng chức năng theo mock UI
### 1) Map Screen
- **Dữ liệu**: `FoodEntry` có `location(lat, lng, address?)`, `photoUrl/localPhotoPath`, `foodName`, `notes`, `timestamp`, `moodColor`.
- **Luồng**: Lấy toàn bộ entries có location → cluster markers → click marker mở bottom sheet chi tiết.
- **Trạng thái**: loading spinner (chỉ khi fetch), permission denied view, optional heatmap toggle, map type toggle, refresh.
- **Backend yêu cầu**: Repository cung cấp Flow<List<FoodEntry>> đã có location; cần trường timestamp, moodColor để lọc/heatmap.

### 2) Discovery Screen (TheMealDB)
- **Tabs**: Gợi ý, Khám phá món ăn, Món đã lưu.
- **Dữ liệu**: `Meal(id, name, area, category, thumbUrl, youtubeUrl?, isFavorite)`.
- **Tác vụ**: random meal, search by name, filter by category/area, toggle favorite, open YouTube.
- **Trạng thái**: loading, error + retry, empty.
- **Backend yêu cầu**: MealRepository với các hàm: getRandomMeal(), searchMealsByName(q), filterByCategory(c), filterByArea(a), toggleFavorite(meal). Favorites lưu local (Room) + sync.

### 3) Statistics Screen
- **Tabs**: Overview, Charts, Calendar, AI Insights.
- **Models**:
  - `MoodTrendPoint(date: Long, averageMoodScore: Float, entryCount: Int)`
  - `FoodFrequency(foodName: String, count: Int, averageMoodScore: Float)`
  - `MealDistribution(mealType: MealType, count: Int, percentage: Float)`
  - `ColorDistribution(colorValue: Int, colorName: String, count: Int, percentage: Float)`
  - `WeeklySummary(weekStartDate: Long, totalEntries: Int, averageMoodScore: Float, mostFrequentFood: String?, dominantColor: Int, streak: Int)`
  - `Insight(id: String, title: String, description: String, type: InsightType, actionable: Boolean = false, icon: String = "lightbulb")`
- **Luồng**: DateRange (7/30/90/365/All) → load trend/top foods/distributions/insights/weekly summary → UI hiển thị chart, quick stats, calendar dots, insight cards, empty-state nếu thiếu data.
- **Backend yêu cầu**: StatisticsRepository trả Flow cho từng tập dữ liệu trên; tính streak server-side nếu có.

### 4) Profile & Settings (ModernProfileScreen)
- **Dữ liệu**: User(displayName/email, notificationsEnabled, themePreference, streak), entry counts, export actions.
- **Tác vụ**: toggle notifications, set theme (Light/Dark/Auto), clear entries, export CSV/PDF, logout.
- **Backend yêu cầu**: ProfileRepository cung cấp user profile, update settings, export trigger endpoints or local export helper.

### 5) Camera/Add Entry
- **Luồng**: Capture photo → color analysis → location attach → save entry (Room + Firebase) → optional upload to Storage.
- **Dữ liệu**: `FoodEntry(id, foodName, notes, moodColor, timestamp, photoUrl/localPhotoPath, location?, rating?, tags?)`.

## Use Cases (cần đủ, không gọi repo trực tiếp)
- **Statistics**: GetMoodTrend, GetTopFoods, GetMealDistribution, GetColorDistribution, GenerateInsights, GetWeeklySummary.
- **Meal/Discovery**: GetRandomMeal, SearchMealsByName, FilterMealsByCategory, FilterMealsByArea, GetFavoriteMeals, ToggleFavoriteMeal.
- **Profile**: GetUserProfile, UpdateUserProfile, GetStreakCount, UpdateNotifications, UpdateThemePreference.
- **Map/Entry**: GetEntriesWithLocation, RefreshEntries.

## Data Flow mẫu (Statistics)
UI (StatisticsScreen) → ViewModel (state: loading/empty/error/data) → UseCases → StatisticsRepository → Room (analytics) ± Firebase for sync → emit Flow to UI.

## Trạng thái & Empty states
- Luôn có 3 trạng thái: loading, data, empty/error. UI mock đã có spinner/placeholder; backend cần trả về Flow/Result rõ ràng.

## Testing & Hợp đồng
- Cần fixtures JSON mẫu cho: MoodTrend, TopFoods, MealDistribution, Insights, Meal list.
- Integration tests: repo ↔ DAO/API; UI preview có thể dùng fake repos.

## Theme & Palette
- Palette PastelGreen/Black giữ nguyên. Colors ở ui/theme/Color.kt.

## Ghi chú triển khai backend
- Firebase: Auth, Firestore (entries, users), Storage (photos).
- Room: cache entries, favorites, stats aggregation.
- Retrofit: TheMealDB endpoints (random, search, filter category/area, lookup id).
- Map: ensure location fields non-null when displaying markers; provide graceful empty list.

## Liên quan file
- Kế thừa nội dung từ RULE.md (stack, kiến trúc) và BACKEND_INTEGRATION_STATUS.md (tính năng đã nối), bỏ phần trùng lặp dài.
- TOPIC_MAPPING.md: đã archive, không còn tham chiếu.
