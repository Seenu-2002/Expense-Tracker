package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ConstraintLayout
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.util.resetTime
import com.ajay.seenu.expensetracker.android.util.tillMidNight
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.DateFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import com.ajay.seenu.expensetracker.util.getDateLabel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    filter: TransactionFilter,
    categories: List<Category> = emptyList(),
    accounts: List<Account> = emptyList(),
    showTypeFilter: Boolean = true,
    formatter: SimpleDateFormat,
    onFilterSelected: (TransactionFilter) -> Unit,
    onCustomDateRequested: () -> Unit = {},
    onDismiss: (() -> Unit)? = null,
) {
    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = { onDismiss?.invoke() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filter",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                if (filter.hasActiveFilters || filter.dateFilter != DateFilter.ThisMonth) {
                    TextButton(onClick = {
                        onFilterSelected(TransactionFilter())
                    }) {
                        Text("Reset all")
                    }
                }
            }

            HorizontalDivider()

            // --- Date section ---
            FilterSectionHeader("Date")
            Column(modifier = Modifier.fillMaxWidth()) {
                FilterBottomSheetRow(
                    modifier = Modifier.clickable {
                        onFilterSelected(filter.copy(dateFilter = DateFilter.ThisWeek))
                    },
                    isSelected = filter.dateFilter == DateFilter.ThisWeek,
                    text = stringResource(R.string.filter_this_week)
                )
                FilterBottomSheetRow(
                    modifier = Modifier.clickable {
                        onFilterSelected(filter.copy(dateFilter = DateFilter.ThisMonth))
                    },
                    isSelected = filter.dateFilter == DateFilter.ThisMonth,
                    text = stringResource(R.string.filter_this_month)
                )
                FilterBottomSheetRow(
                    modifier = Modifier.clickable {
                        onFilterSelected(filter.copy(dateFilter = DateFilter.ThisYear))
                    },
                    isSelected = filter.dateFilter == DateFilter.ThisYear,
                    text = stringResource(R.string.filter_this_year)
                )
                val customText = if (filter.dateFilter is DateFilter.Custom) {
                    val df = filter.dateFilter as DateFilter.Custom
                    stringResource(
                        R.string.filter_custom_arg,
                        stringResource(
                            R.string.filter_custom_arg_format,
                            df.startDate.getDateLabel(),
                            df.endDate.getDateLabel()
                        )
                    )
                } else {
                    stringResource(R.string.filter_custom)
                }
                FilterBottomSheetRow(
                    modifier = Modifier.clickable { onCustomDateRequested() },
                    isSelected = filter.dateFilter is DateFilter.Custom,
                    text = customText
                )
            }

            if (showTypeFilter) {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

                // --- Type section ---
                FilterSectionHeader("Type")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filter.type == null,
                        onClick = { onFilterSelected(filter.copy(type = null)) },
                        label = { Text("All") }
                    )
                    FilterChip(
                        selected = filter.type == TransactionType.INCOME,
                        onClick = {
                            val newType = if (filter.type == TransactionType.INCOME) null else TransactionType.INCOME
                            onFilterSelected(filter.copy(type = newType))
                        },
                        label = { Text("Income") }
                    )
                    FilterChip(
                        selected = filter.type == TransactionType.EXPENSE,
                        onClick = {
                            val newType = if (filter.type == TransactionType.EXPENSE) null else TransactionType.EXPENSE
                            onFilterSelected(filter.copy(type = newType))
                        },
                        label = { Text("Expense") }
                    )
                }
            }

            // --- Category section ---
            if (categories.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                FilterSectionHeader(
                    title = "Category",
                    badge = if (filter.categoryIds.isNotEmpty()) "${filter.categoryIds.size}" else null,
                    onClear = if (filter.categoryIds.isNotEmpty()) {
                        { onFilterSelected(filter.copy(categoryIds = emptySet())) }
                    } else null
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = category.id in filter.categoryIds
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newIds = if (isSelected)
                                    filter.categoryIds - category.id
                                else
                                    filter.categoryIds + category.id
                                onFilterSelected(filter.copy(categoryIds = newIds))
                            },
                            label = { Text(category.label) }
                        )
                    }
                }
            }

            // --- Account section ---
            if (accounts.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
                FilterSectionHeader(
                    title = "Account",
                    badge = if (filter.accountIds.isNotEmpty()) "${filter.accountIds.size}" else null,
                    onClear = if (filter.accountIds.isNotEmpty()) {
                        { onFilterSelected(filter.copy(accountIds = emptySet())) }
                    } else null
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.forEach { account ->
                        val isSelected = account.id in filter.accountIds
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val newIds = if (isSelected)
                                    filter.accountIds - account.id
                                else
                                    filter.accountIds + account.id
                                onFilterSelected(filter.copy(accountIds = newIds))
                            },
                            label = { Text(account.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterSectionHeader(
    title: String,
    badge: String? = null,
    onClear: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        if (badge != null) {
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = "($badge)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (onClear != null) {
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = onClear,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text("Clear", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun FilterBottomSheetRow(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    text: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text)
        if (isSelected) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Check, contentDescription = "selected")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerBottomSheet(
    state: SheetState,
    dateRangePickerState: DateRangePickerState,
    formatter: SimpleDateFormat,
    onDismiss: (() -> Unit)? = null,
    onDateSelected: ((Long, Long) -> Unit)? = null,
) {
    val context = LocalContext.current
    val dateFormatter = remember {
        DatePickerDefaults.dateFormatter() // TODO: Should be user configured format
    }
    val datePickerBottomPadding = if (dateRangePickerState.displayMode == DisplayMode.Input) {
        36.dp
    } else {
        0.dp
    }
    val startDateInMillis = dateRangePickerState.selectedStartDateMillis
    val endDateInMillis = dateRangePickerState.selectedEndDateMillis
    val isValidDateRange =
        startDateInMillis != null && endDateInMillis != null

    ModalBottomSheet(modifier = Modifier, sheetState = state, onDismissRequest = {
        onDismiss?.invoke()
    }) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (datePicker, button) = createRefs()
            DateRangePicker(
                dateRangePickerState,
                modifier = Modifier
                    .heightIn(
                        0.dp,
                        LocalConfiguration.current.screenHeightDp * .7.dp
                    )
                    .constrainAs(datePicker) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom, datePickerBottomPadding)
                        end.linkTo(parent.end)
                        start.linkTo(parent.start)
                    },
                dateFormatter = dateFormatter,
                title = {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        text = context.getString(R.string.select_dates)
                    )
                },
                headline = {
                    SelectedDateText(dateRangePickerState, Modifier.fillMaxWidth(), formatter)
                },
            )
            Button(
                modifier = Modifier
                    .constrainAs(button) {
                        bottom.linkTo(parent.bottom, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                    },
                elevation = ButtonDefaults.buttonElevation(8.dp),
                enabled = isValidDateRange, onClick = {
                    if (isValidDateRange && startDateInMillis != null && endDateInMillis != null) {
                        val calendar = Calendar.getInstance()
                        calendar.time = Date(startDateInMillis)
                        calendar.resetTime()
                        val startDate = calendar.time
                        calendar.time = Date(endDateInMillis)
                        calendar.tillMidNight()
                        val endDate = calendar.time
                        onDateSelected?.invoke(
                            startDate.time,
                            endDate.time
                        )
                    }
                }, shape = RoundedCornerShape(6.dp)
            ) {
                Text("Done")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun SelectedDateTextPreview() {
    SelectedDateText(
        DateRangePickerState(
            initialSelectedStartDateMillis = 1727721000000,
            initialSelectedEndDateMillis = 1728837566062,
            locale = Locale.ENGLISH
        ),
        formatter = SimpleDateFormat(
            "dd MMM, yyyy",
            Locale.ENGLISH
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectedDateText(
    dateRangePickerState: DateRangePickerState,
    modifier: Modifier = Modifier,
    formatter: SimpleDateFormat,
) {
    val startDate = dateRangePickerState.selectedStartDateMillis
    val endDate = dateRangePickerState.selectedEndDateMillis
    val context = LocalContext.current
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val startDateText = if (startDate == null) {
            context.getString(R.string.start_date)
        } else {
            formatter.format(Date(startDate))
        }
        val endDateText = if (endDate == null) {
            context.getString(R.string.end_date)
        } else {
            formatter.format(Date(endDate))
        }

        Text(text = stringResource(R.string.date_range, startDateText, endDateText))
    }
}
