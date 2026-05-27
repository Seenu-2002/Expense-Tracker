package com.ajay.seenu.expensetracker.android.presentation.screeens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.budget.Budget
import com.ajay.seenu.expensetracker.domain.model.budget.BudgetRequest
import kotlin.text.ifEmpty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedCreateBudgetScreen(
    isEdit: Boolean = false,
    initialBudget: Budget? = null,
    categories: List<Category> = emptyList(),
    onSave: (BudgetRequest) -> Unit,
    onNavigateBack: () -> Unit
) {
    var currentStep by remember { mutableStateOf(if (isEdit) 1 else 0) }
    var amount by remember { mutableStateOf(initialBudget?.amount?.toInt()?.toString() ?: "") }
    var selectedCategory by remember { mutableStateOf(initialBudget?.categoryId ?: categories.firstOrNull()?.id) }
    var receiveAlert by remember { mutableStateOf(true) }
    var showNumpad by remember { mutableStateOf(false) }

    when (currentStep) {
        0 -> {
            // Amount Input Step
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
                            "Create Budget",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
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
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Amount Display
                    Box(
                        modifier = Modifier.clickable { showNumpad = true }
                    ) {
                        Text(
                            "${amount.ifEmpty { "0" }}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Custom Numpad
                if (showNumpad) {
                    NumberPad(
                        value = amount,
                        onValueChange = { amount = it },
                        onDone = {
                            showNumpad = false
                            if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                                currentStep = 1
                            }
                        }
                    )
                } else {
                    // Continue Button
                    Button(
                        onClick = {
                            if (amount.isNotEmpty() && amount.toDoubleOrNull() != null) {
                                currentStep = 1
                            } else {
                                showNumpad = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(bottom = 32.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            "Continue",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        1 -> {
            // Category and Settings Step
            AddEditBudgetScreen(
                arg = if(isEdit && initialBudget!= null) AddEditBudgetArg.Edit(initialBudget) else AddEditBudgetArg.Create,
                categories = categories,
                onSave = onSave,
                onNavigateBack = { currentStep = 0 }
            )
        }
    }
}

@Composable
fun NumberPad(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Number grid
            val numbers = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("", "0", "⌫")
            )

            numbers.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { number ->
                        NumberPadButton(
                            text = number,
                            onClick = {
                                when (number) {
                                    "⌫" -> {
                                        if (value.isNotEmpty()) {
                                            onValueChange(value.dropLast(1))
                                        }
                                    }
                                    "" -> { /* Empty space */ }
                                    else -> {
                                        if (value.length < 10) { // Limit to reasonable length
                                            onValueChange(value + number)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Done Button
            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Done",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun NumberPadButton(
    text: String,
    onClick: () -> Unit
) {
    if (text.isEmpty()) {
        Spacer(modifier = Modifier.size(64.dp))
    } else {
        Card(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}