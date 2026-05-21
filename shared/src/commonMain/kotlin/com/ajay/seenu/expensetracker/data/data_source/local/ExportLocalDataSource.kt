package com.ajay.seenu.expensetracker.data.data_source.local

import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.data_source.ExportDataSource
import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.ExportData
import com.ajay.seenu.expensetracker.domain.model.ExportFormat
import com.ajay.seenu.expensetracker.domain.model.ExportResult
import com.ajay.seenu.expensetracker.domain.model.ExportState
import com.ajay.seenu.expensetracker.domain.model.TransactionExport
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class ExportLocalDataSource constructor(
    private val transactionRepository: TransactionRepository,
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

            val totalCount = getTransactionCount()
            val transactions = if (totalCount > 0) {
                transactionRepository.getAllTransactions(1, totalCount).data
            } else {
                emptyList()
            }
            val exportTransactions = transactions.map { transaction ->
                TransactionExport(
                    id = transaction.id,
                    amount = transaction.amount,
                    description = transaction.note,
                    category = transaction.category.id,
                    categoryName = transaction.category.label,
                    accountName = transaction.account.name,
                    date = formatDate(transaction.createdAt.toEpochMilliseconds()),
                    type = transaction.type,
                    place = transaction.place,
                    toAccountName = transaction.toAccount?.name
                )
            }

            val totalIncome =
                transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val totalExpense =
                transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

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
                ExportFormat.XLSX -> json.encodeToString(exportData) // Binary generation handled in Android layer
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

    override suspend fun getExportData(): ExportData {
        val totalCount = getTransactionCount()
        val transactions = if (totalCount > 0) {
            transactionRepository.getAllTransactions(1, totalCount).data
        } else {
            emptyList()
        }
        val exportTransactions = transactions.map { transaction ->
            TransactionExport(
                id = transaction.id,
                amount = transaction.amount,
                description = transaction.note,
                category = transaction.category.id,
                categoryName = transaction.category.label,
                accountName = transaction.account.name,
                date = formatDate(transaction.createdAt.toEpochMilliseconds()),
                type = transaction.type,
                place = transaction.place,
                toAccountName = transaction.toAccount?.name
            )
        }

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        return ExportData(
            exportDate = getCurrentDateTime(),
            appVersion = appVersion,
            totalTransactions = transactions.size,
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            transactions = exportTransactions
        )
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
        val header = "ID,Amount,Description,CategoryId,CategoryName,AccountName,Date,Type,Place,ToAccountName"
        val rows = transactions.joinToString("\n") { tx ->
            "${tx.id}," +
                    "${tx.amount}," +
                    "\"${tx.description?.replace("\"", "\"\"") ?: ""}\"," +
                    "${tx.category}," +
                    "\"${tx.categoryName.replace("\"", "\"\"")}\"," +
                    "\"${tx.accountName.replace("\"", "\"\"")}\"," +
                    "\"${tx.date}\"," +
                    "${tx.type}," +
                    "\"${tx.place?.replace("\"", "\"\"") ?: ""}\"," +
                    "\"${tx.toAccountName?.replace("\"", "\"\"") ?: ""}\""
        }
        return "$header\n$rows"
    }

    private fun generateFileName(format: ExportFormat): String {
        val timestamp = getCurrentDateTime().replace(":", "-").replace(" ", "_")
        return "expense_tracker_export_${timestamp}.${format.extension}"
    }

    private fun formatDate(timestamp: Long): String {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val localDateTime = instant.toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.time}"
    }

    private fun getCurrentDateTime(): String {
        val now = Clock.System.now()
        val localDateTime = now.toLocalDateTime(TimeZone.Companion.currentSystemDefault())
        return "${localDateTime.date} ${localDateTime.time}"
    }
}