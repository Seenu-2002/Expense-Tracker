package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.domain.data.Error
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.data.UiState
import com.ajay.seenu.expensetracker.android.presentation.common.CategoryDefaults
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.AddEditCategoryViewModel
import com.ajay.seenu.expensetracker.android.presentation.widgets.AdaptiveVerticalGrid
import com.ajay.seenu.expensetracker.android.presentation.widgets.DiscardChangesDialog
import com.ajay.seenu.expensetracker.android.presentation.widgets.ProgressDialog
import com.ajay.seenu.expensetracker.android.presentation.widgets.SlidingSwitch

val ColorSaver = run {
    val redKey = "Red"
    val greenKey = "Green"
    val blueKey = "Blue"
    mapSaver(
        save = { mapOf(redKey to it.red, greenKey to it.green, blueKey to it.blue) },
        restore = {
            Color(
                red = it[redKey] as Float,
                green = it[greenKey] as Float,
                blue = it[blueKey] as Float
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    arg: AddEditScreenArg,
    onNavigateBack: () -> Unit,
    viewModel: AddEditCategoryViewModel = hiltViewModel()
) {

    val status: UiState<String>? by viewModel.status.collectAsStateWithLifecycle()
    val isInEditMode = arg is AddEditScreenArg.Edit
    val categoryId = if (isInEditMode) {
        (arg as AddEditScreenArg.Edit).id
    } else {
        null
    }

    val categoryState = viewModel.category.collectAsStateWithLifecycle().value

    LaunchedEffect(categoryState == UiState.Empty) {
        if (categoryId != null) {
            viewModel.getCategory(categoryId)
        }
    }

    val category = (categoryState as? UiState.Success<Transaction.Category>)?.data

    var selectedIcon: Int? by rememberSaveable(category) {
        mutableStateOf(category?.res ?: CategoryDefaults.categoryIconsList.random())
    }
    var title: String by rememberSaveable(category) {
        mutableStateOf(category?.label ?: "")
    }
    var iconBackgroundColor: Color by rememberSaveable(category, stateSaver = ColorSaver) {
        mutableStateOf(category?.color ?: CategoryDefaults.categoryColors.random())
    }
    var transactionType: Transaction.Type by rememberSaveable(category) {
        mutableStateOf(category?.type ?: Transaction.Type.INCOME)
    }

    var showError by rememberSaveable { mutableStateOf(false) }
    var showExitDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    val stringRes = if (isInEditMode) {
                        R.string.update_category_title
                    } else {
                        R.string.create_category_title
                    }
                    Text(
                        stringResource(stringRes),
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    IconButton(onClick = {

                        // TODO: Add onBackPressed logic as well
                        if (isInEditMode) {
                            if (title != category?.label ||
                                selectedIcon != category.res ||
                                iconBackgroundColor != category.color ||
                                transactionType != category.type
                            ) {
                                showExitDialog = true
                            } else {
                                onNavigateBack()
                            }
                        } else {
                            if (title.isNotBlank()) {
                                showExitDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }

                    }) {
                        Icon(
                            modifier = Modifier
                                .clip(RoundedCornerShape(percent = 50))
                                .padding(8.dp),
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {

            if (categoryId != null && category == null) {
                return@Scaffold
            }

            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = {
                    if (title.isNotBlank()) {
                        if (arg is AddEditScreenArg.Edit) {
                            val category = category!!
                            val hasChanges =
                                category.label != title || category.color != iconBackgroundColor || category.res != selectedIcon

                            if (!hasChanges) {
                                Toast.makeText(
                                    context,
                                    R.string.no_changes_made,
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@ExtendedFloatingActionButton
                            }

                            viewModel.updateCategory(
                                id = categoryId!!,
                                label = title,
                                type = transactionType,
                                color = iconBackgroundColor,
                                res = selectedIcon ?: CategoryDefaults.categoryIconsList.random()
                            )
                        } else {
                            viewModel.createCategory(
                                label = title,
                                type = transactionType,
                                color = iconBackgroundColor,
                                res = selectedIcon ?: CategoryDefaults.categoryIconsList.random()
                            )
                        }
                    } else {
                        showError = true
                        Toast.makeText(
                            context,
                            R.string.error_empty_category_title,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                },
                shape = CircleShape,
                text = {
                    val text = if (isInEditMode) {
                        stringResource(R.string.update)
                    } else {
                        stringResource(R.string.create)
                    }
                    Text(text = text)
                }, icon = {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        contentDescription = null,
                        tint = Color.White
                    )
                })
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val values = Transaction.Type.entries.map { stringResource(it.stringRes) }
                if (arg is AddEditScreenArg.Create) {
                    val type = arg.type
                    SlidingSwitch(
                        selectedValue = stringResource(type.stringRes),
                        values = values,
                        modifier = Modifier.widthIn(max = 600.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) { index, _ ->
                        transactionType = Transaction.Type.entries[index]
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryIconItem(
                        modifier = Modifier
                            .size(52.dp)
                            .padding(4.dp),
                        res = selectedIcon,
                        iconBackground = iconBackgroundColor
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(1F),
                        value = title,
                        onValueChange = {
                            title = it
                            showError = false
                        },
                        placeholder = {
                            Text(stringResource(R.string.category_name))
                        },
                        singleLine = true,
                        isError = showError,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = true,
                            capitalization = KeyboardCapitalization.Sentences
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .padding(
                            8.dp
                        )
                        .weight(1F)
                        .verticalScroll(state = rememberScrollState()),
                ) {
                    Text(
                        text = stringResource(R.string.icons),
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    )
                    AdaptiveVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        adaptiveCellSize = 48.dp,
                    ) {
                        for (icon in CategoryDefaults.categoryIconsList) {
                            CategoryIconItem(
                                modifier = Modifier
                                    .size(52.dp)
                                    .padding(4.dp),
                                res = icon
                            ) { res ->
                                selectedIcon = res
                            }
                        }
                    }
                    Text(
                        text = stringResource(R.string.colors),
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    AdaptiveVerticalGrid(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize(),
                        adaptiveCellSize = 48.dp,
                    ) {
                        for (color in CategoryDefaults.categoryColors) {
                            CategoryColorItem(
                                modifier = Modifier
                                    .size(52.dp)
                                    .padding(4.dp),
                                color = color,
                                isSelected = color == iconBackgroundColor
                            ) { color ->
                                iconBackgroundColor = color
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        if (showExitDialog) {
            DiscardChangesDialog(onDiscardConfirmed = {
                showExitDialog = false
                onNavigateBack()
            }, onDismiss = {
                showExitDialog = false
            })
        }

        if (arg is AddEditScreenArg.Edit && categoryState is UiState.Loading) {
            ProgressDialog()
        }

        when (val status = status) {
            is UiState.Loading -> ProgressDialog()
            is UiState.Success -> {
                LaunchedEffect(status) {
                    val stringResId = if (isInEditMode) {
                        R.string.category_updated
                    } else {
                        R.string.category_created
                    }
                    Toast.makeText(
                        context,
                        context.getString(stringResId, status.data),
                        Toast.LENGTH_SHORT
                    ).show()
                    onNavigateBack()
                }
            }

            is UiState.Failure -> {
                LaunchedEffect(status) {
                    if (status.error is Error.CategoryAlreadyPresent) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_category_already_exists),
                            Toast.LENGTH_SHORT
                        ).show()
                        showError = true
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_category_not_created),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            else -> {}
        }
    }

}

@Preview
@Composable
fun CategoryIconItem(
    modifier: Modifier = Modifier,
    @DrawableRes res: Int? = R.drawable.bolt,
    iconBackground: Color = Color(0xFF3F7BAA),
    iconSize: Dp = 24.dp,
    onClicked: ((Int?) -> Unit)? = null
) {
    val isBackgroundDark = iconBackground.luminance() < 0.5f
    val iconColor = if (isBackgroundDark) Color.White else Color.Black
    Box(
        modifier = modifier
            .background(iconBackground, CircleShape)
            .clip(CircleShape)
            .clickable(enabled = onClicked != null) {
                onClicked?.invoke(res)
            },
        contentAlignment = Alignment.Center
    ) {
        res?.let {
            Image(
                modifier = Modifier.size(iconSize),
                painter = painterResource(res),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = iconColor)
            )
        }
    }
}

@Preview
@Composable
fun CategoryColorItem(
    modifier: Modifier = Modifier,
    isSelected: Boolean = true,
    color: Color = Color(0xFF3F7BAA),
    onClicked: ((Color) -> Unit)? = null
) {
    Box(
        modifier = modifier
            .background(color, CircleShape)
            .clip(CircleShape)
            .clickable(enabled = onClicked != null, indication = null, interactionSource = null) {
                onClicked?.invoke(color)
            },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Image(
                modifier = Modifier
                    .matchParentSize()
                    .background(color = Color(0x33000000))
                    .padding(8.dp),
                painter = painterResource(R.drawable.check),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = Color.White)
            )
        }
    }
}

sealed interface AddEditScreenArg {
    data class Create(val type: Transaction.Type) : AddEditScreenArg
    data class Edit(val id: Long) : AddEditScreenArg
}