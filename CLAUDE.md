# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

```bash
# Build
./gradlew androidApp:build
./gradlew androidApp:assembleRelease

# Install on device
./gradlew androidApp:installDebug

# Tests
./gradlew shared:testDebugUnitTest          # Shared KMM unit tests
./gradlew androidApp:testDebugUnitTest      # Android unit tests
./gradlew androidApp:connectedAndroidTest   # Instrumentation tests

# Lint
./gradlew androidApp:lint
./gradlew shared:lint
```

## Architecture Overview

This is a **Kotlin Multiplatform Mobile (KMM)** project targeting Android and iOS with shared business logic.

### Modules
- `shared/` — KMM module: domain models, use cases, repositories, and SQLDelight database (runs on both platforms)
- `androidApp/` — Android-specific UI (Jetpack Compose), ViewModels, Hilt DI, and platform implementations
- `iosApp/` — iOS app consuming the shared module as a framework
- `widgets/` — Android home screen widgets (Glance/Compose)

### Layer Structure (Clean Architecture + MVVM)

```
Presentation (androidApp)
  └── Composable screens + Hilt ViewModels (StateFlow for state)
        ↓
Domain (shared/commonMain)
  └── UseCases + domain models
        ↓
Data (shared/commonMain)
  └── Repositories → DataSources → SQLDelight-generated queries
        ↓
Platform (shared/androidMain | shared/iosMain)
  └── expect/actual: DatabaseDriver, UserConfigManager
```

### Key Patterns

**Dependency Injection:** Hilt with three modules:
- `AppModule` — app-level singletons (FileManager, UserConfigManager)
- `RepositoryModule` — database driver, repositories, data sources
- `UseCaseModule` — use case factory methods

**Database:** SQLDelight generates type-safe Kotlin from `.sq` files.
- Schema defined in `shared/src/commonMain/sqldelight/com/ajay/seenu/expensetracker/ExpenseDatabase.sq`
- Tables: `TransactionDetailEntity`, `CategoryEntity`, `AccountEntity`, `AttachmentEntity`, `BudgetEntity`
- Android driver: `SQLiteDriver`; iOS: native SQLite driver — both wired via `expect/actual DriverFactory`

**State Management:** ViewModels use `MutableStateFlow` / `StateFlow` with coroutine scopes. UI collects with `collectAsState()`.

**KMM expect/actual:** Platform-specific implementations (database driver, file access, preferences) live in `shared/androidMain` and `shared/iosMain`, declared as `expect` in `commonMain`.

### Dependency Versions (libs.versions.toml)
- Kotlin: 2.2.0 | AGP: 8.13.2 | Compose: 1.9.0
- Hilt: 2.57.1 | SQLDelight: 2.0.2 | Coroutines: (kotlinx)
- Coil (images), Vico (charts), Timber (logging), Security Crypto (encrypted prefs)

### Important Paths
- Screens: `androidApp/src/main/java/.../presentation/screeens/` (note: `screeens` typo is intentional — matches existing structure)
- ViewModels: `androidApp/src/main/java/.../presentation/viewmodels/`
- DI modules: `androidApp/src/main/java/.../di/`
- Use cases: `shared/src/commonMain/kotlin/.../domain/usecase/`
- Repositories: `shared/src/commonMain/kotlin/.../data/repository/`
