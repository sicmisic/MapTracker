# MapTracker

A location-aware field notes app for Android. Drop pins on Google Maps, attach notes, organize by color and category — all stored offline.

Built as a portfolio project targeting a TomTom application, demonstrating Clean Architecture, Jetpack Compose, and modern Android development practices.

## Features

- **Drop pins** — Long-press anywhere on Google Maps to create a new pin
- **Notes** — Each pin has a required title and an optional multi-line note
- **Color coding** — Choose from 8 colors per pin for quick visual identification on the map
- **Categories** — Create custom named categories with a color; pins inherit the category's color
- **Jump to pin** — Tap any saved location in the list to fly the map camera to it
- **Edit pins** — Tap the edit icon on a list item to update title, note, color, or category
- **Swipe to delete** — Swipe a list item left to remove it
- **Search** — Real-time filtering across titles and notes
- **Your location** — FAB centers the camera on your current GPS position
- **Offline-first** — All data lives in a local Room database; no network required

## Architecture

```
┌─────────────────────────────────────────┐
│  UI Layer                               │
│  Compose screens · HiltViewModels       │
│  StateFlow · UiState data classes       │
├─────────────────────────────────────────┤
│  Domain Layer  (zero Android imports)   │
│  Location · Category · PinColor models  │
│  Repository interfaces · Use cases      │
├─────────────────────────────────────────┤
│  Data Layer                             │
│  Room entities · DAOs · AppDatabase     │
│  Repository implementations · Hilt DI  │
└─────────────────────────────────────────┘
```

Package root: `com.example.maptracker.{domain,data,presentation}`

## Tech Stack

| Concern | Library / Version |
|---|---|
| Language | Kotlin 2.2.10 |
| Build | AGP 9.1.1, Gradle version catalog |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose 2.8 (type-safe routes) |
| Dependency Injection | Hilt 2.59.2 + KSP 2.2.10-2.0.2 |
| Database | Room 2.8.4 |
| Maps | Google Maps Compose 4.4.1 + play-services-maps 18.2.0 |
| Location | FusedLocationProviderClient (play-services-location 21.1.0) |
| State management | StateFlow + `combine()` |
| Min SDK | 26 (Android 8.0) |

## Setup

1. Clone the repository
2. Obtain a Google Maps API key from [Google Cloud Console](https://console.cloud.google.com/) — enable **Maps SDK for Android**
3. Add the key to `local.properties` in the project root:
   ```
   MAPS_API_KEY=your_key_here
   ```
4. Open in Android Studio Meerkat (2024.3+) and run on a device or emulator with API 26+

## Building

```bash
# Debug build
./gradlew assembleDebug

# Unit tests
./gradlew test

# Instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest
```

## Project Structure

```
app/src/main/java/com/example/maptracker/
├── domain/
│   ├── model/          Location.kt · Category.kt · PinColor.kt
│   ├── repository/     LocationRepository.kt · CategoryRepository.kt
│   └── usecase/        GetAllLocations · SaveLocation · DeleteLocation
│                       GetLocationById · GetAllCategories · SaveCategory
├── data/
│   ├── local/
│   │   ├── entity/     LocationEntity.kt · CategoryEntity.kt
│   │   ├── LocationDao.kt · CategoryDao.kt
│   │   └── AppDatabase.kt  (Room, v2 with migration)
│   ├── repository/     LocationRepositoryImpl.kt · CategoryRepositoryImpl.kt
│   └── di/             DatabaseModule.kt
└── presentation/
    ├── viewmodel/      LocationViewModel.kt
    └── ui/
        ├── screens/    MapScreen · ListScreen · AddEditScreen
        ├── navigation/ AppNavigation.kt (type-safe routes)
        └── theme/      Material 3 theme
```

## Testing

- **Unit tests** — Use case tests + ViewModel tests using `FakeLocationRepository` (no mocking libraries)
- **Room integration tests** — In-memory database tests for `LocationDao`
- **Compose UI tests** — Semantic-tree tests for `ListScreen`

Tests live in `app/src/test/` and `app/src/androidTest/`.
