# 🎯 TOPIC MAPPING - ÁNH XẠ CÁC TOPIC ANDROID VÀO PROJECT

> **Tài liệu chi tiết cách áp dụng từng topic Android vào Food Mood Diary**

---

## 📋 MỤC LỤC

1. [Google Maps API](#1-google-maps-api)
2. [Threading & Background Tasks](#2-threading--background-tasks)
3. [Multimedia (Camera/Photo/Media)](#3-multimedia-cameraphoto)
4. [Content Provider](#4-content-provider)
5. [Jetpack Compose](#5-jetpack-compose)
6. [Notifications](#6-notifications)
7. [RESTful API](#7-restful-api)
8. [Performance Optimization](#8-performance-optimization)
9. [Animation](#9-animation)
10. [Sensors](#10-sensors)

---

## 1. GOOGLE MAPS API

### 📍 Áp dụng ở đâu

#### A. Entry Detail Screen - Hiển thị vị trí món ăn
**File**: `EntryDetailScreen.kt`

```kotlin
@Composable
fun EntryDetailScreen(
    entry: FoodEntry,
    viewModel: EntryDetailViewModel = hiltViewModel()
) {
    Column {
        // Food image, name, mood...
        
        // Maps Section
        if (entry.location != null) {
            LocationMapView(
                latitude = entry.location.latitude,
                longitude = entry.location.longitude,
                address = entry.location.address
            )
        }
    }
}

@Composable
fun LocationMapView(
    latitude: Double,
    longitude: Double,
    address: String?,
    modifier: Modifier = Modifier
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(latitude, longitude), 
            15f
        )
    }
    
    GoogleMap(
        modifier = modifier.height(200.dp),
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = LatLng(latitude, longitude)),
            title = address ?: "Food Location"
        )
    }
}
```

#### B. Add Entry Screen - Lấy vị trí hiện tại
**File**: `AddEntryViewModel.kt`

```kotlin
class AddEntryViewModel(
    private val locationService: LocationService
) : ViewModel() {
    
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()
    
    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val location = locationService.getCurrentLocation()
                _currentLocation.value = location
                
                // Reverse geocoding để lấy địa chỉ
                val address = locationService.getAddressFromLocation(
                    location.latitude,
                    location.longitude
                )
                _locationAddress.value = address
            } catch (e: Exception) {
                // Handle permission denied or location unavailable
            }
        }
    }
}
```

**File**: `LocationService.kt`

```kotlin
class LocationService(
    private val context: Context
) {
    private val fusedLocationClient = LocationServices
        .getFusedLocationProviderClient(context)
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location = suspendCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    continuation.resume(location)
                } else {
                    // Request fresh location
                    requestNewLocation(continuation)
                }
            }
            .addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
    }
    
    suspend fun getAddressFromLocation(
        latitude: Double,
        longitude: Double
    ): String? = withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            addresses?.firstOrNull()?.getAddressLine(0)
        } catch (e: Exception) {
            null
        }
    }
}
```

#### C. Statistics Screen - Bản đồ nhiệt món ăn (Heat Map)
**File**: `StatisticsMapScreen.kt`

```kotlin
@Composable
fun FoodHeatMapScreen(
    entries: List<FoodEntry>,
    modifier: Modifier = Modifier
) {
    val latLngList = entries.mapNotNull { entry ->
        entry.location?.let { 
            WeightedLatLng(
                LatLng(it.latitude, it.longitude),
                1.0
            )
        }
    }
    
    GoogleMap(modifier = modifier.fillMaxSize()) {
        HeatMap(
            data = latLngList,
            gradient = HeatmapTileProvider.Gradient(
                colors = listOf(
                    Color.TRANSPARENT,
                    Color.Green,
                    Color.Yellow,
                    Color.Red
                ),
                startPoints = floatArrayOf(0f, 0.25f, 0.5f, 1f)
            )
        )
    }
}
```

### 🎯 Kỹ thuật áp dụng

| Kỹ thuật | Implementation |
|----------|----------------|
| **Display Map** | GoogleMap Composable |
| **GPS Location** | FusedLocationProviderClient |
| **Geocoding** | Geocoder API (LatLng ↔ Address) |
| **Markers** | Custom marker cho từng entry |
| **Heat Map** | Hiển thị mật độ món ăn theo vùng |
| **Clustering** | Gom nhóm markers khi zoom out |

---

## 2. THREADING & BACKGROUND TASKS

### ⚡ Áp dụng ở đâu

#### A. Coroutines - Async Operations

**1. ViewModel - UI Operations**
```kotlin
class EntryViewModel(
    private val repository: EntryRepository
) : ViewModel() {
    
    fun loadEntries() {
        viewModelScope.launch {  // ✅ Auto-cancelled khi ViewModel destroyed
            _uiState.value = UiState.Loading
            
            repository.getEntries()
                .catch { e -> 
                    _uiState.value = UiState.Error(e.message) 
                }
                .collect { entries ->
                    _uiState.value = UiState.Success(entries)
                }
        }
    }
    
    fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            val colors = withContext(Dispatchers.Default) {
                // CPU-intensive task on background thread
                ColorAnalyzer.extractPalette(bitmap)
            }
            _extractedColors.value = colors
        }
    }
}
```

**2. Repository - Data Operations**
```kotlin
class EntryRepository(
    private val localDataSource: EntryDao,
    private val remoteDataSource: FirebaseService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun addEntry(entry: FoodEntry) = withContext(ioDispatcher) {
        // 1. Save to local DB
        val localId = localDataSource.insert(entry.toEntity())
        
        // 2. Upload image to Firebase Storage
        val imageUrl = remoteDataSource.uploadImage(entry.imageUri)
        
        // 3. Save to Firestore
        val firebaseId = remoteDataSource.saveEntry(
            entry.copy(imageUrl = imageUrl)
        )
        
        // 4. Update local DB with Firebase ID
        localDataSource.updateFirebaseId(localId, firebaseId)
    }
}
```

#### B. WorkManager - Periodic Background Tasks

**File**: `SyncWorker.kt`

```kotlin
class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        return try {
            // Sync unsaved entries to Firebase
            val unsynced = database.entryDao().getUnsyncedEntries()
            
            unsynced.forEach { entry ->
                firebase.syncEntry(entry)
                database.entryDao().markAsSynced(entry.id)
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
```

**Setup Periodic Sync**
```kotlin
class FoodMoodDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupPeriodicSync()
    }
    
    private fun setupPeriodicSync() {
        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sync_entries",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
```

#### C. Service - Foreground Service cho Location Tracking

**File**: `LocationTrackingService.kt`

```kotlin
class LocationTrackingService : Service() {
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(
            NOTIFICATION_ID,
            createNotification("Tracking your food journey...")
        )
        
        // Start location updates
        startLocationTracking()
        
        return START_STICKY
    }
    
    private fun startLocationTracking() {
        // Use FusedLocationProviderClient
        // Update location every 5 minutes
    }
}
```

### 🎯 So sánh Threading Approaches

| Approach | Use Case | Ưu điểm | Nhược điểm |
|----------|----------|---------|------------|
| **Coroutines** | UI operations, API calls | Modern, dễ đọc, lifecycle-aware | Cần học syntax mới |
| **WorkManager** | Scheduled tasks, guaranteed execution | Persist qua reboot, battery optimized | Không real-time |
| **Service** | Long-running foreground tasks | Full control | Cần quản lý lifecycle |
| **AsyncTask** (deprecated) | ❌ Không dùng | - | Memory leak, deprecated |

---

## 3. MULTIMEDIA (Camera/Photo)

### 📸 Áp dụng ở đâu

#### A. CameraX - Chụp ảnh món ăn
**File**: `CameraPreviewScreen.kt`

```kotlin
@Composable
fun CameraPreviewScreen(
    onPhotoCaptured: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    controller = cameraController
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Capture Button
        FloatingActionButton(
            onClick = {
                capturePhoto(cameraController, context, onPhotoCaptured)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        ) {
            Icon(Icons.Default.Camera, "Capture")
        }
    }
}

private fun capturePhoto(
    controller: LifecycleCameraController,
    context: Context,
    onPhotoCaptured: (Bitmap) -> Unit
) {
    controller.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                val bitmap = image.toBitmap()
                onPhotoCaptured(bitmap)
                image.close()
            }
            
            override fun onError(exception: ImageCaptureException) {
                // Handle error
            }
        }
    )
}
```

#### B. MediaStore - Chọn ảnh từ thư viện
**File**: `AddEntryScreen.kt`

```kotlin
@Composable
fun AddEntryScreen(viewModel: AddEntryViewModel = hiltViewModel()) {
    
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { viewModel.onImageSelected(it) }
    }
    
    Column {
        // Image Display
        AsyncImage(
            model = viewModel.selectedImageUri.collectAsState().value,
            contentDescription = "Food Photo",
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        
        // Action Buttons
        Row {
            Button(onClick = { 
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }) {
                Text("Choose from Gallery")
            }
            
            Button(onClick = { 
                viewModel.openCamera() 
            }) {
                Text("Take Photo")
            }
        }
    }
}
```

#### C. Palette API - Phân tích màu sắc
**File**: `ColorAnalyzer.kt`

```kotlin
object ColorAnalyzer {
    
    suspend fun extractPalette(bitmap: Bitmap): ColorPalette = 
        withContext(Dispatchers.Default) {
            val palette = Palette.from(bitmap).generate()
            
            ColorPalette(
                vibrant = palette.vibrantSwatch?.rgb,
                vibrantDark = palette.darkVibrantSwatch?.rgb,
                vibrantLight = palette.lightVibrantSwatch?.rgb,
                muted = palette.mutedSwatch?.rgb,
                mutedDark = palette.darkMutedSwatch?.rgb,
                mutedLight = palette.lightMutedSwatch?.rgb,
                dominant = palette.dominantSwatch?.rgb
            )
        }
    
    fun suggestMood(colorPalette: ColorPalette): MoodSuggestion {
        val dominantColor = colorPalette.dominant ?: return MoodSuggestion.Unknown
        
        return when {
            isWarmColor(dominantColor) -> MoodSuggestion(
                mood = "Energetic",
                emoji = "🔥",
                reason = "Warm colors like red/orange suggest energy"
            )
            isCoolColor(dominantColor) -> MoodSuggestion(
                mood = "Calm",
                emoji = "😌",
                reason = "Cool colors like blue/green suggest calmness"
            )
            else -> MoodSuggestion.Unknown
        }
    }
    
    private fun isWarmColor(rgb: Int): Boolean {
        val color = Color(rgb)
        val red = color.red
        val yellow = (color.red + color.green) / 2
        return red > 0.6f || yellow > 0.6f
    }
}
```

### 🎯 Kỹ thuật áp dụng

| Feature | Implementation |
|---------|----------------|
| **Camera Capture** | CameraX API (Preview + ImageCapture) |
| **Gallery Access** | Photo Picker API (ActivityResultContracts) |
| **Image Compression** | Bitmap scaling & JPEG compression |
| **Color Analysis** | Android Palette API |
| **Image Caching** | Coil library |

---

## 4. CONTENT PROVIDER

### 🔄 Áp dụng ở đâu

#### A. Chia sẻ Entry sang app khác
**File**: `EntryContentProvider.kt`

```kotlin
class EntryContentProvider : ContentProvider() {
    
    companion object {
        const val AUTHORITY = "com.haphuongquynh.foodmooddiary.provider"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/entries")
        
        const val CODE_ENTRIES = 1
        const val CODE_ENTRY_ITEM = 2
    }
    
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "entries", CODE_ENTRIES)
        addURI(AUTHORITY, "entries/#", CODE_ENTRY_ITEM)
    }
    
    private lateinit var database: FoodMoodDatabase
    
    override fun onCreate(): Boolean {
        database = Room.databaseBuilder(
            context!!,
            FoodMoodDatabase::class.java,
            "food_mood_db"
        ).build()
        return true
    }
    
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            CODE_ENTRIES -> {
                database.entryDao().getAllEntriesCursor()
            }
            CODE_ENTRY_ITEM -> {
                val id = uri.lastPathSegment?.toLongOrNull()
                database.entryDao().getEntryByIdCursor(id!!)
            }
            else -> null
        }
    }
    
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Không cho phép insert từ bên ngoài (security)
        throw UnsupportedOperationException("Insert not supported")
    }
    
    override fun getType(uri: Uri): String {
        return when (uriMatcher.match(uri)) {
            CODE_ENTRIES -> "vnd.android.cursor.dir/vnd.$AUTHORITY.entries"
            CODE_ENTRY_ITEM -> "vnd.android.cursor.item/vnd.$AUTHORITY.entries"
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }
    
    // update() và delete() tương tự
}
```

**File**: `EntryDao.kt` (thêm Cursor query)

```kotlin
@Dao
interface EntryDao {
    @Query("SELECT * FROM food_entries")
    fun getAllEntriesCursor(): Cursor
    
    @Query("SELECT * FROM food_entries WHERE localId = :id")
    fun getEntryByIdCursor(id: Long): Cursor
}
```

#### B. Sử dụng Content Provider từ app khác

```kotlin
// App khác có thể đọc entries
val cursor = contentResolver.query(
    Uri.parse("content://com.haphuongquynh.foodmooddiary.provider/entries"),
    null, null, null, null
)

cursor?.use {
    while (it.moveToNext()) {
        val foodName = it.getString(it.getColumnIndexOrThrow("foodName"))
        val mood = it.getString(it.getColumnIndexOrThrow("mood"))
        // Use data...
    }
}
```

### 🎯 Use Case

- Chia sẻ food entries sang app khác (e.g., fitness tracker)
- Cho phép widget từ app khác hiển thị mood calendar
- Export data cho analytics tools

---

## 5. JETPACK COMPOSE

### 🎨 Áp dụng ở đâu

**Toàn bộ UI sử dụng Jetpack Compose**

#### A. Theme System
**File**: `Theme.kt`

```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB74D),
    secondary = Color(0xFF4CAF50),
    tertiary = Color(0xFFE91E63)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF9800),
    secondary = Color(0xFF8BC34A),
    tertiary = Color(0xFFF48FB1)
)

@Composable
fun FoodMoodDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

#### B. Complex UI Components
**File**: `EntryCard.kt`

```kotlin
@Composable
fun EntryCard(
    entry: FoodEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Food Image
            AsyncImage(
                model = entry.imageUrl,
                contentDescription = entry.foodName,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.foodName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Mood with Emoji
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = entry.moodEmoji,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = entry.mood,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Time & Location
                Text(
                    text = "${entry.time} • ${entry.locationAddress ?: "Unknown"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Rating
            RatingStars(rating = entry.rating)
        }
    }
}

@Composable
fun RatingStars(rating: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFB74D) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
```

#### C. State Management
**File**: `HomeScreen.kt`

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val entries by viewModel.entries.collectAsStateWithLifecycle()
    
    HomeContent(
        uiState = uiState,
        entries = entries,
        onRefresh = viewModel::refresh,
        onEntryClick = viewModel::onEntryClicked
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    entries: List<FoodEntry>,
    onRefresh: () -> Unit,
    onEntryClick: (String) -> Unit
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState is HomeUiState.Loading,
        onRefresh = onRefresh
    )
    
    Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(entries, key = { it.id }) { entry ->
                EntryCard(
                    entry = entry,
                    onClick = { onEntryClick(entry.id) }
                )
            }
        }
        
        PullRefreshIndicator(
            refreshing = uiState is HomeUiState.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}
```

### 🎯 Compose Features Used

| Feature | Use Case |
|---------|----------|
| **Material3** | Design system, theming |
| **Navigation** | Screen navigation |
| **LazyColumn/Grid** | Scrollable lists |
| **State Management** | StateFlow + collectAsState |
| **Side Effects** | LaunchedEffect, DisposableEffect |
| **Animation** | animateContentSize, AnimatedVisibility |

---

## 6. NOTIFICATIONS

### 🔔 Áp dụng ở đâu

#### A. Local Notifications - Nhắc nhở ghi nhật ký
**File**: `NotificationService.kt`

```kotlin
class NotificationService(private val context: Context) {
    
    private val notificationManager = context.getSystemService(
        Context.NOTIFICATION_SERVICE
    ) as NotificationManager
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_REMINDER,
                    "Entry Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders to log your food & mood"
                },
                NotificationChannel(
                    CHANNEL_INSIGHTS,
                    "Insights",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Weekly insights about your mood patterns"
                }
            )
            
            notificationManager.createNotificationChannels(channels)
        }
    }
    
    fun showReminderNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDER)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Time to log your meal! 🍽️")
            .setContentText("How was your lunch today?")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(
                R.drawable.ic_add,
                "Add Entry",
                pendingIntent
            )
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
    }
    
    fun showInsightNotification(insight: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_INSIGHTS)
            .setSmallIcon(R.drawable.ic_insights)
            .setContentTitle("Weekly Insight 📊")
            .setContentText(insight)
            .setStyle(NotificationCompat.BigTextStyle().bigText(insight))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_INSIGHT, notification)
    }
    
    companion object {
        const val CHANNEL_REMINDER = "reminder_channel"
        const val CHANNEL_INSIGHTS = "insights_channel"
        const val NOTIFICATION_ID_REMINDER = 1
        const val NOTIFICATION_ID_INSIGHT = 2
    }
}
```

#### B. WorkManager - Schedule Notifications
**File**: `ReminderWorker.kt`

```kotlin
class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val notificationService = NotificationService(applicationContext)
        
        // Check if user hasn't logged today
        val hasLoggedToday = checkIfLoggedToday()
        
        if (!hasLoggedToday) {
            notificationService.showReminderNotification()
        }
        
        return Result.success()
    }
    
    private suspend fun checkIfLoggedToday(): Boolean {
        val today = LocalDate.now().toString()
        return database.entryDao().hasEntriesForDate(today) > 0
    }
}
```

**Setup Daily Reminders**
```kotlin
fun scheduleDailyReminders(context: Context) {
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()
    
    // Lunch reminder at 12:30 PM
    val lunchReminder = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(calculateDelayToTime(12, 30), TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .build()
    
    // Dinner reminder at 7:00 PM
    val dinnerReminder = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(calculateDelayToTime(19, 0), TimeUnit.MILLISECONDS)
        .setConstraints(constraints)
        .build()
    
    WorkManager.getInstance(context).apply {
        enqueueUniqueWork(
            "lunch_reminder",
            ExistingWorkPolicy.REPLACE,
            lunchReminder
        )
        enqueueUniqueWork(
            "dinner_reminder",
            ExistingWorkPolicy.REPLACE,
            dinnerReminder
        )
    }
}
```

#### C. Firebase Cloud Messaging - Push Notifications

```kotlin
class FoodMoodFirebaseMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {
            showNotification(it.title, it.body)
        }
        
        message.data.let { data ->
            when (data["type"]) {
                "streak_milestone" -> handleStreakMilestone(data)
                "friend_activity" -> handleFriendActivity(data)
            }
        }
    }
    
    override fun onNewToken(token: String) {
        // Send token to server
        sendTokenToServer(token)
    }
}
```

### 🎯 Notification Strategy

| Type | Trigger | Purpose |
|------|---------|---------|
| **Daily Reminder** | 12:30 PM, 7:00 PM | Nhắc ghi nhật ký |
| **Streak Alert** | Midnight if no entry | Duy trì streak |
| **Weekly Insight** | Sunday 8:00 PM | Insights từ data |
| **Achievement** | When milestone reached | Gamification |

---

## 7. RESTFUL API

### 🌐 Áp dụng ở đâu

#### A. Discovery Feature - External Food API
**File**: `FoodApiService.kt`

```kotlin
interface FoodApiService {
    
    @GET("random.php")
    suspend fun getRandomMeal(): MealResponse
    
    @GET("search.php")
    suspend fun searchMeals(@Query("s") query: String): MealSearchResponse
    
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): MealResponse
}

// Retrofit instance
object FoodApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
        )
        .build()
    
    val api: FoodApiService = retrofit.create(FoodApiService::class.java)
}
```

**File**: `DiscoveryViewModel.kt`

```kotlin
class DiscoveryViewModel(
    private val foodApiService: FoodApiService
) : ViewModel() {
    
    private val _randomMeal = MutableStateFlow<Meal?>(null)
    val randomMeal: StateFlow<Meal?> = _randomMeal.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun getRandomMeal() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = foodApiService.getRandomMeal()
                _randomMeal.value = response.meals?.firstOrNull()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun saveMealToFavorites(meal: Meal) {
        viewModelScope.launch {
            // Convert API meal to FoodEntry and save
            val entry = meal.toFoodEntry()
            repository.addEntry(entry)
        }
    }
}
```

**File**: `DiscoveryScreen.kt`

```kotlin
@Composable
fun DiscoveryScreen(
    viewModel: DiscoveryViewModel = hiltViewModel()
) {
    val meal by viewModel.randomMeal.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Discover New Dishes! 🍽️",
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            meal?.let {
                MealCard(
                    meal = it,
                    onSaveClick = { viewModel.saveMealToFavorites(it) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = { viewModel.getRandomMeal() }) {
            Text("Get Random Meal")
        }
    }
}
```

### 🎯 API Integration

| API | Purpose | Endpoint |
|-----|---------|----------|
| **TheMealDB** | Random meal discovery | `/random.php` |
| **Spoonacular** (alternative) | Nutrition info | `/recipes/random` |
| **Custom Backend** (future) | User data sync | Your own API |

---

## 8. PERFORMANCE OPTIMIZATION

### ⚡ Áp dụng ở đâu

#### A. Image Optimization
**File**: `ImageOptimizer.kt`

```kotlin
object ImageOptimizer {
    
    private const val MAX_WIDTH = 1024
    private const val MAX_HEIGHT = 1024
    private const val JPEG_QUALITY = 85
    
    fun compressImage(bitmap: Bitmap): Bitmap {
        val ratio = calculateScaleRatio(bitmap)
        
        return if (ratio < 1.0) {
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * ratio).toInt(),
                (bitmap.height * ratio).toInt(),
                true
            )
        } else {
            bitmap
        }
    }
    
    fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
        return stream.toByteArray()
    }
    
    private fun calculateScaleRatio(bitmap: Bitmap): Float {
        val widthRatio = MAX_WIDTH.toFloat() / bitmap.width
        val heightRatio = MAX_HEIGHT.toFloat() / bitmap.height
        return min(widthRatio, heightRatio)
    }
}
```

#### B. Database Query Optimization
**File**: `EntryDao.kt`

```kotlin
@Dao
interface EntryDao {
    
    // ✅ Use Flow for reactive updates
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC LIMIT 50")
    fun getRecentEntries(): Flow<List<FoodEntryEntity>>
    
    // ✅ Use indexes for faster queries
    @Query("SELECT * FROM food_entries WHERE date = :date")
    fun getEntriesByDate(date: String): Flow<List<FoodEntryEntity>>
    
    // ✅ Aggregate queries
    @Query("""
        SELECT mood, COUNT(*) as count 
        FROM food_entries 
        WHERE date BETWEEN :startDate AND :endDate
        GROUP BY mood
        ORDER BY count DESC
    """)
    fun getMoodStatistics(startDate: String, endDate: String): Flow<List<MoodCount>>
    
    // ✅ Pagination với PagingSource
    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllEntriesPaged(): PagingSource<Int, FoodEntryEntity>
}

// Index definition trong Entity
@Entity(
    tableName = "food_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["userId"]),
        Index(value = ["mood"])
    ]
)
data class FoodEntryEntity(...)
```

#### C. Memory Leak Detection
**File**: `FoodMoodDiaryApp.kt`

```kotlin
class FoodMoodDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        if (BuildConfig.DEBUG) {
            // Setup LeakCanary
            LeakCanary.config = LeakCanary.config.copy(
                dumpHeap = true,
                retainedVisibleThreshold = 3
            )
        }
    }
}
```

#### D. LazyColumn Optimization

```kotlin
@Composable
fun EntryList(entries: List<FoodEntry>) {
    LazyColumn {
        items(
            items = entries,
            key = { it.id }  // ✅ Stable key for recomposition
        ) { entry ->
            EntryCard(
                entry = entry,
                onClick = { /* ... */ },
                modifier = Modifier.animateItemPlacement()  // ✅ Smooth animations
            )
        }
    }
}
```

### 🎯 Performance Tools

| Tool | Purpose | Usage |
|------|---------|-------|
| **Android Profiler** | CPU, Memory, Network | Monitor real-time performance |
| **LeakCanary** | Memory leak detection | Auto-detect leaks in debug |
| **Layout Inspector** | UI hierarchy | Analyze overdraw |
| **Systrace** | Frame timing | Identify jank |

---

## 9. ANIMATION

### 🎬 Áp dụng ở đâu

#### A. Property Animation - Smooth Transitions
**File**: `AnimatedComponents.kt`

```kotlin
@Composable
fun AnimatedMoodEmoji(
    mood: String,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = 1.2f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    
    Text(
        text = getMoodEmoji(mood),
        fontSize = 48.sp,
        modifier = modifier.scale(scale)
    )
}

@Composable
fun PulsingAddButton(onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.scale(scale)
    ) {
        Icon(Icons.Default.Add, "Add Entry")
    }
}
```

#### B. Compose Animation - Screen Transitions
**File**: `AnimatedNavigation.kt`

```kotlin
@Composable
fun AnimatedNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(
            route = "home",
            enterTransition = { fadeIn() + slideInHorizontally() },
            exitTransition = { fadeOut() + slideOutHorizontally() }
        ) {
            HomeScreen()
        }
        
        composable(
            route = "add_entry",
            enterTransition = { 
                slideInVertically(initialOffsetY = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = { it }) + fadeOut()
            }
        ) {
            AddEntryScreen()
        }
    }
}
```

#### C. Lottie Animation - Complex Animations
**File**: `LoadingAnimation.kt`

```kotlin
@Composable
fun LoadingAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_food)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(200.dp)
    )
}

@Composable
fun SuccessAnimation(onAnimationEnd: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.success_checkmark)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )
    
    LaunchedEffect(progress) {
        if (progress == 1f) {
            onAnimationEnd()
        }
    }
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = Modifier.size(150.dp)
    )
}
```

#### D. Chart Animations
**File**: `AnimatedChart.kt`

```kotlin
@Composable
fun AnimatedMoodChart(data: List<MoodData>) {
    var animationPlayed by remember { mutableStateOf(false) }
    val animationProgress by animateFloatAsState(
        targetValue = if (animationPlayed) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )
    
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        data.forEachIndexed { index, moodData ->
            val barHeight = moodData.count * animationProgress
            drawRect(
                color = moodData.color,
                topLeft = Offset(index * 100f, size.height - barHeight),
                size = Size(80f, barHeight)
            )
        }
    }
}
```

### 🎯 Animation Types

| Type | Use Case | Performance |
|------|----------|-------------|
| **Property Animation** | Simple value transitions | High performance |
| **Compose Animation** | Declarative UI transitions | Native, optimized |
| **Lottie** | Complex after-effects animations | Medium (JSON parsing) |
| **Canvas Animation** | Custom drawing animations | High (direct drawing) |

---

## 10. SENSORS

### 📡 Áp dụng ở đâu

#### A. Accelerometer - Shake to Undo
**File**: `ShakeDetector.kt`

```kotlin
class ShakeDetector(
    private val context: Context,
    private val onShakeDetected: () -> Unit
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    
    private val shakeListener = object : SensorEventListener {
        private var lastUpdate: Long = 0
        private var lastX = 0f
        private var lastY = 0f
        private var lastZ = 0f
        
        override fun onSensorChanged(event: SensorEvent) {
            val currentTime = System.currentTimeMillis()
            
            if (currentTime - lastUpdate > 100) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime
                
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                
                val speed = abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
                
                if (speed > SHAKE_THRESHOLD) {
                    onShakeDetected()
                }
                
                lastX = x
                lastY = y
                lastZ = z
            }
        }
        
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    
    fun start() {
        sensorManager.registerListener(
            shakeListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    
    fun stop() {
        sensorManager.unregisterListener(shakeListener)
    }
    
    companion object {
        private const val SHAKE_THRESHOLD = 800
    }
}
```

**Usage in ViewModel**
```kotlin
class AddEntryViewModel : ViewModel() {
    private var shakeDetector: ShakeDetector? = null
    
    fun setupShakeDetector(context: Context) {
        shakeDetector = ShakeDetector(context) {
            // Undo last action
            undoLastChange()
        }
        shakeDetector?.start()
    }
    
    override fun onCleared() {
        shakeDetector?.stop()
        super.onCleared()
    }
}
```

#### B. Light Sensor - Auto Theme Switching
**File**: `LightSensorManager.kt`

```kotlin
class LightSensorManager(
    private val context: Context,
    private val onLightLevelChanged: (Boolean) -> Unit  // true = dark theme
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    
    private val lightListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val lux = event.values[0]
            
            // Dưới 50 lux = môi trường tối
            val shouldUseDarkTheme = lux < DARK_THRESHOLD
            onLightLevelChanged(shouldUseDarkTheme)
        }
        
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    
    fun start() {
        sensorManager.registerListener(
            lightListener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }
    
    fun stop() {
        sensorManager.unregisterListener(lightListener)
    }
    
    companion object {
        private const val DARK_THRESHOLD = 50f  // lux
    }
}
```

**Integration with Theme**
```kotlin
@Composable
fun FoodMoodDiaryTheme(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    var useDarkTheme by remember { mutableStateOf(isSystemInDarkTheme()) }
    
    DisposableEffect(Unit) {
        val lightSensorManager = LightSensorManager(context) { shouldUseDark ->
            useDarkTheme = shouldUseDark
        }
        lightSensorManager.start()
        
        onDispose {
            lightSensorManager.stop()
        }
    }
    
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        content = content
    )
}
```

#### C. Step Counter - Activity Tracking (Optional)
**File**: `StepCounterService.kt`

```kotlin
class StepCounterService : Service() {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    
    private val stepListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val steps = event.values[0].toInt()
                updateStepCount(steps)
            }
        }
        
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        
        stepSensor?.let {
            sensorManager.registerListener(
                stepListener,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        
        return START_STICKY
    }
    
    private fun updateStepCount(steps: Int) {
        // Save to database or update UI
    }
}
```

### 🎯 Sensor Use Cases

| Sensor | Feature | Purpose |
|--------|---------|---------|
| **Accelerometer** | Shake to undo | Quick action reversal |
| **Light Sensor** | Auto dark mode | Better UX in different lighting |
| **Step Counter** | Activity tracking | Correlate activity with mood |
| **Proximity** (future) | Screen off when in pocket | Battery saving |

---

## 📊 TỔNG KẾT

### ✅ Topics được implement đầy đủ

1. ✅ **Google Maps API** - Entry location, heat map
2. ✅ **Threading & Background** - Coroutines, WorkManager, Service
3. ✅ **Multimedia** - CameraX, Palette API, Image compression
4. ✅ **Content Provider** - Share entries to other apps
5. ✅ **Jetpack Compose** - Modern declarative UI
6. ✅ **Notifications** - Local reminders + FCM
7. ✅ **RESTful API** - Retrofit with TheMealDB
8. ✅ **Performance** - Profiler, LeakCanary, optimization
9. ✅ **Animation** - Property, Compose, Lottie
10. ✅ **Sensors** - Accelerometer, Light sensor

### ❌ Topics không phù hợp

- **Wear OS/Android TV**: App mobile-first, không cần extend
- **ExoPlayer**: Không có video/audio playback requirement

---

**Version**: 1.0  
**Last Updated**: December 17, 2025  
**Maintainer**: Ha Phuong Quynh
