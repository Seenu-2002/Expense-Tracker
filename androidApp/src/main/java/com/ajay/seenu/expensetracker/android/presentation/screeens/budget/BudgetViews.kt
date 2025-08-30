package com.ajay.seenu.expensetracker.android.presentation.screeens.budget

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

val Orange = Color(0xFFF59E0B)
val Gray100 = Color(0xFFF3F4F6)
val Gray600 = Color(0xFF4B5563)
val Gray800 = Color(0xFF1F2937)

@Composable
fun DeleteBudgetDialog(
    budgetName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Remove this budget?",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text(
                "Are you sure do you wanna remove this budget?",
                color = Gray600
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("No", color = Gray600)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}