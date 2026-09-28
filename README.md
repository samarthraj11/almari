# Almari

An India-first wardrobe and AI outfit studio built with Compose Multiplatform.

The app turns a wardrobe into five mixable rails—head, top, layer, bottom, and shoes—with per-rail locking and a one-tap outfit shuffle. Closet, Saved, and Profile follow the supplied Almari design system and work offline first.

## Working flows

- Capture a garment with the camera or choose one from the native photo library
- Search/filter the closet, inspect pieces, style one into a look, or remove it
- Generate a full-body AI try-on from the selected outfit and save its preview
- Save, favorite, wear, delete, and reopen looks in Outfit Studio
- Edit profile and switch between persistent light/dark appearance
- Sign in with Google through Firebase Authentication on Android
- Keep garments, photos, looks, preferences, and profile data on the device

## Modules

- `composeApp` — Android entry point and iOS framework
- `iosApp` — SwiftUI host
- `shared` — state and outfit-mixing behavior
- `core:designsystem` — Almari tokens, theme, logo, and reusable UI
- `feature:home` — home screen

## Run

Open the project in Android Studio and run `composeApp`, or open `iosApp/iosApp.xcodeproj` in Xcode.

```bash
./gradlew :composeApp:assembleDebug
```

Wardrobe data remains offline-first. Only **Try it on** connects to `almari-backend`: Android emulators use `http://10.0.2.2:8080`, while the iOS simulator uses `http://127.0.0.1:8080`. Google sign-in uses Android Credential Manager's native account chooser and Firebase Authentication locally. Google sign-in is temporarily Android-only; iOS displays the onboarding screen without starting a browser flow.

Register an Android app with package `com.almari.app` in the Firebase project, add the signing certificate SHA-1, enable the Google provider under Firebase Authentication, download the generated `google-services.json`, and place it at `composeApp/google-services.json`. For the local debug certificate, print it with:

```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```
