# Android Portfolio App — Build TODO
**App Name:** `MapTracker` (or similar — a geospatial field notes app)
**Why this app:** Directly targets TomTom's stack. Uses Kotlin, Jetpack Compose, Android SDK, Clean Architecture, Gradle, and geospatial features — hitting nearly every bullet point in the job description.

---

## App Concept
A location-aware field notes app where users can:
- Drop pins on a map with notes/photos
- View all saved locations in a list or on a map
- Filter/search saved places
- Works offline with local persistence

This is realistic, demonstrable, and portfolio-worthy.

---

## Tech Stack (match it to the job description)
- **Language:** Kotlin (100%)
- **UI:** Jetpack Compose
- **Architecture:** Clean Architecture (Domain / Data / Presentation layers)
- **Maps:** Google Maps SDK for Android (or OpenStreetMap via osmdroid)
- **Local DB:** Room (with Flow)
- **DI:** Hilt
- **Navigation:** Jetpack Navigation Compose
- **Build:** Gradle (Kotlin DSL preferred)
- **Testing:** JUnit4, MockK, Compose UI tests
- **Version Control:** Git (push to GitHub — public repo)

---

## Project Setup

- [ ] Create new Android project in Android Studio
- [ ] Set `minSdk = 26`, `targetSdk = 34`
- [ ] Use Kotlin DSL (`build.gradle.kts`) for all Gradle files
- [ ] Set up version catalog (`libs.versions.toml`)
- [ ] Add dependencies: Compose BOM, Room, Hilt, Navigation, Maps SDK, Coroutines
- [ ] Enable `buildFeatures { compose = true }`
- [ ] Set up `.gitignore` and push initial commit to GitHub

---

## Architecture — Clean Architecture Layers

### Domain Layer (`domain/`)
- [ ] Create `Location` data model (id, title, note, lat, lng, timestamp)
- [ ] Create `LocationRepository` interface
- [ ] Create use cases:
    - [ ] `GetAllLocationsUseCase`
    - [ ] `SaveLocationUseCase`
    - [ ] `DeleteLocationUseCase`
    - [ ] `GetLocationByIdUseCase`

### Data Layer (`data/`)
- [ ] Create Room `@Entity` for `LocationEntity`
- [ ] Create `LocationDao` with `@Query`, `@Insert`, `@Delete`
- [ ] Create `AppDatabase` with Room builder
- [ ] Implement `LocationRepositoryImpl` mapping entity ↔ domain model
- [ ] Provide database and repository via Hilt `@Module`

### Presentation Layer (`presentation/`)
- [ ] Create `LocationViewModel` using `@HiltViewModel`
- [ ] Expose state via `StateFlow<UiState>`
- [ ] Handle user events (add, delete, select)

---

## Screens — Jetpack Compose

### Map Screen (`MapScreen.kt`)
- [ ] Embed `GoogleMap` composable (or OSMDroid MapView via `AndroidView`)
- [ ] Show all saved pins as map markers
- [ ] Long-press on map → open "Add Note" bottom sheet
- [ ] Tap a marker → show pin details in a bottom sheet
- [ ] FAB to center map on current location

### List Screen (`ListScreen.kt`)
- [ ] `LazyColumn` of all saved locations
- [ ] Each item shows title, timestamp, distance from current location
- [ ] Swipe-to-delete with confirmation
- [ ] Search bar to filter by title/note text

### Add/Edit Screen (`AddEditScreen.kt`)
- [ ] Form with `OutlinedTextField` for title and note
- [ ] Show selected coordinates (read-only)
- [ ] Save and Cancel buttons
- [ ] Input validation (title required)

### Navigation
- [ ] Set up `NavHost` with bottom navigation bar
- [ ] Tabs: Map, List
- [ ] Navigate to Add/Edit from both tabs

---

## Permissions & Location

- [ ] Add `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` to manifest
- [ ] Runtime permission request using `rememberLauncherForActivityResult`
- [ ] Use `FusedLocationProviderClient` to get current location
- [ ] Handle permission denied gracefully (show rationale UI)

---

## Testing

- [ ] Unit test each use case (`GetAllLocationsUseCase`, `SaveLocationUseCase`)
- [ ] Unit test `LocationViewModel` with fake repository
- [ ] DAO integration test using Room in-memory database
- [ ] At least one Compose UI test (e.g., list renders items correctly)
- [ ] Use MockK for mocking, `kotlinx-coroutines-test` for coroutine testing

---

## Polish (makes it stand out on a portfolio)

- [ ] Dark theme support (`MaterialTheme` with dynamic color or custom dark palette)
- [ ] Empty state illustration on List screen when no pins saved
- [ ] Loading state handled in UI
- [ ] Error state with retry option
- [ ] Smooth Compose animations (pin drop animation, list item transitions)
- [ ] App icon (custom, not default Android icon)
- [ ] Proper `README.md` with screenshots and architecture diagram

---

## GitHub / Portfolio Setup

- [ ] Push to public GitHub repo named `MapTracker` (or chosen name)
- [ ] Write `README.md` with:
    - [ ] App description and screenshots (use emulator screenshots)
    - [ ] Tech stack badges
    - [ ] Architecture diagram (simple ASCII or image)
    - [ ] How to run locally (API key setup, etc.)
- [ ] Tag a `v1.0.0` release with a signed APK attached
- [ ] Add repo link to CV under Projects section
- [ ] Add to LinkedIn featured section

---

## CV/Resume Update (after build)

Add to Projects section:
```
MapTracker — Geospatial Field Notes App
Kotlin · Jetpack Compose · Room · Hilt · Google Maps SDK · Clean Architecture
[github link] 

Location-aware Android app for saving geo-tagged notes with offline support.
Built with Jetpack Compose UI, Clean Architecture (Domain/Data/Presentation),
Room persistence, Hilt DI, and the Google Maps SDK.
```

---

## Estimated Time
| Phase | Time |
|---|---|
| Setup + Architecture | 2–3 hours |
| Screens + Navigation | 4–6 hours |
| Location + Maps | 2–3 hours |
| Testing | 2–3 hours |
| Polish + README | 2 hours |
| **Total** | **~12–17 hours** |