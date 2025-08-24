package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.R

@Composable
fun CategoryChangeConfirmationDialog(
    modifier: Modifier = Modifier,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        title = { Text(text = stringResource(R.string.replace_category_title)) },
        text = {
            Text(
                text = message
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.action_replace_delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.action_cancel),
                    modifier = Modifier.padding(8.dp)
                )
            }
        },
        onDismissRequest = onDismiss,
    )
}