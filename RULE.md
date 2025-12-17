# 📱 FOOD MOOD DIARY - PROJECT RULES & TECHNICAL DOCUMENTATION

> **Ứng dụng Nhật ký Ăn uống & Cảm xúc kết hợp Firebase & Smart Analysis**

---

## 📋 MỤC LỤC

- [Tổng quan dự án](#tổng-quan-dự-án)
- [Technical Stack](#technical-stack)
- [Kiến trúc ứng dụng](#kiến-trúc-ứng-dụng)
- [Cấu trúc thư mục](#cấu-trúc-thư-mục)
- [Database Schema](#database-schema)
- [Quy tắc phát triển](#quy-tắc-phát-triển)
- [Áp dụng các Topic Android](#áp-dụng-các-topic-android)

---

## 🎯 TỔNG QUAN DỰ ÁN

### Mô tả
**Food Mood Diary** là ứng dụng mobile giúp người dùng theo dõi mối liên hệ giữa **thực phẩm** và **cảm xúc**. Ứng dụng tập trung vào sức khỏe tinh thần (mindfulness), giúp người dùng nhận ra họ thường ăn món gì khi vui, buồn hay stress.

### Đối tượng sử dụng
1. **Gen Z / Foodies**: Thích chụp ảnh đồ ăn, quan tâm đến cái đẹp
2. **Emotional Eaters**: Người có thói quen ăn uống theo cảm xúc
3. **Người bận rộn**: Cần ghi chép nhanh, ít thao tác nhập liệu

### Điểm nổi bật (USP)
- 📸 **Visual-first**: Tập trung vào hình ảnh món ăn đẹp mắt
- 🎨 **Smart Color Analysis**: Tự động phân tích màu sắc món ăn để gợi ý tâm trạng
- ☁️ **Cloud Sync**: Đồng bộ dữ liệu đa thiết bị (Firebase)
- 🗺️ **Location Aware**: Ghi nhận vị trí khi thêm entry
- 📊 **AI Insights**: Phân tích xu hướng ăn uống theo cảm xúc

---

## 🚀 TECHNICAL STACK

### 📱 Core Framework
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **Language** | Kotlin 2.0.21 | Ngôn ngữ chính |
| **SDK** | Android SDK 36 (Compile), Min SDK 26 | Nền tảng Android |
| **Java Runtime** | Java 21 LTS | JVM Runtime |
| **Build Tool** | Gradle 8.13 | Build automation |

### 🏗️ Architecture & Design Pattern
| Pattern | Thư viện/Framework | Mô tả |
|---------|-------------------|--------|
| **MVVM** | ViewModel + LiveData/StateFlow | Model-View-ViewModel pattern |
| **Repository Pattern** | - | Tách biệt data source và business logic |
| **Dependency Injection** | Hilt/Koin | Quản lý dependencies |
| **Single Activity** | Navigation Component | Điều hướng bằng Fragments |

### 🎨 UI Framework
| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| **Jetpack Compose** | Latest | Modern declarative UI |
| **Material Design 3** | Compose Material3 | Design system |
| **Compose Navigation** | - | Screen navigation |
| **Coil** | 2.x | Image loading & caching |

### 💾 Data & Storage
| Công nghệ | Mục đích | Implementation |
|-----------|----------|----------------|
| **Room Database** | Local SQLite database | Offline-first storage |
| **DataStore** | Key-value preferences | User settings |
| **Firebase Realtime DB** | Cloud sync | Real-time data sync |
| **Firebase Firestore** | Structured cloud data | Complex queries |
| **Firebase Storage** | Image storage | Food photos storage |

### 🔐 Authentication & Security
| Công nghệ | Mục đích |
|-----------|----------|
| **Firebase Authentication** | Email/Password + Google Sign-In |
| **Biometric API** | Fingerprint/Face unlock |
| **Encrypted SharedPreferences** | Secure local storage |

### 🌐 Network & API
| Công nghệ | Mục đích |
|-----------|----------|
| **Retrofit** | REST API client |
| **OkHttp** | HTTP client & interceptor |
| **Gson/Moshi** | JSON serialization |
| **Coil** | Image loading from URL |

### 🗺️ Location & Maps
| Công nghệ | Mục đích |
|-----------|----------|
| **Google Maps SDK** | Hiển thị bản đồ |
| **Places API** | Tìm kiếm địa điểm |
| **Fused Location Provider** | GPS location |
| **Geocoding API** | Chuyển đổi tọa độ ↔ địa chỉ |

### 🎬 Media & Multimedia
| Công nghệ | Mục đích |
|-----------|----------|
| **CameraX** | Chụp ảnh camera |
| **MediaStore API** | Truy cập thư viện ảnh |
| **Palette API** | Phân tích màu sắc ảnh |
| **ExoPlayer** | Phát video/audio (nếu cần) |

### ⚡ Background Processing
| Công nghệ | Mục đích |
|-----------|----------|
| **WorkManager** | Scheduled tasks & periodic sync |
| **Kotlin Coroutines** | Async operations |
| **Kotlin Flow** | Reactive data streams |
| **Service** | Background service (nếu cần) |

### 🔔 Notifications
| Công nghệ | Mục đích |
|-----------|----------|
| **NotificationManager** | Local notifications |
| **Firebase Cloud Messaging** | Push notifications |
| **NotificationCompat** | Backward compatibility |

### 📊 Charts & Analytics
| Công nghệ | Mục đích |
|-----------|----------|
| **MPAndroidChart** | Line/Bar/Pie charts |
| **Firebase Analytics** | User behavior tracking |
| **Compose Charts** | Native Compose charts (alternative) |

### 🔬 Sensors
| Sensor | Use Case |
|--------|----------|
| **Accelerometer** | Detect shake to undo |
| **Light Sensor** | Auto theme switching |
| **Step Counter** | Activity tracking |

### 🎨 Animation
| Công nghệ | Mục đích |
|-----------|----------|
| **Property Animation** | Smooth transitions |
| **View Animation** | Legacy animations |
| **Lottie** | Complex animations from JSON |
| **Compose Animation** | Declarative animations |

### 🧪 Testing & Quality
| Công nghệ | Mục đích |
|-----------|----------|
| **JUnit** | Unit testing |
| **Espresso** | UI testing |
| **Mockk** | Mocking framework |
| **LeakCanary** | Memory leak detection |
| **Android Profiler** | Performance monitoring |

---

## 🏛️ KIẾN TRÚC ỨNG DỤNG

### MVVM Architecture Pattern

```
┌─────────────────────────────────────────────────────────────┐
│                         UI LAYER                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Composable Screens (Jetpack Compose)                │  │
│  │  - HomeScreen, AddEntryScreen, StatisticsScreen      │  │
│  │  - ProfileScreen, MapScreen                          │  │
│  └────────────────┬─────────────────────────────────────┘  │
│                   │ observes StateFlow/LiveData             │
│                   ▼                                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  ViewModels                                          │  │
│  │  - EntryViewModel, StatisticsViewModel               │  │
│  │  - AuthViewModel, MapViewModel                       │  │
│  └────────────────┬─────────────────────────────────────┘  │
└───────────────────┼──────────────────────────────────────────┘
                    │ calls
                    ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Use Cases (Business Logic)                         │  │
│  │  - AddEntryUseCase, GetEntriesUseCase               │  │
│  │  - AnalyzeMoodUseCase, SyncDataUseCase              │  │
│  └────────────────┬─────────────────────────────────────┘  │
│                   │                                          │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Domain Models                                       │  │
│  │  - FoodEntry, User, Statistics, MoodAnalysis        │  │
│  └──────────────────────────────────────────────────────┘  │
└───────────────────┼──────────────────────────────────────────┘
                    │ uses
                    ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Repositories (Single Source of Truth)              │  │
│  │  - EntryRepository, UserRepository                  │  │
│  │  - StatisticsRepository                             │  │
│  └────┬────────────────────────────────┬────────────────┘  │
│       │                                │                    │
│       ▼                                ▼                    │
│  ┌─────────────┐                 ┌──────────────┐         │
│  │ Local Data  │                 │ Remote Data  │         │
│  │   Source    │◄───sync────────►│   Source     │         │
│  │             │                 │              │         │
│  │ - Room DB   │                 │ - Firebase   │         │
│  │ - DataStore │                 │ - REST API   │         │
│  └─────────────┘                 └──────────────┘         │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow
```
User Action (UI) 
    → ViewModel captures event
    → ViewModel calls Use Case
    → Use Case calls Repository
    → Repository coordinates Local + Remote data
    → Repository returns Flow/LiveData
    → ViewModel transforms to UI State
    → Composable observes and recomposes
```

### Offline-First Strategy
1. **Write**: Lưu vào Room DB ngay lập tức
2. **Sync**: WorkManager đồng bộ lên Firebase khi có mạng
3. **Read**: Đọc từ Room DB, Firebase chỉ là backup
4. **Conflict Resolution**: Timestamp-based (last-write-wins)

---

## 📁 CẤU TRÚC THƯ MỤC

```
app/src/main/
├── java/com/haphuongquynh/foodmooddiary/
│   ├── 📁 data/
│   │   ├── 📁 local/
│   │   │   ├── 📁 database/
│   │   │   │   ├── FoodMoodDatabase.kt
│   │   │   │   └── DatabaseMigrations.kt
│   │   │   ├── 📁 dao/
│   │   │   │   ├── EntryDao.kt
│   │   │   │   ├── UserDao.kt
│   │   │   │   └── StatisticsDao.kt
│   │   │   └── 📁 entities/
│   │   │       ├── EntryEntity.kt
│   │   │       ├── UserEntity.kt
│   │   │       └── MoodColorEntity.kt
│   │   ├── 📁 remote/
│   │   │   ├── 📁 firebase/
│   │   │   │   ├── FirebaseAuthService.kt
│   │   │   │   ├── FirebaseRealtimeService.kt
│   │   │   │   └── FirebaseStorageService.kt
│   │   │   └── 📁 api/
│   │   │       ├── ApiService.kt
│   │   │       └── ApiModels.kt
│   │   ├── 📁 repository/
│   │   │   ├── EntryRepository.kt
│   │   │   ├── UserRepository.kt
│   │   │   └── StatisticsRepository.kt
│   │   └── 📁 models/
│   │       ├── FoodEntry.kt
│   │       ├── User.kt
│   │       └── Statistics.kt
│   │
│   ├── 📁 domain/
│   │   ├── 📁 usecases/
│   │   │   ├── AddEntryUseCase.kt
│   │   │   ├── GetEntriesUseCase.kt
│   │   │   ├── AnalyzeMoodUseCase.kt
│   │   │   └── SyncDataUseCase.kt
│   │   └── 📁 models/
│   │       └── DomainModels.kt
│   │
│   ├── 📁 presentation/
│   │   ├── 📁 screens/
│   │   │   ├── 📁 home/
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── HomeViewModel.kt
│   │   │   │   └── components/
│   │   │   ├── 📁 addentry/
│   │   │   │   ├── AddEntryScreen.kt
│   │   │   │   ├── AddEntryViewModel.kt
│   │   │   │   ├── CameraPreviewScreen.kt
│   │   │   │   └── components/
│   │   │   ├── 📁 entrydetail/
│   │   │   │   ├── EntryDetailScreen.kt
│   │   │   │   └── EntryDetailViewModel.kt
│   │   │   ├── 📁 statistics/
│   │   │   │   ├── StatisticsScreen.kt
│   │   │   │   ├── StatisticsViewModel.kt
│   │   │   │   └── components/
│   │   │   │       ├── MoodCalendarView.kt
│   │   │   │       ├── ChartComponents.kt
│   │   │   │       └── InsightsView.kt
│   │   │   ├── 📁 profile/
│   │   │   │   ├── ProfileScreen.kt
│   │   │   │   └── ProfileViewModel.kt
│   │   │   ├── 📁 auth/
│   │   │   │   ├── SignInScreen.kt
│   │   │   │   ├── SignUpScreen.kt
│   │   │   │   └── AuthViewModel.kt
│   │   │   └── 📁 map/
│   │   │       ├── MapScreen.kt
│   │   │       └── MapViewModel.kt
│   │   ├── 📁 navigation/
│   │   │   ├── NavGraph.kt
│   │   │   └── NavigationDestinations.kt
│   │   └── 📁 theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   │
│   ├── 📁 utils/
│   │   ├── 📁 helpers/
│   │   │   ├── ColorAnalyzer.kt
│   │   │   ├── DateTimeHelper.kt
│   │   │   └── PermissionHelper.kt
│   │   ├── 📁 extensions/
│   │   │   ├── ContextExt.kt
│   │   │   └── ViewExt.kt
│   │   └── 📁 constants/
│   │       └── AppConstants.kt
│   │
│   ├── 📁 services/
│   │   ├── SyncService.kt
│   │   ├── NotificationService.kt
│   │   └── LocationService.kt
│   │
│   ├── 📁 workers/
│   │   ├── SyncWorker.kt
│   │   └── ReminderWorker.kt
│   │
│   ├── 📁 di/ (Dependency Injection)
│   │   ├── AppModule.kt
│   │   ├── DatabaseModule.kt
│   │   ├── NetworkModule.kt
│   │   └── RepositoryModule.kt
│   │
│   └── FoodMoodDiaryApp.kt (Application class)
│
├── res/
│   ├── drawable/
│   ├── values/
│   │   ├── strings.xml
│   │   ├── colors.xml
│   │   └── themes.xml
│   └── xml/
│       ├── network_security_config.xml
│       └── backup_rules.xml
│
└── AndroidManifest.xml
```

---

## 💾 DATABASE SCHEMA

### Room Database Entities

#### 1. FoodEntry (Bảng chính)
```kotlin
@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey(autoGenerate = true) 
    val localId: Long = 0,
    
    val firebaseId: String? = null,     // ID từ Firebase
    val userId: String,                  // User ID
    
    // Food Info
    val foodName: String,
    val foodImageLocalPath: String?,     // Đường dẫn ảnh local
    val foodImageRemoteUrl: String?,     // URL ảnh trên Firebase Storage
    
    // Mood Info
    val mood: String,                    // "happy", "sad", "stress", "calm", "energetic"
    val moodEmoji: String,               // Emoji Unicode
    val moodColor: String,               // Hex color extracted from image
    
    // Time & Location
    val timestamp: Long,
    val date: String,                    // Format: "yyyy-MM-dd"
    val time: String,                    // Format: "HH:mm"
    val locationLatitude: Double?,
    val locationLongitude: Double?,
    val locationAddress: String?,
    
    // Meal Info
    val mealType: String,                // "breakfast", "lunch", "dinner", "snack"
    val rating: Int,                     // 1-5 stars
    val notes: String?,
    
    // AI Analysis
    val aiSuggestion: String?,           // Gợi ý từ AI dựa trên màu
    val colorPalette: String?,           // JSON array màu trích xuất
    
    // Sync Status
    val syncStatus: Int = 0,             // 0: Pending, 1: Synced, 2: Failed
    val lastSyncTime: Long? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

#### 2. User
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey 
    val userId: String,                  // Firebase UID
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val streak: Int = 0,                 // Số ngày liên tục ghi nhật ký
    val totalEntries: Int = 0,
    val createdAt: Long,
    val lastLoginAt: Long
)
```

#### 3. MoodColorMapping (Bảng tra cứu)
```kotlin
@Entity(tableName = "mood_color_mapping")
data class MoodColorEntity(
    @PrimaryKey 
    val colorHex: String,
    val moodName: String,
    val moodEmoji: String,
    val description: String
)
```

### Firebase Firestore Collections

```
users/
  └── {userId}/
      ├── profile: { email, displayName, photoUrl, streak, ... }
      └── entries/
          └── {entryId}/
              ├── foodName
              ├── mood
              ├── timestamp
              ├── location: { lat, lng, address }
              └── ... (tương tự Room Entity)
```

---

## 📋 QUY TẮC PHÁT TRIỂN

### 🎯 Code Style & Convention

#### Kotlin Style Guide
```kotlin
// ✅ ĐÚNG
class EntryViewModel(
    private val repository: EntryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    fun loadEntries() {
        viewModelScope.launch {
            repository.getEntries()
                .catch { e -> _uiState.value = UiState.Error(e.message) }
                .collect { entries -> _uiState.value = UiState.Success(entries) }
        }
    }
}

// ❌ SAI
class entryviewmodel {
    var uiState: UiState? = null
    fun load() { /* blocking call */ }
}
```

#### Naming Conventions
| Loại | Convention | Ví dụ |
|------|------------|-------|
| Class | PascalCase | `FoodEntryRepository` |
| Function | camelCase | `analyzeColorPalette()` |
| Variable | camelCase | `selectedMood` |
| Constant | UPPER_SNAKE_CASE | `MAX_IMAGE_SIZE` |
| Resource | snake_case | `ic_mood_happy`, `string_app_name` |

### 🏗️ Architecture Rules

#### 1. Dependency Rule
```
Presentation → Domain → Data
```
- **Presentation** chỉ biết **Domain**
- **Domain** KHÔNG phụ thuộc vào **Data** hay **Presentation**
- **Data** implement interface từ **Domain**

#### 2. ViewModel Rules
```kotlin
// ✅ ĐÚNG: ViewModel không biết Android Framework
class EntryViewModel(
    private val addEntryUseCase: AddEntryUseCase
) : ViewModel() {
    // Use StateFlow/LiveData, not Context/View
}

// ❌ SAI: ViewModel không được giữ reference đến Context/Activity/Fragment
class BadViewModel(
    private val context: Context  // ❌ Memory leak!
) : ViewModel()
```

#### 3. Repository Rules
```kotlin
// ✅ ĐÚNG: Single Source of Truth
class EntryRepository(
    private val localDataSource: EntryDao,
    private val remoteDataSource: FirebaseService
) {
    fun getEntries(): Flow<List<FoodEntry>> {
        // 1. Emit from Room DB immediately (cache)
        // 2. Fetch from Firebase in background
        // 3. Update Room DB
        // 4. Room DB automatically emits updated data
        return localDataSource.getAllEntries()
            .map { it.map { entity -> entity.toDomainModel() } }
            .onStart { 
                syncFromRemote() 
            }
    }
}
```

### 🎨 UI Development Rules

#### Jetpack Compose Best Practices
```kotlin
// ✅ ĐÚNG: Stateless Composable
@Composable
fun EntryCard(
    entry: FoodEntry,
    onClickEntry: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClickEntry(entry.id) }
    ) {
        // UI implementation
    }
}

// ✅ ĐÚNG: Hoisted State
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    HomeContent(
        uiState = uiState,
        onClickEntry = viewModel::onEntryClicked
    )
}

@Composable
fun HomeContent(
    uiState: UiState,
    onClickEntry: (String) -> Unit
) {
    // Pure UI logic
}
```

### 🔄 Async Operations

#### Coroutines Best Practices
```kotlin
// ✅ ĐÚNG: Use appropriate scope
class EntryViewModel : ViewModel() {
    fun addEntry(entry: FoodEntry) {
        viewModelScope.launch {  // Auto-cancelled when ViewModel cleared
            try {
                repository.addEntry(entry)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}

// ✅ ĐÚNG: Use proper dispatcher
suspend fun analyzeImage(bitmap: Bitmap): ColorAnalysis {
    return withContext(Dispatchers.Default) {  // CPU-intensive task
        // Complex color analysis
    }
}

suspend fun saveToDatabase(entry: FoodEntry) {
    withContext(Dispatchers.IO) {  // I/O operation
        database.entryDao().insert(entry)
    }
}
```

### 🔐 Security Rules

#### 1. API Keys Protection
```kotlin
// ❌ SAI: Hardcode API key
val GOOGLE_MAPS_KEY = "AIzaSyC..."

// ✅ ĐÚNG: Sử dụng BuildConfig hoặc local.properties
val GOOGLE_MAPS_KEY = BuildConfig.GOOGLE_MAPS_API_KEY
```

#### 2. Firebase Rules
```javascript
// Firestore Security Rules
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/entries/{entryId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 📊 Performance Rules

#### 1. Image Optimization
```kotlin
// ✅ ĐÚNG: Compress trước khi lưu
fun compressImage(bitmap: Bitmap): Bitmap {
    val maxSize = 1024 // Max width/height
    val ratio = min(
        maxSize.toFloat() / bitmap.width,
        maxSize.toFloat() / bitmap.height
    )
    return Bitmap.createScaledBitmap(
        bitmap,
        (bitmap.width * ratio).toInt(),
        (bitmap.height * ratio).toInt(),
        true
    )
}
```

#### 2. Database Query Optimization
```kotlin
// ✅ ĐÚNG: Use Flow để observe changes
@Query("SELECT * FROM food_entries WHERE date = :date ORDER BY timestamp DESC")
fun getEntriesByDate(date: String): Flow<List<FoodEntryEntity>>

// ✅ ĐÚNG: Use paging cho danh sách lớn
@Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
fun getAllEntriesPaged(): PagingSource<Int, FoodEntryEntity>
```

### 🧪 Testing Rules

#### Unit Test Example
```kotlin
@Test
fun `addEntry saves to repository and updates UI state`() = runTest {
    // Given
    val entry = createTestEntry()
    val repository = FakeEntryRepository()
    val viewModel = EntryViewModel(repository)
    
    // When
    viewModel.addEntry(entry)
    
    // Then
    val state = viewModel.uiState.value
    assertTrue(state is UiState.Success)
    assertEquals(1, repository.getEntriesCount())
}
```

---

## 🎓 ÁP DỤNG CÁC TOPIC ANDROID

### ✅ Topic được áp dụng trong dự án

| # | Topic | Áp dụng ở đâu | Mức độ | Ghi chú |
|---|-------|---------------|--------|---------|
| 1 | **Google Maps API** | MapScreen, Entry Detail | ⭐⭐⭐ | Hiển thị vị trí món ăn, định vị GPS |
| 2 | **Threading & Background Task** | Sync, Image Processing | ⭐⭐⭐ | Coroutines + WorkManager |
| 3 | **Multimedia (Camera/Photo)** | Add Entry Screen | ⭐⭐⭐ | CameraX + MediaStore |
| 4 | **Content Provider** | Sharing entries | ⭐⭐ | Chia sẻ entry sang app khác |
| 5 | **Jetpack Compose** | Toàn bộ UI | ⭐⭐⭐ | Modern UI framework |
| 6 | **Notifications** | Nhắc nhở ghi nhật ký | ⭐⭐⭐ | Local + FCM |
| 7 | **RESTful API** | Discovery Feature | ⭐⭐ | Retrofit call external API |
| 8 | **Performance Optimization** | Image loading, DB queries | ⭐⭐⭐ | Profiler usage |
| 9 | **Animation** | Screen transitions, charts | ⭐⭐ | Property + Compose animation |
| 10 | **Sensors** | Shake to undo, Light sensor | ⭐⭐ | Accelerometer, Light sensor |

### ❌ Topic KHÔNG áp dụng (và lý do)

| Topic | Lý do không phù hợp |
|-------|---------------------|
| **Wear OS / Android TV** | App tập trung mobile phone, không cần extend sang wearable |
| **ExoPlayer (Video/Audio)** | App về ảnh tĩnh, không có nhu cầu phát media |

---

## 📅 TIMELINE THỰC HIỆN (4 TUẦN)

### Tuần 1: Setup & Core Architecture
- [ ] Setup project, dependencies, Firebase
- [ ] Implement MVVM architecture
- [ ] Setup Room Database + Firebase sync
- [ ] Implement Authentication (Email/Google)
- [ ] Base screens with Navigation

### Tuần 2: Core Features
- [ ] Add Entry Screen (Camera, Gallery, Form)
- [ ] Home Screen (Grid/List/Calendar view)
- [ ] Entry Detail Screen
- [ ] Color Palette Analysis
- [ ] Location integration

### Tuần 3: Statistics & Advanced Features
- [ ] Statistics Screen (Charts)
- [ ] Calendar mood view
- [ ] AI Insights generation
- [ ] Google Maps integration
- [ ] Notification system

### Tuần 4: Polish & Testing
- [ ] Discovery feature (External API)
- [ ] Animations & transitions
- [ ] Sensor integration
- [ ] Performance optimization
- [ ] Testing & bug fixes

---

## 📚 TÀI LIỆU THAM KHẢO

- [Android Developer Guides](https://developer.android.com/guide)
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Firebase Android SDK](https://firebase.google.com/docs/android/setup)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [Google Maps SDK for Android](https://developers.google.com/maps/documentation/android-sdk)

---

**Version**: 1.0  
**Last Updated**: December 17, 2025  
**Maintainer**: Ha Phuong Quynh
