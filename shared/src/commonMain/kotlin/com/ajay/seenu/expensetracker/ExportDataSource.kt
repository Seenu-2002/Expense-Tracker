package com.ajay.seenu.expensetracker

// ExportRepository.kt
import com.ajay.seenu.expensetracker.entity.ExportFormat
import com.ajay.seenu.expensetracker.entity.ExportResult
import com.ajay.seenu.expensetracker.entity.ExportState
import kotlinx.coroutines.flow.StateFlow

interface ExportDataSource {
    val exportState: StateFlow<ExportState>
    suspend fun exportData(format: ExportFormat): ExportResult
    suspend fun getTransactionCount(): Int
    fun resetState()
}