# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## About This Project

NextRequest is a Postman-like Android API client supporting HTTP requests and WebSocket connections. Users can execute requests, browse history, and organize requests into collections.

## Build & Development Commands

```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Run on device/emulator
./gradlew installDebug

# Unit tests (JUnit 5 via useJUnitPlatform())
./gradlew testDebug
./gradlew testDebug --tests "com.example.nextrequest.HomeViewModelTest"

# Instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# CI equivalent (what GitHub Actions runs on PRs)
./gradlew clean testDebug
```

## Architecture

**MVVM + Clean Architecture** with feature-based package organization under `app/src/main/java/com/example/nextrequest/`:

- `core/` — Shared infrastructure: Room DB setup, Hilt DI modules, Ktor network layer, navigation, theme, reusable Compose components
- `home/` — HTTP request execution feature
- `history/` — Saved request history (HTTP + WebSocket)
- `collection/` — User-organized request collections (HTTP + WebSocket)
- `socket/` — WebSocket real-time communication feature

**Layered structure within each feature:**
```
feature/
├── data/       # Repository implementations, DAOs, entities, mappers
├── domain/     # Interfaces, domain models
└── presentation/ # ViewModel, Screen composable, UiState, components
```

**Data flow:** UI composable → ViewModel (StateFlow) → Repository interface → Repository implementation → Ktor/OkHttp/Room → back up via coroutines.

**Key patterns:**
- `Loadable<T>` sealed class (in `home/presentation/`) wraps async results: `Loading`, `Success`, `Error`, `NetworkError`, `Empty`
- `HistoryItem` and `CollectionItem` are sealed classes supporting both HTTP and WebSocket request types
- Hilt is used throughout for DI; all ViewModels are `@HiltViewModel`
- Navigation is type-safe via Jetpack Navigation Compose (see `core/presentation/navigation/`)

## Key Technologies

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material Design 3 |
| HTTP client | Ktor 3.2.3 (with Gson) |
| WebSocket | OkHttp 4.12.0 |
| Database | Room 2.7.2 |
| DI | Hilt 2.56.2 + KSP |
| Async | Kotlin Coroutines + StateFlow |
| Navigation | Jetpack Navigation Compose 2.9.2 |
| Unit testing | JUnit 5 + Kotest 5.9.1 + MockK 1.13.7 |
| Instrumented tests | Espresso + Hilt Testing |

## Testing Conventions

Tests use JUnit 5 (`@Test` from `org.junit.jupiter.api`), Kotest matchers (`shouldBe`), and MockK for mocking:

```kotlin
@Test
fun `descriptive test name in backticks`() = runTest {
    coEvery { repo.someMethod(any()) } returns someResult
    viewModel.doSomething()
    testDispatcher.scheduler.advanceUntilIdle()
    coVerify(exactly = 1) { repo.someMethod(any()) }
    viewModel.uiState.value.someField shouldBe expectedValue
}
```

Use `StandardTestDispatcher` injected into ViewModels under test. Call `testDispatcher.scheduler.advanceUntilIdle()` after triggering async operations before asserting state.

## Build Configuration

- Min SDK: 24, Target SDK: 35
- Kotlin: 2.0.21, Java: 11
- Dependency versions managed via `gradle/libs.versions.toml`
- Core library desugaring enabled for Java 11+ APIs on older Android versions
- ProGuard disabled in release builds
