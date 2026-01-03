# FoodMoodDiary - Food & Mood Tracking App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org)
[![Android](https://img.shields.io/badge/Android-SDK%2026+-green.svg)](https://developer.android.com)
[![Java](https://img.shields.io/badge/Java-17%20LTS-orange.svg)](https://openjdk.org/)

## Overview

FoodMoodDiary is an Android mobile application that helps users track and discover the relationship between their diet and emotional state on a daily basis. By combining food journaling with mood tracking, the app provides insights into how different meals affect your emotional well-being.

## Features

The application includes the following core features:

- **User Authentication:** Secure sign-up and login with email/password and Google Sign-In integration
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/auth/`)
- **Food Entry Management:** Capture meals via camera or gallery, with automatic color analysis for mood suggestions
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/entry/`)
- **Camera Integration:** Take photos directly within the app with CameraX integration
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/camera/`)
- **AI Chat Assistant:** Get food and mood insights through Gemini AI-powered chat interface
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/ChatScreen.kt`)
- **Statistics & Analytics:** Comprehensive mood trends, food-mood correlations, and calendar views
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/statistics/`)
- **Vietnamese Meals Discovery:** Explore Vietnamese dishes with recipes from Firestore database
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/discovery/`)
- **Home Dashboard:** Overview of recent entries, mood statistics, and quick access to features
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/home/`)
- **User Profile:** Manage account settings, streak counter, and app preferences
  - (Located in `app/src/main/java/com/haphuongquynh/foodmooddiary/presentation/screens/profile/`)

## Tech Stack & Architecture

FoodMoodDiary is built with modern Android development tools and follows best practices:

- **Language:** [Kotlin](https://kotlinlang.org/) 2.0.21
- **Framework:** Android SDK with Jetpack libraries
- **Architecture:** Clean Architecture with MVVM pattern
  - **Presentation Layer:** UI components built with Jetpack Compose, ViewModels managing UI state
  - **Domain Layer:** Business logic with Use Cases and repository interfaces
  - **Data Layer:** Room database, Firebase integration, and repository implementations
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material3 design
- **State Management:** StateFlow and Compose state management
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for compile-time dependency injection
- **Database:** Room (local) + Firebase Firestore (cloud sync)
- **Authentication:** Firebase Authentication with Google Sign-In
- **AI Integration:** Google Gemini API 2.0 for chat assistance
- **Image Processing:** CameraX for camera integration, Palette API for color analysis
- **Async Operations:** Kotlin Coroutines and Flow
- **Background Tasks:** WorkManager for periodic reminders and sync

## Project Structure

The project follows a feature-first directory structure within Clean Architecture framework:

```
FoodMoodDiary/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/haphuongquynh/foodmooddiary/
│   │   │   │   ├── data/                  # Data layer
│   │   │   │   │   ├── local/            # Room database, DAOs, entities
│   │   │   │   │   ├── provider/         # Content Providers
│   │   │   │   │   └── repository/       # Repository implementations
│   │   │   │   ├── domain/               # Domain layer
│   │   │   │   │   ├── model/           # Domain models
│   │   │   │   │   ├── repository/      # Repository interfaces
│   │   │   │   │   └── usecase/         # Business logic use cases
│   │   │   │   ├── presentation/        # Presentation layer
│   │   │   │   │   ├── navigation/      # Navigation setup
│   │   │   │   │   ├── screens/         # UI screens (Compose)
│   │   │   │   │   └── viewmodel/       # ViewModels
│   │   │   │   ├── di/                  # Dependency injection modules
│   │   │   │   ├── ui/                  # UI theme and animations
│   │   │   │   ├── util/                # Utility classes
│   │   │   │   └── worker/              # Background workers
│   │   │   ├── res/                     # Resources
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                        # Unit tests
│   │   └── androidTest/                 # Instrumentation tests
│   ├── build.gradle.kts                 # App-level Gradle config
│   └── google-services.json             # Firebase configuration
├── gradle/                              # Gradle wrapper
├── scripts/                             # Helper scripts
│   └── upload_meals_to_firestore.py    # Script to populate Firestore
├── build.gradle.kts                     # Project-level Gradle config
├── settings.gradle.kts
├── local.properties                     # Local configuration (API keys)
└── README.md                           # This file
```

## Getting Started

Follow these instructions to get the project up and running on your local machine.

### Prerequisites

- Android Studio Ladybug or newer: [Installation Guide](https://developer.android.com/studio)
- Java 17 LTS: [Download](https://adoptium.net/)
- Android SDK 26-36
- Firebase Account: For backend services

### Installation & Setup

1. **Clone the repository:**

   ```bash
   git clone https://github.com/yourusername/FoodMoodDiary.git
   cd FoodMoodDiary
   ```

2. **Setup Firebase:**
   - Create a Firebase project at [Firebase Console](https://console.firebase.google.com)
   - Download `google-services.json` and place it in the `app/` directory
   - Enable the following Firebase services:
     - Authentication (Email/Password and Google Sign-In)
     - Firestore Database
     - Cloud Storage (for images)

3. **Configure API Keys:**
   
   Create a `local.properties` file in the root directory and add:
   
   ```properties
   sdk.dir=/path/to/your/android/sdk
   
   # Google Maps API Key (optional, for map features)
   GOOGLE_MAPS_API_KEY=your_google_maps_api_key_here
   
   # Gemini AI API Key (required for chat feature)
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
   
   Get your Gemini API key from [Google AI Studio](https://aistudio.google.com/app/apikey)

4. **Setup Firestore Vietnamese Meals Database (Optional):**
   
   To populate Vietnamese meals data:
   
   ```bash
   cd scripts
   pip install firebase-admin
   python3 upload_meals_to_firestore.py
   ```
   
   You'll need a Firebase service account key file (`serviceAccountKey.json`) in the `scripts/` folder.

5. **Sync & Build:**

   ```bash
   ./gradlew build
   ```

### Running the App

1. **Ensure an emulator is running or a device is connected.**
   
   Check connected devices:
   ```bash
   adb devices
   ```

2. **Run the app:**
   ```bash
   ./gradlew installDebug
   adb shell am start -n com.haphuongquynh.foodmooddiary/.MainActivity
   ```
   
   Or use the VS Code task: `Run FoodMoodDiary`
   
   Alternatively, run the app from Android Studio by clicking Run ▶️

## Key Features Highlight

### 📸 Smart Camera Integration
- Direct camera capture with CameraX
- Automatic color analysis using Palette API
- Mood suggestions based on food colors

### 🤖 AI-Powered Insights
- Chat with Gemini AI for food and mood advice
- Automated pattern recognition in eating habits
- Personalized suggestions for emotional well-being

### 📊 Comprehensive Analytics
- Mood trends over time with interactive charts
- Food-mood correlation analysis
- Calendar view with color-coded mood indicators

### 🍜 Vietnamese Cuisine Discovery
- Curated collection of Vietnamese dishes
- Recipes and YouTube video tutorials
- Save favorite meals for quick access

### ☁️ Cloud Sync & Offline Support
- Firebase integration for seamless cloud synchronization
- Offline-first architecture with Room database
- Data persistence across devices

## Firebase Setup Details

### Required Firebase Services

1. **Authentication:**
   - Enable Email/Password provider
   - Enable Google Sign-In provider
   - Configure OAuth consent screen

2. **Firestore Database:**
   - Create collection: `vietnameseMeals`
   - Collection stores Vietnamese dish information with fields:
     - `name` (string): Dish name
     - `category` (string): "Món nước", "Món khô", or "Tráng miệng"
     - `youtubeUrl` (string): Recipe video link
     - `imageUrl` (string): High-quality image URL
     - `calories` (number): Estimated calories
     - `description` (string): Dish description
     - `tags` (array): Related keywords

3. **Cloud Storage:**
   - Enable Storage for user-uploaded food images
   - Configure security rules for authenticated users

### Adding Vietnamese Meals to Firestore

**Method 1: Via Firebase Console (Easiest)**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: FoodMoodDiary
3. Navigate to **Firestore Database**
4. Select `vietnameseMeals` collection
5. Click **Add Document**
6. Fill in the fields as described above

**Method 2: Using Python Script**

1. Generate Firebase service account key:
   - Go to Project Settings > Service Accounts
   - Click "Generate New Private Key"
   - Save as `serviceAccountKey.json` in `scripts/` folder

2. Run the upload script:
   ```bash
   cd scripts
   pip install firebase-admin
   python3 upload_meals_to_firestore.py
   ```

## Troubleshooting

### Common Issues

**Java Version Mismatch**
- Ensure Java 17 LTS is installed
- Check version: `java -version`
- macOS: Set JAVA_HOME in zsh:
  ```bash
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  export PATH="$JAVA_HOME/bin:$PATH"
  ```
- Windows: Set JAVA_HOME in PowerShell:
  ```powershell
  $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.x.x"
  $env:Path += ";$env:JAVA_HOME\bin"
  ```

**SDK Location Not Found**
- Edit `local.properties`:
  ```properties
  # macOS
  sdk.dir=/Users/your-username/Library/Android/sdk
  
  # Windows
  sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
  ```

**Gemini API 403 Error**
- Your API key may be leaked or expired
- Generate a new key at [Google AI Studio](https://aistudio.google.com/app/apikey)
- Update `local.properties` with the new key

**Build Dependency Issues**
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

**Firebase Connection Issues**
- Verify `google-services.json` is in `app/` directory
- Check Firebase project settings match your bundle ID
- Ensure all required Firebase services are enabled

## Development Notes

### Key Android Concepts Demonstrated

1. **Jetpack Compose:** Modern declarative UI framework
2. **Clean Architecture:** Separation of concerns with layers
3. **Dependency Injection:** Hilt for managing dependencies
4. **Coroutines & Flow:** Asynchronous programming and reactive streams
5. **CameraX:** Camera integration with modern API
6. **Room Database:** Local data persistence
7. **Firebase Integration:** Authentication, Firestore, Storage
8. **WorkManager:** Background task scheduling
9. **Content Providers:** Sharing data with other apps
10. **Sensors:** Accelerometer and light sensor integration

### Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

## Future Enhancements

- [ ] Social features: Share entries with friends
- [ ] Advanced AI insights with personalized recommendations
- [ ] Integration with health apps (Google Fit)
- [ ] Export data to CSV/PDF formats
- [ ] Multi-language support (Vietnamese, English)
- [ ] Dark theme customization
- [ ] Widget for quick entry creation

## Contributing

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

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- [Android Jetpack](https://developer.android.com/jetpack) - Modern Android development
- [Firebase](https://firebase.google.com) - Backend as a Service
- [Google Gemini API](https://ai.google.dev/) - AI chat integration
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) - Chart library
- [Lottie](https://airbnb.design/lottie/) - Animation library

---

Built with ❤️ using Kotlin & Jetpack Compose
