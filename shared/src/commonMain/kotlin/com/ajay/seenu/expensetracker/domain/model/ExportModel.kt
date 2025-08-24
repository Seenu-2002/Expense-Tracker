package com.ajay.seenu.expensetracker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TransactionExport constructor(
    val id: Long,
    val amount: Double,
    val description: String?,
    val category: Long,
    val date: String,
    val type: TransactionType
)

@Serializable
data class ExportData(
    val exportDate: String,
    val appVersion: String,
    val totalTransactions: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val transactions: List<TransactionExport>
)

enum class ExportFormat(val displayName: String, val extension: String, val mimeType: String) {
    JSON("JSON", "json", "application/json"),
    CSV("CSV", "csv", "text/csv")
}

data class ExportResult(
    val success: Boolean,
    val data: String? = null,
    val fileName: String? = null,
    val errorMessage: String? = null
)

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val result: ExportResult) : ExportState()
    data class Error(val message: String) : ExportState()
}