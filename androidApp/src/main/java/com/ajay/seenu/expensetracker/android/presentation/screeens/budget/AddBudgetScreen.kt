package com.ajay.seenu.expensetracker.android.presentation.screeens.budget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.TransactionFieldView
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetPeriodType
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetRequest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    isEdit: Boolean = false,
    initialBudget: Budget? = null,
    categories: List<Category> = emptyList(),
    onSave: (BudgetRequest) -> Unit,
    onNavigateBack: () -> Unit
) {
    var amount by remember { mutableStateOf(initialBudget?.amount?.toInt()?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(initialBudget?.categoryId ?: categories.firstOrNull()?.id) }
    var receiveAlert by rememberSaveable { mutableStateOf(false) }
    var showAmountError by rememberSaveable {
        mutableStateOf(false)
    }
    var showCategoryError by rememberSaveable {
        mutableStateOf(false)
    }
    val interactionSource = remember { MutableInteractionSource() }
    val sliderState = remember { SliderState(value = 0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    if (isEdit) "Edit Budget" else "Create Budget",
                    color = Color.White,
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                "How much do you want to spend?",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = amount,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 60.sp,
                    color = Color.White
                ),
                singleLine = true,
                onValueChange = {
                    amount = it
                    showAmountError = it.isEmpty()
                },
                decorationBox = { innerTextField ->
                    OutlinedTextFieldDefaults.DecorationBox(
                        value = amount,
                        innerTextField = innerTextField,
                        prefix = {
                            Text(text = "$",
                                style = LocalTextStyle.current.copy(
                                    color = Color.White,
                                    fontSize = 60.sp
                                )
                            )
                        },
                        enabled = true,
                        singleLine = true,
                        interactionSource = interactionSource,
                        visualTransformation = VisualTransformation.None,
                        contentPadding = PaddingValues(0.dp),
                        supportingText = {
                            if (showAmountError) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 10.dp),
                                    text = stringResource(R.string.enter_the_amount), // FIXME: String resource
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        isError = showAmountError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            errorBorderColor = Color.Transparent
                        ),
                    )
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.background,
                    RoundedCornerShape(
                        topStart = 25.dp,
                        topEnd = 25.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .padding(start = 15.dp, end = 15.dp, top = 40.dp, bottom = 60.dp),
        ) {
            var expandedCategory by remember { mutableStateOf(false) }
            val selectedCategoryName = categories.find { it.id == selectedCategory }?.label ?: "Category"

            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                TransactionFieldView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    text = selectedCategoryName,
                    onClick = {},
                    isError = showCategoryError,
                    errorView = {
                        if(showCategoryError) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                                text = "Enter the category", // FIXME: String resource
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false },
                    containerColor = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.label) },
                            onClick = {
                                selectedCategory = category.id
                                expandedCategory = false
                            },
                            trailingIcon = {
                                if(selectedCategoryName == category.label) {
                                    Icon(
                                        painter = painterResource(R.drawable.icon_done),
                                        contentDescription = "Selected",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else null
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Receive Alert",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        "Receive alert when it reaches\nsome point",
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    )
                }

                Switch(
                    checked = receiveAlert,
                    onCheckedChange = { receiveAlert = it },
                )
            }
            AnimatedVisibility(receiveAlert) {
                Slider(
                    modifier = Modifier.padding(top = 32.dp),
                    state = sliderState,
                    colors = SliderDefaults.colors(
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = LocalContentColor.current.copy(alpha = 0.2f),
                    ),
                    thumb = {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp)
                                .size(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${(it.value * 100).roundToInt()}%",
                                fontSize = 12.sp,
                                color = Color.White,
                            )
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (amount.isNotEmpty() && selectedCategory != null) {
                        onSave(
                            BudgetRequest(
                                name = selectedCategoryName,
                                categoryId = selectedCategory,
                                amount = amount.toDoubleOrNull() ?: 0.0,
                                periodType = BudgetPeriodType.MONTHLY
                            )
                        )
                    } else {
                        showAmountError = amount.isEmpty()
                        showCategoryError = selectedCategory == null
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 15.dp)
            ) {
                Text(
                    "Continue",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}