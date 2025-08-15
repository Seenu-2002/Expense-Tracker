package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ajay.seenu.expensetracker.android.R

@Composable
fun DiscardChangesDialog(
    onDiscardConfirmed: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.dialog_discard_changes_title))
        },
        text = {
            Text(text = stringResource(R.string.dialog_discard_changes_message))
        },
        confirmButton = {
            TextButton(onClick = onDiscardConfirmed) {
                Text(
                    text = stringResource(R.string.dialog_discard_changes_positive),
                    color = MaterialTheme.colorScheme.error // red to indicate destructive action
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.dialog_discard_changes_negative))
            }
        }
    )
}
