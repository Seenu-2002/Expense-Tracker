package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ajay.seenu.expensetracker.entity.ExportFormat
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.ExportViewModel
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.SettingsViewModel
import com.ajay.seenu.expensetracker.entity.ExportState
import com.ajay.seenu.expensetracker.entity.StartDayOfTheWeek
import com.ajay.seenu.expensetracker.entity.Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController,
                   viewModel: SettingsViewModel = hiltViewModel(),
                   exportViewModel: ExportViewModel = hiltViewModel()) {
    val configs by viewModel.userConfigs.collectAsStateWithLifecycle()
    val exportUiState by exportViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current.applicationContext
    var showWeekStartsFromBottomSheet by remember { mutableStateOf(false) }
    var showDateFormatBottomSheet by remember { mutableStateOf(false) }
    var showThemeBottomSheet by remember { mutableStateOf(false) }
    var showDeleteAllTransactionDialog by remember { mutableStateOf(false) }
    val weekStartsFromBottomSheet = rememberModalBottomSheetState()
    val dateFormatBottomSheet = rememberModalBottomSheetState()
    val themeBottomSheet = rememberModalBottomSheetState()

    val weekStartsFromStringRes = when (configs.weekStartsFrom) {
        StartDayOfTheWeek.SUNDAY -> R.string.sunday
        StartDayOfTheWeek.MONDAY -> R.string.monday
    }

    val themeStringRes = when (configs.theme) {
        Theme.LIGHT -> R.string.theme_light
        Theme.DARK -> R.string.theme_dark
        Theme.SYSTEM_THEME -> R.string.theme_system_theme
    }

    LaunchedEffect(Unit) {
        viewModel.getConfigs(context)
    }

    LaunchedEffect(weekStartsFromBottomSheet) {
        snapshotFlow { weekStartsFromBottomSheet.isVisible }.collect { it ->
            showWeekStartsFromBottomSheet = it
        }
    }

    LaunchedEffect(dateFormatBottomSheet) {
        snapshotFlow { dateFormatBottomSheet.isVisible }.collect { it ->
            showDateFormatBottomSheet = it
        }
    }

    LaunchedEffect(themeBottomSheet) {
        snapshotFlow { themeBottomSheet.isVisible }.collect { it ->
            showThemeBottomSheet = it
        }
    }
    val scope = rememberCoroutineScope()

    if (showWeekStartsFromBottomSheet) {
        val options = listOf(stringResource(R.string.sunday), stringResource(R.string.monday))
        ListBottomSheet(state = weekStartsFromBottomSheet, items = options, selectedItem = stringResource(weekStartsFromStringRes), onDismiss = {
            showWeekStartsFromBottomSheet = false
        }) { index, _ ->
            scope.launch {
                weekStartsFromBottomSheet.hide()
            }
            val newValue = StartDayOfTheWeek.entries[index]
            viewModel.changeWeekStartFromPref(newValue)
        }
    }

    if (showDateFormatBottomSheet) {
        val dateFormats = remember {
            viewModel.supportedDateFormats.map {
                context.getString(R.string.date_format_row, it.first, it.second)
            }
        }
        ListBottomSheet(state = dateFormatBottomSheet, items = dateFormats, selectedItem = configs.dateFormat, onDismiss = {
            showDateFormatBottomSheet = false
        }) { index, option ->
            scope.launch {
                dateFormatBottomSheet.hide()
            }
            viewModel.changeDateFormatPref(index, option)
        }
    }

    if (showThemeBottomSheet) {
        val options = listOf(stringResource(R.string.theme_light), stringResource(R.string.theme_dark), stringResource(R.string.theme_system_theme))
        ListBottomSheet(state = themeBottomSheet, items = options, selectedItem = stringResource(themeStringRes), onDismiss = {
            showThemeBottomSheet = false
        }) { index, _ ->
            scope.launch {
                themeBottomSheet.hide()
            }
            val newValue = Theme.entries[index]
            viewModel.changeThemePref(newValue)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 12.dp)
    ) {
        UserInfo(name = configs.name, userImagePath = configs.userImagePath)
        Spacer(Modifier.height(12.dp))
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            SettingsRowContainer(
                modifier = Modifier.fillMaxWidth(), title = "Configuration"
            ) { modifier ->
                Column(modifier.fillMaxWidth()) {
                    SettingsRow(
                        Modifier, Icons.Filled.Settings, "Theme", stringResource(themeStringRes)
                    ) {
                        showThemeBottomSheet = true
                    }
                    SettingsRowWithSwitch(Modifier,
                        Icons.Filled.Lock,
                        "App Lock",
                        configs.isAppLockEnabled,
                        isClickable = false,
                        onClick = { }
                    ) {
                        viewModel.shouldEnableAppLock(it)
                        val enabled = if(it) "enabled" else "disabled"
                        Toast.makeText(context, "App lock is $enabled", Toast.LENGTH_SHORT).show()
                    }
                    SettingsRow(
                        Modifier,
                        Icons.Filled.DateRange,
                        "Week Starts From",
                        stringResource(weekStartsFromStringRes)
                    ) {
                        showWeekStartsFromBottomSheet = true
                    }
                    SettingsRow(
                        Modifier, Icons.Filled.DateRange, "Date Format", configs.dateFormat
                    ) {
                        showDateFormatBottomSheet = true
                    }
                }
            }
            SettingsRowContainer(modifier = Modifier.fillMaxWidth(), title = "Data") { modifier ->
                Column(modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth()
                        .height(52.dp)
                        .clickable(enabled = exportUiState.exportState !is ExportState.Loading, onClick = {
                            exportViewModel.selectFormat(ExportFormat.CSV)
                            exportViewModel.exportAndSave()
                        }),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.padding(start = 12.dp),
                            painter = painterResource(R.drawable.baseline_import_export_24),
                            contentDescription = "Export Icon",
                        )
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = "Export",
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        if (exportUiState.exportState is ExportState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    }
                    Row(modifier = Modifier.fillMaxWidth()
                        .height(52.dp)
                        .clickable(onClick = {
                            showDeleteAllTransactionDialog = true
                        }),
                        verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier.padding(start = 12.dp),
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete all data",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            modifier = Modifier.padding(start = 12.dp),
                            text = "Delete all data",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
    if (exportUiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { exportViewModel.dismissSuccessDialog() },
            title = { Text("Export Successful") },
            text = { Text("Your data has been exported successfully.") },
            confirmButton = {
                TextButton(onClick = { exportViewModel.dismissSuccessDialog() }) {
                    Text("OK")
                }
            }
        )
    }
    if(showDeleteAllTransactionDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllTransactionDialog = false },
            title = { Text("Delete All Transactions") },
            text = { Text("Are you sure you want to delete all transactions? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteAllTransactions()
                    showDeleteAllTransactionDialog = false
                }) {
                    Text("Delete",
                        color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllTransactionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserInfo(modifier: Modifier = Modifier, name: String, userImagePath: String? = null) {
    Column(
        modifier = modifier.then(Modifier.fillMaxWidth()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Box(contentAlignment = Alignment.Center) {
            Image(
                modifier = Modifier
                    .padding(8.dp)
                    .size(120.dp)
                    .clip(RoundedCornerShape(60.dp)),
                painter = ColorPainter(Color.Red),
                contentDescription = "User Image"
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            modifier = Modifier.padding(bottom = 24.dp),
            text = name,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Preview
@Composable
fun UserInfoPreview(modifier: Modifier = Modifier) {
    UserInfo(name = "Seenivasan T")
}

@Composable
fun SettingsRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String? = null,
    showArrow: Boolean = true,
    isClickable: Boolean = true,
    onClick: () -> Unit,
) {
    ConstraintLayout(
        modifier.then(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable(enabled = isClickable, onClick = onClick)
        )
    ) {
        val (iconRef, labelRef, valueRef, arrowRef) = createRefs()
        Icon(modifier = Modifier.constrainAs(iconRef) {
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
            start.linkTo(parent.start, 12.dp)
        }, imageVector = icon, contentDescription = "")
        Text(modifier = Modifier.constrainAs(labelRef) {
            start.linkTo(iconRef.end, 12.dp)
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
        }, text = label, fontWeight = FontWeight.SemiBold)
        Text(modifier = Modifier.constrainAs(valueRef) {
            linkTo(
                start = labelRef.end,
                startMargin = 8.dp,
                end = arrowRef.start,
                endMargin = 8.dp,
                bias = 1F
            )
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
            visibility = if (value == null) {
                Visibility.Gone
            } else {
                Visibility.Visible
            }
        }, text = value ?: "")
        Icon(modifier = Modifier.constrainAs(arrowRef) {
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
            end.linkTo(parent.end, 4.dp)
            visibility = if (showArrow) {
                Visibility.Visible
            } else {
                Visibility.Gone
            }
        }, imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "")
    }
}

@Preview
@Composable
fun SettingsRowWithSwitchPreview(modifier: Modifier = Modifier) {
    var isChecked by remember { mutableStateOf(false) }
    SettingsRowWithSwitch(icon = Icons.Filled.Lock,
        label = "Encryption",
        isChecked = isChecked,
        onClick = { isChecked = !isChecked },
        listener = { isChecked = it })
}

@Composable
fun SettingsRowWithSwitch(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    isChecked: Boolean,
    isClickable: Boolean = true,
    onClick: () -> Unit,
    listener: (Boolean) -> Unit,
) {
    ConstraintLayout(
        modifier.then(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clickable(enabled = isClickable, onClick = onClick)
        )
    ) {
        val (iconRef, labelRef, switchRef) = createRefs()
        Icon(modifier = Modifier.constrainAs(iconRef) {
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
            start.linkTo(parent.start, 12.dp)
        }, imageVector = icon, contentDescription = "")
        Text(modifier = Modifier.constrainAs(labelRef) {
            start.linkTo(iconRef.end, 12.dp)
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
        }, text = label, fontWeight = FontWeight.SemiBold)
        Switch(checked = isChecked, modifier = Modifier.constrainAs(switchRef) {
            top.linkTo(parent.top, 8.dp)
            bottom.linkTo(parent.bottom, 8.dp)
            end.linkTo(parent.end, 12.dp)
        }, onCheckedChange = listener)
    }
}

@Preview
@Composable
fun SettingsRowContainerPreview(modifier: Modifier = Modifier) {
    SettingsRowContainer(modifier = Modifier.fillMaxWidth(), title = "Data") { modifier ->
        Column(modifier = modifier.fillMaxWidth()) {
            repeat(10) {
                Text("Sample Text ${it + 1}", modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun SettingsRowContainer(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable (Modifier) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Box(modifier = Modifier.padding(12.dp)) {
            content(
                Modifier.background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListBottomSheet(
    modifier: Modifier = Modifier,
    state: SheetState,
    items: List<String>,
    selectedItem: String?,
    onDismiss: (() -> Unit)? = null,
    onClick: (Int, String) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = {
        onDismiss?.invoke()
    }, sheetState = state) {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(items.size) {
                val text = items[it]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onClick(it, text)
                        }
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = text
                    )
                    if (text == selectedItem) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = ""
                        )
                    }
                }
            }
        }
    }
}

fun showToBeImplementedToast(context: Context) {
    Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show()
}