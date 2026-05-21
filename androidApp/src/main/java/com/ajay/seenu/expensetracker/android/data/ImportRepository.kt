package com.ajay.seenu.expensetracker.android.data

import android.content.ContentResolver
import android.net.Uri
import com.ajay.seenu.expensetracker.data.data_source.ImportDataSource
import com.ajay.seenu.expensetracker.domain.model.ImportResult
import javax.inject.Inject

class ImportRepository @Inject constructor(
    private val dataSource: ImportDataSource,
    private val contentResolver: ContentResolver
) {
    suspend fun importFromUri(uri: Uri): ImportResult {
        return try {
            val content = contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                ?: return ImportResult(success = false, errorMessage = "Could not read file")

            val fileName = uri.lastPathSegment?.lowercase() ?: ""
            val mimeType = contentResolver.getType(uri)?.lowercase() ?: ""

            when {
                fileName.endsWith(".json") || mimeType.contains("json") -> dataSource.importFromJson(content)
                fileName.endsWith(".csv") || mimeType.contains("csv") || mimeType.contains("text/plain") -> dataSource.importFromCsv(content)
                else -> {
                    // Try JSON first, fall back to CSV
                    val trimmed = content.trimStart()
                    if (trimmed.startsWith("{") || trimmed.startsWith("[")) {
                        dataSource.importFromJson(content)
                    } else {
                        dataSource.importFromCsv(content)
                    }
                }
            }
        } catch (e: Exception) {
            ImportResult(success = false, errorMessage = e.message ?: "Import failed")
        }
    }
}
