# AGENTS.md

## Project Overview

Kotlin Multiplatform Mobile (KMM) expense tracker — Android (Jetpack Compose + Hilt) and iOS with shared business logic. Clean Architecture + MVVM.

## Module Boundaries

- **`shared/`** — KMM module: domain models, use cases, repositories, data sources, SQLDelight DB. No Android/iOS framework imports in `commonMain`.
- **`androidApp/`** — Compose UI, Hilt ViewModels, Android services (`BudgetMonitorService`, `NotificationService`), navigation.
- **`widgets/`** — Single Glance widget (`AddTransactionWidget`). Depends on `shared`.
- **`iosApp/`** — Swift app consuming `shared` as a framework.

## Build & Test

```bash
./gradlew androidApp:build                    # Full Android build
./gradlew shared:testDebugUnitTest            # Shared unit tests (SQLDelight)
./gradlew androidApp:testDebugUnitTest        # Android unit tests
./gradlew androidApp:connectedAndroidTest     # Instrumented tests
```

> **No `gradlew` shell script** — `gradlew.bat` is Windows-only. Use `gradle` (Homebrew) or add execute permission to a `gradlew` shell script if needed.

## Architecture Conventions

### Data Flow
```
Screen (Compose) → ViewModel (StateFlow) → UseCase → Repository → DataSource → SQLDelight
```

### Key Rules

1. **Use cases live in `shared/commonMain`**, are plain classes (not `@Inject`), and are provided by `UseCaseModule` in `androidApp/di/` via `@Provides` factory methods — no `@Singleton` on use cases.
2. **Repositories and DataSources** are `@Singleton` in `RepositoryModule`. Each `DataSource` interface has a single `Local` impl (e.g., `TransactionDataSource` → `TransactionLocalDataSource`).
3. **ViewModels** use `@HiltViewModel` + `@Inject constructor` for primary deps, and `@Inject internal lateinit var` for secondary deps (see `OverviewScreenViewModel`, `AddTransactionViewModel`).
4. **State pattern**: `MutableStateFlow` exposed as `StateFlow` via `.asStateFlow()`. Error events use `MutableSharedFlow` with sealed class events (e.g., `AddTransactionEvent`).
5. **UI state wrapper**: `UiState<T>` sealed class (`Loading`, `Success`, `Empty`, `Failure`) in `presentation/state/UiState.kt`.

### Timestamp Convention (Critical)
- **Transactions**: epoch **milliseconds** (`Instant.fromEpochMilliseconds` / `toEpochMilliseconds`)
- **Budgets**: epoch **seconds** (`Instant.fromEpochSeconds`, `strftime('%s','now')` in SQL)
- Mixing these up causes silent data bugs.

### KMM expect/actual
- `expect class DriverFactory` → `actual` in `shared/androidMain` and `shared/iosMain`
- `expect class UserConfigurationsManager` → platform-specific preferences/DataStore
- Platform files: `shared/src/{androidMain,iosMain}/kotlin/.../`

## File Locations (Note the typo)

| What | Path |
|------|------|
| Screens | `androidApp/.../presentation/screeens/` (**not** `screens`) |
| ViewModels | `androidApp/.../presentation/viewmodels/` |
| DI modules | `androidApp/.../di/{AppModule,RepositoryModule,UseCaseModule}.kt` |
| Navigation | `androidApp/.../presentation/navigation/Screen.kt` (sealed class routes) |
| Use cases | `shared/.../domain/usecase/{transaction,category,account,attachment,data_filter}/` |
| Repositories | `shared/.../data/repository/` |
| DataSource interfaces | `shared/.../data/data_source/` → impls in `local/` |
| Mappers | `shared/.../data/mapper/` (entity↔domain conversions) |
| SQLDelight schema | `shared/src/commonMain/sqldelight/.../ExpenseDatabase.sq` |
| Domain models | `shared/.../domain/model/` |
| Version catalog | `gradle/libs.versions.toml` |

## Adding a New Feature Checklist

1. **Domain model** in `shared/.../domain/model/`
2. **SQL** in `ExpenseDatabase.sq` → rebuild to generate types
3. **DataSource** interface method + `Local` impl
4. **Mapper** extension function (entity → domain) in `shared/.../data/mapper/`
5. **Repository** method in `shared/.../data/repository/`
6. **UseCase** class in `shared/.../domain/usecase/<feature>/`
7. **DI**: Add `@Provides` in `UseCaseModule` (use cases) or `RepositoryModule` (data layer)
8. **ViewModel** in `androidApp/.../presentation/viewmodels/`
9. **Screen** composable in `androidApp/.../presentation/screeens/`
10. **Route** in `Screen.kt` sealed class + wire in navigation (`MainScreen.kt` or parent `NavHost`)

## SQLDelight

- Single `.sq` file defines all tables and queries. SQLDelight generates typed Kotlin classes per query (e.g., `GetAllTransactionsWithDetails`).
- JOIN queries (e.g., `getAllTransactionsWithDetails`) return dedicated generated types — map with `toDomain()` extensions in `TransactionMapper.kt` / `BudgetMapper.kt`.
- After editing `.sq`, rebuild (`./gradlew shared:build`) to regenerate.
- Foreign keys enabled via `PRAGMA foreign_keys=ON` in `DatabaseDriver.kt`.

## Known Issues & Tech Debt

- `BudgetMonitorService` is Android-layer only (uses `@Inject constructor`), not in any DI module — Hilt finds it via constructor injection.
- `FilterPreference.getCurrentFilter()` called in Compose recomposition should be wrapped in `remember`.
- Events (`AddTransactionEvent`, `BudgetEvent`) are emitted but not yet collected/displayed in all UI screens.
- Random `delay()` in `ExpenseByCategoryChartViewModel` should be removed.

