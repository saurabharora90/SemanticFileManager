# Agents Notes

## General Guidelines

@AGENT_CODING_GUIDELINES.md

## Dependency Injection

This project uses [Metro](https://github.com/ZacSweers/metro) for Dependency Injection.
Please ensure you use Metro annotations and setup for any new components or dependencies.
Do not use Dagger or Hilt.

## Dispatchers

Always inject dispatchers instead of hardcoding `Dispatchers.IO`, `Dispatchers.Main`, etc.
Use `dev.bongballe.libs.base.annotation.WithDispatcherType` annotation with
`dev.bongballe.libs.base.DispatcherType` enum to inject the appropriate `CoroutineContext` or
`CoroutineDispatcher`.

Example:

```kotlin
class MyRepository(
  @WithDispatcherType(DispatcherType.IO) private val ioContext: CoroutineContext
) {
  suspend fun doWork() = withContext(ioContext) { ... }
}
```

## Screenshot Tests

This project uses [Roborazzi](https://github.com/takahirom/roborazzi) for screenshot testing.

### Guidelines

- **Rule**: Use `dev.bongballe.libs.screenshot.BongBalleRoborazziRule`.
- **Configuration**:
  - Annotate test class with `@RunWith(AndroidJUnit4::class)` and
    `@GraphicsMode(GraphicsMode.Mode.NATIVE)`.
  - Use `createComposeRule()`.
  - Use `setContentWithTheme` extension to set content.
  - Use `captureRoboImage()` to capture the screenshot.
- **Output**: Screenshots are saved in `src/test/screenshots`.

### Example

Refer to `features/browser/src/test/kotlin/dev/bongballe/features/browser/FileItemScreenshotTest.kt`
for a complete example.

## Module Creation

This project uses [Slack Foundry](https://github.com/slackhq/foundry) to configure modules.

### Guidelines

1. **Plugins**: Apply `libs.plugins.foundry.base` along with `android.library` and `kotlin.android`.
2. **Namespace**: Define the Android namespace.
3. **Foundry Configuration**: Use the `foundry` block to enable features such as:

* `compose()`: Enables Jetpack Compose support.
* `metro()`: Enables Metro dependency injection support.
* `android { features { snapshotTests() } }`: Enables screenshot testing support (e.g., for
  feature modules).

### Example `build.gradle.kts`

```kotlin
plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.foundry.base)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
}

android {
  namespace = "dev.bongballe.features.myfeature"
}

foundry {
  features {
    compose() // if using compose
    metro() // if using DI
    // other foundry features like Moshi
  }
  android {
    features {
      snapshotTests() // if adding screenshot tests
      robolectric() // if using Robolectric
    }
  }
}

dependencies {
  implementation(project(":libs:base"))
  // ... other dependencies
}
```
