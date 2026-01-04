# Agents Notes

## Dependency Injection
This project uses [Metro](https://github.com/ZacSweers/metro) for Dependency Injection.
Please ensure you use Metro annotations and setup for any new components or dependencies.
Do not use Dagger or Hilt.

## Dispatchers
Always inject dispatchers instead of hardcoding `Dispatchers.IO`, `Dispatchers.Main`, etc.
Use `dev.bongballe.libs.base.annotation.WithDispatcherType` annotation with `dev.bongballe.libs.base.DispatcherType` enum to inject the appropriate `CoroutineContext` or `CoroutineDispatcher`.

Example:
```kotlin
class MyRepository(
  @WithDispatcherType(DispatcherType.IO) private val ioContext: CoroutineContext
) {
  suspend fun doWork() = withContext(ioContext) { ... }
}
```
