# Almari

An India-first wardrobe and outfit studio built with Compose Multiplatform.

The app turns a wardrobe into five mixable rails—head, top, layer, bottom, and shoes—with per-rail locking and a one-tap outfit shuffle. Closet, Saved, and Profile follow the supplied Almi design system and work offline first.

## Working flows

- Capture a garment with the camera or choose one from the native photo library
- Search/filter the closet, inspect pieces, style one into a look, or remove it
- Save, favorite, wear, delete, and reopen looks in Outfit Studio
- Edit profile and switch between persistent light/dark appearance
- Register/login and sync garments, photos, looks, credits, and profile with `almi-backend`

## Modules

- `composeApp` — Android entry point and iOS framework
- `iosApp` — SwiftUI host
- `shared` — state and outfit-mixing behavior
- `core:designsystem` — Almi tokens, theme, logo, and reusable UI
- `feature:home` — home screen

## Run

Open the project in Android Studio and run `composeApp`, or open `iosApp/iosApp.xcodeproj` in Xcode.

```bash
./gradlew :composeApp:assembleDebug
```

The Android emulator connects to `http://10.0.2.2:8080`; the iOS simulator uses `http://127.0.0.1:8080`. Start the backend before using Cloud sync. Local wardrobe actions continue to work when the backend is unavailable.

For local Google sign-in on the Android emulator, run `adb reverse tcp:8080 tcp:8080` so Google's localhost callback can reach the backend. Configure the backend's Google OAuth variables before launching the flow. A deployed build should replace the development URLs with the public HTTPS API URL.
