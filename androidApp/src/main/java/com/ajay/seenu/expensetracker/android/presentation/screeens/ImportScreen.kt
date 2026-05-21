package com.ajay.seenu.expensetracker.android.presentation.screeens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.ImportState
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.ImportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportScreen(
    viewModel: ImportViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val jsonPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importFile(it) }
    }
    val csvPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.importFile(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Transactions") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val s = state) {
                ImportState.Idle -> IdleContent(
                    onPickJson = { jsonPicker.launch("*/*") },
                    onPickCsv = { csvPicker.launch("*/*") }
                )

                ImportState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Importing transactions…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                is ImportState.Success -> SuccessContent(
                    importedCount = s.importedCount,
                    onImportMore = { viewModel.reset() },
                    onDone = onNavigateBack
                )

                is ImportState.Error -> ErrorContent(
                    message = s.message,
                    onRetry = { viewModel.reset() }
                )
            }
        }
    }
}

@Composable
private fun IdleContent(onPickJson: () -> Unit, onPickCsv: () -> Unit) {
    Text(
        text = "Import Transactions",
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Restore your transactions from a previously exported file. Both JSON and CSV formats are supported.",
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(32.dp))
    Button(
        onClick = onPickJson,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Import from JSON (.json)")
    }
    Spacer(Modifier.height(12.dp))
    OutlinedButton(
        onClick = onPickCsv,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Import from CSV (.csv)")
    }
}

@Composable
private fun SuccessContent(importedCount: Int, onImportMore: () -> Unit, onDone: () -> Unit) {
    Icon(
        imageVector = Icons.Filled.CheckCircle,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(64.dp)
    )
    Spacer(Modifier.height(16.dp))
    Text(
        text = "Import Complete",
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "$importedCount transaction${if (importedCount == 1) "" else "s"} imported successfully.",
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(32.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(onClick = onImportMore, modifier = Modifier.weight(1f)) {
            Text("Import More")
        }
        Button(onClick = onDone, modifier = Modifier.weight(1f)) {
            Text("Done")
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Text(
        text = "Import Failed",
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.error
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = message,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(32.dp))
    Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
        Text("Try Again")
    }
}
