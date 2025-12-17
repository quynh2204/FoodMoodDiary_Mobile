# 🚀 QUICK START GUIDE

> **Hướng dẫn bắt đầu nhanh với Food Mood Diary**

---

## 📋 PRE-REQUISITES

- ✅ Android Studio Ladybug or newer
- ✅ Java 21 LTS (đã setup)
- ✅ Android SDK 26-36 (đã setup)
- ✅ Firebase account
- ✅ Google Cloud account (for Maps API)

---

## ⚡ SETUP IN 5 STEPS

### Step 1: Firebase Setup (10 minutes)

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Click "Add project" → Enter name "FoodMoodDiary"
3. Add Android app:
   - Package name: `com.haphuongquynh.foodmooddiary`
   - Download `google-services.json`
   - Move file to `app/` directory

4. Enable services in Firebase Console:
   ```
   Authentication → Sign-in method:
     ☑ Email/Password
     ☑ Google
   
   Firestore Database → Create database (start in test mode)
   
   Storage → Create bucket
   
   Cloud Messaging → Enabled by default
   ```

5. Update Firestore Security Rules:
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /users/{userId}/entries/{entryId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
     }
   }
   ```

### Step 2: Google Maps API Setup (5 minutes)

1. Go to [Google Cloud Console](https://console.cloud.google.com)
2. Enable APIs:
   - Maps SDK for Android
   - Places API
   - Geocoding API
3. Create API key → Copy it
4. Create file `local.properties` (if not exists) in project root:
   ```properties
   sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
   GOOGLE_MAPS_API_KEY=YOUR_API_KEY_HERE
   ```

### Step 3: Update AndroidManifest.xml (2 minutes)

Add to `app/src/main/AndroidManifest.xml`:

```xml
<manifest>
    <!-- Permissions -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <uses-feature android:name="android.hardware.camera" android:required="false" />
    <uses-feature android:name="android.hardware.location.gps" android:required="false" />
    
    <application
        android:name=".FoodMoodDiaryApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.FoodMoodDiary">
        
        <!-- Google Maps API Key -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${GOOGLE_MAPS_API_KEY}" />
        
        <activity
            android:name=".presentation.MainActivity"
            android:exported="true"
            android:theme="@style/Theme.FoodMoodDiary">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- WorkManager initialization -->
        <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false"
            tools:node="merge" />
    </application>
</manifest>
```

### Step 4: Sync & Build (3 minutes)

1. Open project in Android Studio
2. Click "Sync Project with Gradle Files" (🐘 icon)
3. Wait for sync to complete
4. Build project: `Build → Make Project`

If you see errors:
- Check `google-services.json` is in `app/` directory
- Check `local.properties` has correct API key
- Try: `Build → Clean Project` then rebuild

### Step 5: Run on Device/Emulator

1. Connect Android device or start emulator
2. Click Run (▶️ icon)
3. App should launch (currently empty - ready for implementation!)

---

## 📝 NEXT: START CODING

### Day 1 Task: Create Application Class

Create `app/src/main/java/com/haphuongquynh/foodmooddiary/FoodMoodDiaryApp.kt`:

```kotlin
package com.haphuongquynh.foodmooddiary

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FoodMoodDiaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // TODO: Initialize Firebase, WorkManager, etc.
    }
}
```

### Day 1 Task: Create MainActivity

Create `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/MainActivity.kt`:

```kotlin
package com.haphuongquynh.foodmooddiary.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.haphuongquynh.foodmooddiary.presentation.theme.FoodMoodDiaryTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodMoodDiaryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Food Mood Diary")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Welcome to $name!",
        modifier = modifier
    )
}
```

### Run Again

Now run the app - you should see "Welcome to Food Mood Diary!"

---

## 📂 FOLDER STRUCTURE TO CREATE

Follow this structure (create as you go):

```
app/src/main/java/com/haphuongquynh/foodmooddiary/
├── FoodMoodDiaryApp.kt ✅ Create first
├── data/
│   ├── local/
│   │   ├── database/
│   │   │   └── FoodMoodDatabase.kt
│   │   ├── dao/
│   │   │   └── EntryDao.kt
│   │   └── entities/
│   │       └── FoodEntryEntity.kt
│   ├── remote/
│   │   └── firebase/
│   │       ├── FirebaseAuthService.kt
│   │       └── FirebaseStorageService.kt
│   └── repository/
│       └── EntryRepository.kt
├── presentation/
│   ├── MainActivity.kt ✅ Create first
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   └── auth/
│   │       ├── SignInScreen.kt
│   │       └── AuthViewModel.kt
│   ├── navigation/
│   │   └── NavGraph.kt
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   └── NetworkModule.kt
└── utils/
    └── Constants.kt
```

---

## 🧪 VERIFY SETUP

### Checklist
- [ ] App builds without errors
- [ ] App runs on emulator/device
- [ ] Firebase connection working (check Logcat for Firebase initialization)
- [ ] Google Maps API key configured (no "InvalidKey" errors)
- [ ] Hilt working (app starts with @HiltAndroidApp)

### Test Firebase Connection

Add this to MainActivity onCreate (temporary):

```kotlin
Firebase.auth.addAuthStateListener { auth ->
    Log.d("Firebase", "Auth state: ${auth.currentUser?.uid ?: "Not signed in"}")
}
```

Check Logcat - should see Firebase initialization logs.

---

## 🐛 TROUBLESHOOTING

### Problem: "google-services.json not found"
**Solution**: Make sure file is in `app/` directory (not project root)

### Problem: "Failed to resolve: com.google.firebase"
**Solution**: 
1. Check internet connection
2. File → Invalidate Caches → Restart
3. Delete `.gradle` folder and sync again

### Problem: "Maps API key invalid"
**Solution**:
1. Check API key is correct in `local.properties`
2. Enable "Maps SDK for Android" in Google Cloud Console
3. Add app's SHA-1 fingerprint to Firebase project

Get SHA-1:
```bash
./gradlew signingReport
```

### Problem: "AAPT: error: resource android:attr/lStar not found"
**Solution**: Update compileSdk to 36 (already done)

### Problem: Hilt errors
**Solution**: Make sure:
1. Application class has `@HiltAndroidApp`
2. MainActivity has `@AndroidEntryPoint`
3. Clean & Rebuild project

---

## 📚 HELPFUL COMMANDS

```bash
# Clean project
./gradlew clean

# Build
./gradlew build

# Install on device
./gradlew installDebug

# Run tests
./gradlew test

# Check dependencies
./gradlew dependencies

# Get signing report (for SHA-1)
./gradlew signingReport
```

---

## 🎯 READY TO CODE!

You're all set! Now follow the [IMPLEMENTATION_ROADMAP.md](./IMPLEMENTATION_ROADMAP.md) to start building features.

**Recommended order:**
1. Week 1: Authentication & Core Architecture
2. Week 2: Add Entry & Home Screen
3. Week 3: Statistics & Maps
4. Week 4: Polish & Advanced Features

Good luck! 🚀

---

**Need Help?**
- Check [RULE.md](./RULE.md) for architecture details
- Check [TOPIC_MAPPING.md](./TOPIC_MAPPING.md) for implementation examples
- Check [PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md) for overview

---

**Version**: 1.0  
**Last Updated**: December 17, 2025
