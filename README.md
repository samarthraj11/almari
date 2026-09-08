# Almari

An India-first wardrobe and outfit studio built with Compose Multiplatform.

The first prototype turns a wardrobe into five mixable rails—head, top, layer, bottom, and shoes—with per-rail locking and a one-tap outfit shuffle.

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
