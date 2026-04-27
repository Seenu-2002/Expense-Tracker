package com.ajay.seenu.expensetracker.android.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.OverallData
import com.ajay.seenu.expensetracker.domain.model.TransactionFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionsByDate
import com.ajay.seenu.expensetracker.domain.usecase.DateRangeCalculatorUseCase
import com.ajay.seenu.expensetracker.domain.usecase.account.GetAccountsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.category.GetAllCategoriesUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredOverallDataUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetFilteredTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.data_filter.GetRecentTransactionsUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.RestoreTransactionUseCase
import com.ajay.seenu.expensetracker.domain.usecase.transaction.SoftDeleteTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userConfigurationsManager: UserConfigurationsManager,
    private val getRecentTransactions: GetRecentTransactionsUseCase,
    private val getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase,
    private val getFilteredOverallDataUseCase: GetFilteredOverallDataUseCase,
    private val softDeleteTransactionUseCase: SoftDeleteTransactionUseCase,
    private val restoreTransactionUseCase: RestoreTransactionUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val dateRangeCalculatorUseCase: DateRangeCalculatorUseCase
) : ViewModel() {

    private val _overallData: MutableStateFlow<UiState<OverallData>> =
        MutableStateFlow(UiState.Loading)
    val overallData = _overallData.asStateFlow()

    private val _recentTransactions: MutableStateFlow<UiState<List<TransactionsByDate>>> =
        MutableStateFlow(UiState.Loading)
    val recentTransactions = _recentTransactions.asStateFlow()

    private val _userName: MutableStateFlow<String> = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _hasMoreData: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasMoreData = _hasMoreData.asStateFlow()

    private val _currentFilter: MutableStateFlow<TransactionFilter> =
        MutableStateFlow(TransactionFilter())
    val currentFilter: StateFlow<TransactionFilter> = _currentFilter.asStateFlow()

    private val _categories: MutableStateFlow<List<Category>> = MutableStateFlow(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _accounts: MutableStateFlow<List<Account>> = MutableStateFlow(emptyList())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _snackbarEvent: MutableSharedFlow<SnackbarEvent> = MutableSharedFlow()
    val snackbarEvent: SharedFlow<SnackbarEvent> = _snackbarEvent.asSharedFlow()

    data class SnackbarEvent(val transactionId: Long, val message: String)

    private val _updatedDateFormat: MutableStateFlow<String> = MutableStateFlow("")
    val updatedDateFormat = _updatedDateFormat.asStateFlow()

    init {
        init()
        loadCategoriesAndAccounts()
        viewModelScope.launch {
            _userName.emit("Seenivasan T")
        }
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery.debounce(300L).collectLatest { query ->
                val current = _currentFilter.value
                if (current.searchQuery != query) {
                    val newFilter = current.copy(searchQuery = query)
                    _currentFilter.emit(newFilter)
                    _recentTransactions.emit(UiState.Loading)
                    getRecentTransactions(newFilter)
                }
            }
        }
    }

    private fun loadCategoriesAndAccounts() {
        viewModelScope.launch {
            try {
                val income = getAllCategoriesUseCase(TransactionType.INCOME)
                val expense = getAllCategoriesUseCase(TransactionType.EXPENSE)
                _categories.emit((income + expense).sortedBy { it.label })
                _accounts.emit(getAccountsUseCase())
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories/accounts for filter")
            }
        }
    }

    private var lastFetchedPage: Int = 1

    private fun getOverallData(filter: TransactionFilter) {
        viewModelScope.launch {
            val range = dateRangeCalculatorUseCase(filter.dateFilter)
            Timber.d("Calculated Date Range: $range")
            getFilteredOverallData(range)
        }
    }

    private fun init() {
        viewModelScope.launch {
            userConfigurationsManager.getDateFormat().collectLatest {
                _updatedDateFormat.emit(it)
            }
        }
    }

    private fun getFilteredOverallData(dateRange: DateRange) {
        viewModelScope.launch {
            getFilteredOverallDataUseCase(dateRange).collectLatest {
                _overallData.emit(UiState.Success(it))
            }
        }
    }

    private fun getRecentTransactions(filter: TransactionFilter = TransactionFilter()) {
        viewModelScope.launch {
            lastFetchedPage = 1
            val range = dateRangeCalculatorUseCase(filter.dateFilter)
            getFilteredTransactions(dateRange = range, transactionFilter = filter)
        }
    }

    private fun getFilteredTransactions(
        pageNo: Int = lastFetchedPage,
        dateRange: DateRange,
        transactionFilter: TransactionFilter? = null
    ) {
        viewModelScope.launch {
            val currentState = _recentTransactions.value
            getFilteredTransactionsUseCase.invoke(
                dateRange = dateRange,
                pageNo = pageNo,
                transactionFilter = transactionFilter
            ).collectLatest {
                _recentTransactions.emit(
                    UiState.Success(
                        if (lastFetchedPage == 1)
                            it.data
                        else
                            mergeTransactionPages((currentState as UiState.Success).data, it.data)
                    )
                )
                _hasMoreData.emit(it.hasMoreData)
            }
        }
    }

    private fun mergeTransactionPages(
        existing: List<TransactionsByDate>,
        newPage: List<TransactionsByDate>
    ): List<TransactionsByDate> {
        if (newPage.isEmpty()) return existing
        val last = existing.lastOrNull() ?: return newPage
        val first = newPage.first()
        return if (last.rawDate == first.rawDate) {
            existing.dropLast(1) +
                TransactionsByDate(last.rawDate, last.transactions + first.transactions) +
                newPage.drop(1)
        } else {
            existing + newPage
        }
    }

    fun getNextPageTransactions() {
        viewModelScope.launch {
            lastFetchedPage++
            val filter = _currentFilter.value
            val range = dateRangeCalculatorUseCase(filter.dateFilter)
            getFilteredTransactions(dateRange = range, transactionFilter = filter)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            try {
                val currentFilter = _currentFilter.value
                softDeleteTransactionUseCase.invoke(id)
                getOverallData(currentFilter)
                getRecentTransactions(currentFilter)
                _snackbarEvent.emit(SnackbarEvent(id, "Transaction deleted"))
            } catch (e: Exception) {
                Timber.e(e, "Error deleting transaction")
            }
        }
    }

    fun undoDelete(id: Long) {
        viewModelScope.launch {
            try {
                restoreTransactionUseCase.invoke(id)
                getOverallData(_currentFilter.value)
                getRecentTransactions(_currentFilter.value)
            } catch (e: Exception) {
                Timber.e(e, "Error restoring transaction")
            }
        }
    }

    fun setDateFilter(dateFilter: DateFilter) {
        FilterPreference.setCurrentFilter(context, dateFilter)
        val newFilter = _currentFilter.value.copy(dateFilter = dateFilter)
        viewModelScope.launch {
            _currentFilter.emit(newFilter)
            _overallData.emit(UiState.Loading)
            _recentTransactions.emit(UiState.Loading)
            getOverallData(newFilter)
            getRecentTransactions(newFilter)
        }
    }

    fun setFilter(filter: TransactionFilter) {
        FilterPreference.setCurrentFilter(context, filter.dateFilter)
        val filterWithQuery = filter.copy(searchQuery = _currentFilter.value.searchQuery)
        viewModelScope.launch {
            _currentFilter.emit(filterWithQuery)
            _overallData.emit(UiState.Loading)
            _recentTransactions.emit(UiState.Loading)
            getOverallData(filterWithQuery)
            getRecentTransactions(filterWithQuery)
        }
    }

    fun setSearchQuery(query: String) {
        viewModelScope.launch {
            _searchQuery.emit(query)
        }
    }
}