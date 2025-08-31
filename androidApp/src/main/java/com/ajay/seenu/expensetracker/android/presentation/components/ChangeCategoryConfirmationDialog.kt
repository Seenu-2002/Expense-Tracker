package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.R

@Composable
fun ChangeConfirmationDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    confirmButtonText: String = stringResource(R.string.action_delete),
    dismissButtonText: String = stringResource(R.string.action_cancel),
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        title = { Text(text = title) },
        text = {
            Text(
                text = message
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmButtonText, color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissButtonText,
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}