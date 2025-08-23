package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.ExportData
import com.ajay.seenu.expensetracker.entity.ExportFormat
import com.ajay.seenu.expensetracker.entity.ExportResult
import com.ajay.seenu.expensetracker.entity.ExportState
import com.ajay.seenu.expensetracker.entity.TransactionExport
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ExportDataSourceImpl(
    private val database: ExpenseDatabase,
    private val appVersion: String = "1.0.0"
) : ExportDataSource {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    override val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    override suspend fun exportData(format: ExportFormat): ExportResult {
        return try {
            _exportState.value = ExportState.Loading

            val transactions = database.expenseDatabaseQueries.getAllTransactions(Long.MAX_VALUE,
                0).executeAsList()
            val exportTransactions = transactions.map { transaction ->
                TransactionExport(
                    id = transaction.id,
                    amount = transaction.amount,
                    description = transaction.note,
                    category = transaction.category,
                    date = formatDate(transaction.date),
                    type = transaction.type
                )
            }

            val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            val exportData = ExportData(
                exportDate = getCurrentDateTime(),
                appVersion = appVersion,
                totalTransactions = transactions.size,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                transactions = exportTransactions
            )

            val content = when (format) {
                ExportFormat.JSON -> json.encodeToString(exportData)
                ExportFormat.CSV -> generateCsv(exportTransactions)
            }

            val fileName = generateFileName(format)
            val result = ExportResult(
                success = true,
                data = content,
                fileName = fileName
            )

            _exportState.value = ExportState.Success(result)
            result

        } catch (e: Exception) {
            val errorResult = ExportResult(
                success = false,
                errorMessage = e.message ?: "Export failed"
            )
            _exportState.value = ExportState.Error(e.message ?: "Export failed")
            errorResult
        }
    }

    override suspend fun getTransactionCount(): Int {
        return try {
            database.expenseDatabaseQueries.getTransactionsCount().executeAsOne().toInt()
        } catch (e: Exception) {
            0
        }
    }

    override fun resetState() {
        _exportState.value = ExportState.Idle
    }

    private fun generateCsv(transactions: List<TransactionExport>): String {
        val header = "ID,Amount,Description,Category,Date,Type"
        val rows = transactions.joinToString("\n") { transaction ->
            "${transaction.id}," +
                    "${transaction.amount}," +
                    "\"${transaction.description?.replace("\"", "\"\"\"")}\"," +
                    "\"${transaction.category}\"," +
                    "\"${transaction.date}\"," +
                    "\"${transaction.type}\""
                                }
        return "$header\n$rows"
    }

    private fun generateFileName(format: ExportFormat): String {
        val timestamp = getCurrentDateTime().replace(":", "-").replace(" ", "_")
        return "expense_tracker_export_${timestamp}.${format.extension}"
    }

    private fun formatDate(timestamp: Long): String {
        val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.time}"
    }

    private fun getCurrentDateTime(): String {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.time}"
    }
}