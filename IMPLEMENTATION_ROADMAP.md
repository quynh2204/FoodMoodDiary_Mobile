# 🗺️ IMPLEMENTATION ROADMAP - FOOD MOOD DIARY

> **Lộ trình chi tiết triển khai dự án trong 4 tuần**

---

## 📅 TUẦN 1: SETUP & CORE ARCHITECTURE (Days 1-7)

### Day 1-2: Project Setup & Dependencies
- [x] ✅ Upgrade Java to 21 LTS
- [x] ✅ Update gradle dependencies (RULE.md & TOPIC_MAPPING.md created)
- [ ] Setup Firebase project
  - [ ] Create Firebase project on console
  - [ ] Download google-services.json
  - [ ] Enable Authentication (Email/Password + Google)
  - [ ] Enable Firestore & Realtime Database
  - [ ] Enable Storage
  - [ ] Enable Cloud Messaging
- [ ] Setup Google Maps API
  - [ ] Enable Maps SDK in Google Cloud Console
  - [ ] Get API key
  - [ ] Add to local.properties

### Day 3-4: Core Architecture Implementation
- [ ] Create base MVVM structure
  - [ ] `data/` package structure
  - [ ] `domain/` package structure
  - [ ] `presentation/` package structure
  - [ ] `di/` package for Hilt modules
- [ ] Setup Room Database
  - [ ] Create `FoodMoodDatabase`
  - [ ] Define entities: `FoodEntryEntity`, `UserEntity`, `MoodColorEntity`
  - [ ] Create DAOs
  - [ ] Database migrations
- [ ] Setup Hilt Dependency Injection
  - [ ] Application class with @HiltAndroidApp
  - [ ] Database module
  - [ ] Network module
  - [ ] Repository module

### Day 5: Firebase Integration
- [ ] Firebase Authentication
  - [ ] `FirebaseAuthService`
  - [ ] Email/Password sign in/up
  - [ ] Google Sign-In integration
- [ ] Firebase Realtime Database
  - [ ] `FirebaseRealtimeService`
  - [ ] Real-time sync listeners
- [ ] Firebase Storage
  - [ ] `FirebaseStorageService`
  - [ ] Image upload/download

### Day 6-7: Navigation & Theme
- [ ] Jetpack Compose Navigation setup
  - [ ] `NavGraph.kt`
  - [ ] `NavigationDestinations.kt`
  - [ ] Bottom Navigation Bar
- [ ] Material Design 3 Theme
  - [ ] `Color.kt`
  - [ ] `Theme.kt`
  - [ ] `Type.kt`
  - [ ] Dark/Light mode support

**Deliverable Tuần 1**: Project skeleton với authentication & navigation hoạt động

---

## 📅 TUẦN 2: CORE FEATURES (Days 8-14)

### Day 8-9: Authentication Screens
- [ ] Sign In Screen
  - [ ] Email/Password form
  - [ ] Google Sign-In button
  - [ ] "Forgot Password" link
- [ ] Sign Up Screen
  - [ ] Registration form
  - [ ] Email verification
- [ ] Authentication ViewModel
  - [ ] Sign in logic
  - [ ] Sign up logic
  - [ ] Auth state management

### Day 10-11: Add Entry Screen (Part 1)
- [ ] Camera Integration (CameraX)
  - [ ] `CameraPreviewScreen.kt`
  - [ ] Capture photo functionality
  - [ ] Permission handling
- [ ] Gallery Integration
  - [ ] Photo picker
  - [ ] Image display
- [ ] Image Processing
  - [ ] `ColorAnalyzer.kt`
  - [ ] Palette API integration
  - [ ] Mood suggestion algorithm

### Day 12-13: Add Entry Screen (Part 2)
- [ ] Entry Form UI
  - [ ] Food name input
  - [ ] Mood selection (with emojis)
  - [ ] Meal type selection (Breakfast/Lunch/Dinner/Snack)
  - [ ] Rating stars
  - [ ] Notes field
- [ ] Location Services
  - [ ] `LocationService.kt`
  - [ ] Get current GPS location
  - [ ] Reverse geocoding (address)
- [ ] Add Entry ViewModel
  - [ ] Form state management
  - [ ] Image upload
  - [ ] Save to Room & Firebase

### Day 14: Home Screen (List/Grid View)
- [ ] Home Screen UI
  - [ ] View mode switcher (Grid/List/Calendar)
  - [ ] Entry cards
  - [ ] Pull-to-refresh
  - [ ] Floating Action Button
- [ ] Home ViewModel
  - [ ] Load entries from Room
  - [ ] Filter by date/mood
  - [ ] Search functionality

**Deliverable Tuần 2**: Có thể chụp ảnh món ăn, thêm entry và xem danh sách

---

## 📅 TUẦN 3: STATISTICS & ADVANCED FEATURES (Days 15-21)

### Day 15-16: Entry Detail Screen
- [ ] Entry Detail UI
  - [ ] Full-size image display
  - [ ] All entry information
  - [ ] Location map (Google Maps)
  - [ ] Edit button
  - [ ] Delete button
  - [ ] Share button
- [ ] Entry Detail ViewModel
  - [ ] Load entry details
  - [ ] Update entry
  - [ ] Delete entry

### Day 17-18: Statistics Screen (Overview Tab)
- [ ] Statistics UI
  - [ ] Tab layout (Overview/Charts/Insights)
  - [ ] Summary cards
    - [ ] Total entries
    - [ ] Current streak
    - [ ] Most common mood
    - [ ] Favorite meal type
- [ ] Mood Calendar View
  - [ ] `MoodCalendarView.kt`
  - [ ] Monthly calendar grid
  - [ ] Color-coded by mood
  - [ ] Click to see day's entries

### Day 19: Statistics Screen (Charts Tab)
- [ ] MPAndroidChart Integration
  - [ ] Line Chart: Mood trend over time
  - [ ] Bar Chart: Food vs Mood frequency
  - [ ] Pie Chart: Meal type distribution
  - [ ] Pie Chart: Color palette distribution
- [ ] Chart animations
- [ ] Statistics ViewModel
  - [ ] Aggregate data queries
  - [ ] Chart data preparation

### Day 20: Statistics Screen (Insights Tab)
- [ ] AI Insights Generation
  - [ ] Pattern analysis algorithm
  - [ ] Mood-food correlation
  - [ ] Time-based patterns
  - [ ] Activity suggestions
- [ ] Insights UI
  - [ ] Insight cards
  - [ ] Icons & illustrations
  - [ ] Actionable recommendations

### Day 21: Google Maps Integration
- [ ] Map Screen
  - [ ] Display all entry locations
  - [ ] Cluster markers
  - [ ] Heat map overlay
  - [ ] Location filter
- [ ] Map ViewModel
  - [ ] Load entries with location
  - [ ] Marker clustering logic

**Deliverable Tuần 3**: Statistics đầy đủ với charts, insights và maps

---

## 📅 TUẦN 4: POLISH & ADVANCED TOPICS (Days 22-28)

### Day 22: Profile & Settings Screen
- [ ] Profile Screen
  - [ ] User info display
  - [ ] Streak counter
  - [ ] Total entries
  - [ ] Profile photo
- [ ] Settings
  - [ ] Notification toggle
  - [ ] Theme selection (Light/Dark/Auto)
  - [ ] Export data (CSV/PDF)
  - [ ] Clear all entries
  - [ ] Log out

### Day 23: Notifications & WorkManager
- [ ] `NotificationService.kt`
  - [ ] Notification channels
  - [ ] Show reminder notification
  - [ ] Show insight notification
- [ ] `ReminderWorker.kt`
  - [ ] Daily reminder at lunch/dinner time
  - [ ] Weekly insights notification
- [ ] `SyncWorker.kt`
  - [ ] Periodic Firebase sync
  - [ ] Offline-first strategy

### Day 24: Discovery Feature (External API)
- [ ] `FoodApiService.kt`
  - [ ] Retrofit setup
  - [ ] TheMealDB API integration
- [ ] Discovery Screen
  - [ ] "Random Meal" button
  - [ ] Meal card display
  - [ ] Save to favorites
- [ ] Discovery ViewModel

### Day 25: Sensors Integration
- [ ] Accelerometer - Shake to Undo
  - [ ] `ShakeDetector.kt`
  - [ ] Integrate in Add Entry screen
- [ ] Light Sensor - Auto Theme
  - [ ] `LightSensorManager.kt`
  - [ ] Auto dark/light mode switching
- [ ] Permission handling

### Day 26: Animations & Polish
- [ ] Screen Transition Animations
  - [ ] Fade in/out
  - [ ] Slide transitions
- [ ] Component Animations
  - [ ] AnimatedMoodEmoji
  - [ ] PulsingAddButton
  - [ ] Chart animations
- [ ] Lottie Animations
  - [ ] Loading animation
  - [ ] Success animation
  - [ ] Empty state animation

### Day 27: Content Provider & Sharing
- [ ] `EntryContentProvider.kt`
  - [ ] Expose entries to other apps
  - [ ] Read-only access
  - [ ] Security rules
- [ ] Share Entry Functionality
  - [ ] Share as image
  - [ ] Share as text
  - [ ] Export to calendar

### Day 28: Testing & Optimization
- [ ] Unit Tests
  - [ ] ViewModel tests
  - [ ] Repository tests
  - [ ] Use case tests
- [ ] Performance Optimization
  - [ ] Image compression
  - [ ] Database indexes
  - [ ] LazyColumn optimization
  - [ ] Memory leak check (LeakCanary)
- [ ] UI Tests
  - [ ] Critical flow tests
  - [ ] Screenshot tests
- [ ] Bug fixes & polish

**Deliverable Tuần 4**: Ứng dụng hoàn chỉnh, đã test và optimize

---

## 📊 PROGRESS TRACKING

### Week 1 Checklist
- [ ] Firebase setup complete
- [ ] Google Maps API configured
- [ ] MVVM architecture implemented
- [ ] Room Database working
- [ ] Authentication working
- [ ] Navigation working

### Week 2 Checklist
- [ ] Camera capture working
- [ ] Gallery picker working
- [ ] Color analysis working
- [ ] Add entry form complete
- [ ] Location services working
- [ ] Home screen with list view

### Week 3 Checklist
- [ ] Entry detail screen complete
- [ ] Statistics overview working
- [ ] Charts displaying correctly
- [ ] AI insights generating
- [ ] Google Maps showing entries

### Week 4 Checklist
- [ ] Profile & Settings complete
- [ ] Notifications working
- [ ] WorkManager scheduled
- [ ] Discovery API integrated
- [ ] Sensors working
- [ ] Animations smooth
- [ ] Content Provider working
- [ ] All tests passing

---

## 🚀 DEPLOYMENT CHECKLIST

### Pre-Release
- [ ] Update version code & name
- [ ] Generate signed APK
- [ ] ProGuard rules configured
- [ ] Test on multiple devices
- [ ] Check all permissions
- [ ] Verify Firebase rules
- [ ] Test offline functionality
- [ ] Performance profiling

### Play Store
- [ ] App icon & screenshots
- [ ] Feature graphic
- [ ] Privacy policy
- [ ] Terms of service
- [ ] Store listing description
- [ ] Content rating
- [ ] Pricing & distribution

---

## 📝 NOTES & TIPS

### Development Best Practices
1. **Commit Often**: Commit sau mỗi feature nhỏ
2. **Test Continuously**: Test ngay khi implement feature
3. **Handle Errors**: Try-catch và user-friendly error messages
4. **Log Properly**: Timber hoặc proper logging
5. **Comment Complex Logic**: Đặc biệt là algorithm phân tích màu

### Common Pitfalls to Avoid
- ❌ Hardcode API keys trong code
- ❌ Không handle permission denied
- ❌ Không optimize images trước khi upload
- ❌ Quên cancel coroutines khi ViewModel cleared
- ❌ Không test offline mode

### Resources
- [Android Developer Docs](https://developer.android.com)
- [Jetpack Compose Samples](https://github.com/android/compose-samples)
- [Firebase Android Codelab](https://firebase.google.com/docs/android/setup)
- [Google Maps Compose](https://github.com/googlemaps/android-maps-compose)

---

**Version**: 1.0  
**Last Updated**: December 17, 2025  
**Maintainer**: Ha Phuong Quynh
