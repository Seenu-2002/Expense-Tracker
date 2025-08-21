package com.ajay.seenu.expensetracker.android.presentation.screeens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajay.seenu.expensetracker.Budget
import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.entity.budget.BudgetPeriodType
import com.ajay.seenu.expensetracker.entity.budget.BudgetRequest
import kotlin.collections.forEach
import kotlin.text.ifEmpty

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
    var receiveAlert by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.primary
            )
    ) {
        // Top App Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    if (isEdit) "Edit Budget" else "Create Budget",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
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

        Spacer(modifier = Modifier.height(32.dp))

        // Amount Input Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "How much do you want to spend?",
                color = Color.White,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = amount.ifEmpty { "0" },
                prefix = {
                    Text(text = "$", color = Color.White, fontSize = 24.sp,
                        fontWeight = FontWeight.Bold)
                },
                onValueChange = {
                    amount = it.filter { char -> char.isDigit() || char == '$' }
                        .replace("$", "")
                        .takeIf { it.isNotEmpty() } ?: "0"
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 64.sp,
                )
            )
//            Text(
//                "$${amount.ifEmpty { "0" }}",
//                color = Color.White,
//                fontSize = 64.sp,
//                fontWeight = FontWeight.Bold
//            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Form Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Category Dropdown
                var expandedCategory by remember { mutableStateOf(false) }
                val selectedCategoryName = categories.find { it.id == selectedCategory }?.label ?: "Category"

                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.label) },
                                onClick = {
                                    selectedCategory = category.id
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Receive Alert Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Receive Alert",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray800
                        )
                        Text(
                            "Receive alert when it reaches\nsome point",
                            fontSize = 13.sp,
                            color = Gray600,
                            lineHeight = 16.sp
                        )
                    }

                    Switch(
                        checked = receiveAlert,
                        onCheckedChange = { receiveAlert = it },
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Continue Button
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
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
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
}