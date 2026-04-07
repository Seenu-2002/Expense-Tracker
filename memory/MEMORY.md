# Expense Tracker — Project Memory

## Architecture
- KMM project: `shared/` (domain, data, SQLDelight), `androidApp/` (Compose + Hilt), `iosApp/`
- Clean Architecture + MVVM. ViewModels use StateFlow. Screens use `collectAsState()`.
- SQLDelight schema: `shared/src/commonMain/sqldelight/.../ExpenseDatabase.sq`
- No `gradlew` shell script — use `gradle` (installed via Homebrew). `gradlew.bat` exists but is Windows-only.
- Screen path has intentional typo: `screeens/` (not `screens/`)

## Key Patterns
- `TransactionDataSource` interface → `TransactionLocalDataSource` impl (only one impl, no separate Android/iOS split)
- `BudgetMonitorService` is Android-layer service (`@Inject constructor`), not in DI module
- Budget timestamps stored in **epoch seconds** (Instant.fromEpochSeconds); Transaction timestamps in **epoch milliseconds**
- `toEpochMillis()` extension in `com.ajay.seenu.expensetracker.util`

## Completed Optimisations (March 2026)

### Fix #1 — N+1 Eliminated in Transaction Fetching
Added `getAllTransactionsWithDetails` and `getAllTransactionsBetweenWithDetails` JOIN queries (SQL + SQLDelight).
- `TransactionDataSource` interface: 3 new methods
- `TransactionLocalDataSource`: implements them
- `TransactionMapper`: 2 new `toDomain()` extensions for JOIN result types
- `TransactionRepository`: removed `parseTransactions()`, hot path now uses JOIN queries
- Also fixed `!!` crash in `getTotalTransactionPerDayByType` → safe null handling with Logger
- Added missing indexes: `transaction_category_id`, `transaction_account_id`

### Fix #2 — N+1 Eliminated in Budget Spending
Added `getAllActiveBudgetsWithSpending` and `getActiveBudgetsWithSpendingForCategory` queries.
- `BudgetMapper`: 2 new `toDomain()` for the JOIN result types
- `BudgetRepository.getAllBudgetsWithSpending()`: single-query Flow (was N+1)
- `BudgetRepository.getActiveBudgetsWithSpendingForCategory()`: new method for service
- `BudgetMonitorService.checkBudgetExceeded()`: now uses single query; removed `TransactionRepository` dependency

### Fix #3 — Silent Exception Handling Fixed
- `BudgetViewModel`: added `BudgetEvent` sealed class + `_events: MutableSharedFlow`, all catch blocks now emit errors; `loadBudgets` now emits `UiState.Failure` on exception
- `AddTransactionViewModel`: added `AddTransactionEvent` sealed class + `_events`, catch blocks emit errors, budget check uses `launch {}` (not nested `viewModelScope.launch`)
- UI screens need to collect `viewModel.events` and show snackbar/toast — not yet wired up in screens

## Remaining Priority Issues
4. Missing DB indexes (added category+account indexes — done above; `createdAt` index already existed)
5. Crash risk `!!` in TransactionRepository (fixed above)
6. Random 400ms delay in `ExpenseByCategoryChartViewModel` (remove `delay((0..400L).random())`)
7. `FilterPreference.getCurrentFilter()` in Compose recomposition → wrap in `remember`
8. Broken `categories` StateFlow in BudgetViewModel (cold flow with WhileSubscribed)
9. Nested coroutine in AddTransactionViewModel (now fixed to use `launch {}` inside scope)
10. Use cases as `@Singleton` in DI (should be factory-scoped)
