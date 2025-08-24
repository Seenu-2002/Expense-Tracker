package com.ajay.seenu.expensetracker.data.data_source

import com.ajay.seenu.expensetracker.domain.model.ExportFormat
import com.ajay.seenu.expensetracker.domain.model.ExportResult
import com.ajay.seenu.expensetracker.domain.model.ExportState
import kotlinx.coroutines.flow.StateFlow

interface ExportDataSource {
    val exportState: StateFlow<ExportState>
    suspend fun exportData(format: ExportFormat): ExportResult
    suspend fun getTransactionCount(): Int
    fun resetState()
}