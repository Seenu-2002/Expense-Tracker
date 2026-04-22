package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.data.FilterPreference
import com.ajay.seenu.expensetracker.android.presentation.components.DateRangePickerBottomSheet
import com.ajay.seenu.expensetracker.android.presentation.components.FilterBottomSheet
import com.ajay.seenu.expensetracker.android.presentation.components.OverviewCard
import com.ajay.seenu.expensetracker.android.presentation.components.TransactionPreviewRow
import com.ajay.seenu.expensetracker.android.presentation.state.UiState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.OverviewScreenViewModel
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.util.toLocalDate
import com.ajay.seenu.expensetracker.util.toSectionLabel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(
    viewModel: OverviewScreenViewModel = hiltViewModel(),
    onTransactionClicked: (Long) -> Unit,
    onCloneTransaction: (Long) -> Unit
) {
    val dateFormat by viewModel.updatedDateFormat.collectAsStateWithLifecycle()
    val recentTransactionsUiState by viewModel.recentTransactions.collectAsStateWithLifecycle()
    val overallDataUiState by viewModel.overallData.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val hasMoreData by viewModel.hasMoreData.collectAsStateWithLifecycle()
    val currentFilter by viewModel.currentFilter.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val accounts by viewModel.accounts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openFilterBottomSheet by rememberSaveable { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()
    val dateRangeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var openDateRangePicker by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val formatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH) }
    val context = LocalContext.current

    BackHandler(enabled = isSearchActive) {
        isSearchActive = false
        viewModel.setSearchQuery("")
    }

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) focusRequester.requestFocus()
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete(event.transactionId)
            }
        }
    }

    LaunchedEffect(dateFormat) {
        if (dateFormat.isNotBlank()) {
            val savedDateFilter = FilterPreference.getCurrentFilter(context)
            viewModel.setDateFilter(savedDateFilter)
        }
    }

    LaunchedEffect(currentFilter) {
        val dateFilter = currentFilter.dateFilter
        if (dateFilter is DateFilter.Custom) {
            dateRangePickerState.setSelection(dateFilter.startDateInMillis, dateFilter.endDateInMillis)
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        topBar = {
            if (isSearchActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        isSearchActive = false
                        viewModel.setSearchQuery("")
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = viewModel::setSearchQuery,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                            .focusRequester(focusRequester),
                        placeholder = { Text("Search transactions...") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Welcome, $userName",
                        modifier = Modifier.padding(vertical = 12.dp),
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { isSearchActive = true },
                        imageVector = Icons.Default.Search,
                        contentDescription = "search"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    BadgedBox(
                        badge = {
                            val isFiltered = currentFilter.dateFilter != DateFilter.ThisMonth ||
                                    currentFilter.hasActiveFilters
                            if (isFiltered) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(color = MaterialTheme.colorScheme.error)
                                )
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp)
                                .clickable { openFilterBottomSheet = true },
                            painter = painterResource(id = R.drawable.icon_filter_list),
                            contentDescription = "filter"
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    ) { paddingValues ->
        if (isSearchActive && searchQuery.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        modifier = Modifier.size(80.dp),
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Search by note, place, category or account",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = recentTransactionsUiState) {
                    UiState.Loading, UiState.Empty -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is UiState.Failure -> {
                        // Fixme
                    }
                    is UiState.Success -> {
                        val recentTransactions = state.data
                        if (recentTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        modifier = Modifier.size(100.dp),
                                        painter = painterResource(id = R.drawable.icon_database),
                                        contentDescription = "Empty",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(15.dp))
                                    Text(text = if (isSearchActive) "No results found." else "No transactions found.")
                                }
                            }
                            return@Scaffold
                        }

                        if (!isSearchActive) {
                            when (val overallDataState = overallDataUiState) {
                                UiState.Loading, UiState.Empty -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                is UiState.Success -> {
                                    OverviewCard(modifier = Modifier.fillMaxWidth(), data = overallDataState.data)
                                }
                                is UiState.Failure -> {}
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            recentTransactions.forEach { transactionsByDate ->
                                stickyHeader {
                                    Text(
                                        modifier = Modifier
                                            .fillParentMaxWidth()
                                            .background(MaterialTheme.colorScheme.background)
                                            .padding(horizontal = 15.dp, vertical = 8.dp),
                                        text = transactionsByDate.rawDate.toSectionLabel(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.W500
                                    )
                                }
                                items(
                                    transactionsByDate.transactions,
                                    key = { transaction -> transaction.id }
                                ) { transaction ->
                                    TransactionPreviewRow(
                                        Modifier
                                            .fillMaxWidth()
                                            .animateContentSize(),
                                        transaction,
                                        onClick = { onTransactionClicked(transaction.id) },
                                        onDelete = { viewModel.deleteTransaction(transaction.id) },
                                        onClone = { onCloneTransaction(transaction.id) }
                                    )
                                }
                            }
                            if (hasMoreData) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentHeight()
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                    viewModel.getNextPageTransactions()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (openFilterBottomSheet) {
        FilterBottomSheet(
            sheetState = sheetState,
            filter = currentFilter,
            categories = categories,
            accounts = accounts,
            formatter = formatter,
            onFilterSelected = { newFilter ->
                openFilterBottomSheet = false
                viewModel.setFilter(newFilter)
            },
            onCustomDateRequested = {
                openDateRangePicker = true
                openFilterBottomSheet = false
            },
            onDismiss = {
                openFilterBottomSheet = false
            }
        )
    }

    if (openDateRangePicker) {
        DateRangePickerBottomSheet(
            state = dateRangeSheetState,
            dateRangePickerState = dateRangePickerState,
            onDismiss = { openDateRangePicker = false },
            formatter = formatter,
            onDateSelected = { startMs, endMs ->
                openDateRangePicker = false
                val startDate = startMs.toLocalDate()
                val endDate = endMs.toLocalDate()
                viewModel.setFilter(
                    currentFilter.copy(dateFilter = DateFilter.Custom(startDate, endDate))
                )
            }
        )
    }
}
