# Almi Project Guidelines

Almi is a Compose Multiplatform wardrobe and outfit-planning app for Android and iOS.

## Structure

- `composeApp`: Android application and shared app composition root; exports the iOS framework.
- `iosApp`: SwiftUI iOS host.
- `shared`: platform-independent state, domain models, interactors, and data as they are added.
- `core:common`: dependency-free shared utilities.
- `core:designsystem`: theme tokens and reusable UI components/assets.
- `feature:home`: home feature Compose UI only.

## Dependency rules

- Feature modules never depend on other feature modules.
- Core modules never depend on `shared` or feature modules.
- `shared` never depends on feature modules.
- `composeApp` is the composition root and wires feature UI to shared components.
- Business logic belongs in `shared`; feature modules render state and dispatch actions.
- Colors, typography, spacing, and reusable visuals come from `core:designsystem`.
- Prefer `expect`/`actual` for platform-specific behavior.

## Build

```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```
