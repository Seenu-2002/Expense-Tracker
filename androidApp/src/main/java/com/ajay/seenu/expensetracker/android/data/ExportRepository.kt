package com.ajay.seenu.expensetracker.android.data

import com.ajay.seenu.expensetracker.android.util.ExcelGenerator
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
        return when (format) {
            ExportFormat.XLSX -> exportXlsx(share = false)
            else -> {
                val result = dataSource.exportData(format)
                if (result.success && result.data != null && result.fileName != null) {
                    fileManager.saveFile(
                        content = result.data!!,
                        fileName = result.fileName!!,
                        mimeType = format.mimeType
                    )
                } else {
                    false
                }
            }
        }
    }

    suspend fun exportAndShare(format: ExportFormat): Boolean {
        return when (format) {
            ExportFormat.XLSX -> exportXlsx(share = true)
            else -> {
                val result = dataSource.exportData(format)
                if (result.success && result.data != null && result.fileName != null) {
                    fileManager.shareFile(
                        content = result.data!!,
                        fileName = result.fileName!!,
                        mimeType = format.mimeType
                    )
                } else {
                    false
                }
            }
        }
    }

    private suspend fun exportXlsx(share: Boolean): Boolean {
        return try {
            val exportData = dataSource.getExportData()
            val bytes = ExcelGenerator.generate(exportData)
            val timestamp = exportData.exportDate.replace(":", "-").replace(" ", "_")
            val fileName = "expense_tracker_export_${timestamp}.xlsx"

            if (share) {
                fileManager.shareBytes(bytes, fileName, ExportFormat.XLSX.mimeType)
            } else {
                fileManager.saveBytes(bytes, fileName, ExportFormat.XLSX.mimeType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getTransactionCount(): Int = dataSource.getTransactionCount()

    fun resetExportState() = dataSource.resetState()
}