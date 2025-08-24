package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.data.data_source.ExportDataSource
import com.ajay.seenu.expensetracker.domain.FileManager
import com.ajay.seenu.expensetracker.domain.model.ExportFormat
import com.ajay.seenu.expensetracker.domain.model.ExportState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ExportRepository @Inject constructor(
    private val dataSource: ExportDataSource,
    private val fileManager: FileManager
) {
    val exportState: StateFlow<ExportState> = dataSource.exportState

    suspend fun exportAndSave(format: ExportFormat): Boolean {
        val result = dataSource.exportData(format)

        return if (result.success && result.data != null && result.fileName != null) {
            fileManager.saveFile(
                content = result.data!!,
                fileName = result.fileName!!,
                mimeType = format.mimeType
            )
        } else {
            false
        }
    }

    suspend fun exportAndShare(format: ExportFormat): Boolean {
        val result = dataSource.exportData(format)

        return if (result.success && result.data != null && result.fileName != null) {
            fileManager.shareFile(
                content = result.data!!,
                fileName = result.fileName!!,
                mimeType = format.mimeType
            )
        } else {
            false
        }
    }

    suspend fun getTransactionCount(): Int = dataSource.getTransactionCount()

    fun resetExportState() = dataSource.resetState()
}