package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.constraintlayout.compose.ConstraintLayout
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.data.resetTime
import com.ajay.seenu.expensetracker.android.data.tillMidNight
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    filter: Filter,
    formatter: SimpleDateFormat,
    onFilterSelected: (Filter) -> Unit,
    onDismiss: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    ModalBottomSheet(modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = { onDismiss?.invoke() }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            FilterBottomSheetRow(modifier = Modifier.clickable {
                onFilterSelected.invoke(Filter.ThisWeek)
            },
                isSelected = filter == Filter.ThisWeek,
                text = stringResource(R.string.filter_this_week))

            FilterBottomSheetRow(modifier = Modifier.clickable {
                onFilterSelected.invoke(Filter.ThisMonth)
            },
                isSelected = filter == Filter.ThisMonth,
                text = stringResource(R.string.filter_this_month))

            FilterBottomSheetRow(modifier = Modifier.clickable {
                onFilterSelected.invoke(Filter.ThisYear)
            },
                isSelected = filter == Filter.ThisYear,
                text = stringResource(R.string.filter_this_year))

            val text = if (filter is Filter.Custom) {
                val arg = context.getString(
                    R.string.filter_custom_arg_format, formatter.format(
                        Date(filter.startDate)
                    ), formatter.format(Date(filter.endDate))
                )
                stringResource(R.string.filter_custom_arg, arg)
            } else {
                stringResource(R.string.filter_custom)
            }

            FilterBottomSheetRow(modifier = Modifier.clickable {
                onFilterSelected.invoke(Filter.CUSTOM_MOCK)
            },
                isSelected = filter is Filter.Custom,
                text = text)
        }
    }
}

@Composable
fun FilterBottomSheetRow(modifier: Modifier = Modifier,
                         isSelected: Boolean,
                         text: String) {
    Row(modifier = modifier.fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(text = text)
        if(isSelected) {
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