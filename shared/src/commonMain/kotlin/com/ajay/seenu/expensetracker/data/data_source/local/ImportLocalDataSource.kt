package com.ajay.seenu.expensetracker.data.data_source.local

import com.ajay.seenu.expensetracker.CategoryEntity
import com.ajay.seenu.expensetracker.ExpenseDatabase
import com.ajay.seenu.expensetracker.data.data_source.ImportDataSource
import com.ajay.seenu.expensetracker.data.model.TransactionTypeEntity
import com.ajay.seenu.expensetracker.domain.model.ExportData
import com.ajay.seenu.expensetracker.domain.model.ImportResult
import com.ajay.seenu.expensetracker.domain.model.TransactionExport
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ImportLocalDataSource(
    private val database: ExpenseDatabase
) : ImportDataSource {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun importFromJson(content: String): ImportResult = withContext(Dispatchers.IO) {
        try {
            val exportData = json.decodeFromString<ExportData>(content)
            insertTransactions(exportData.transactions)
        } catch (e: Exception) {
            ImportResult(success = false, errorMessage = "Invalid JSON: ${e.message}")
        }
    }

    override suspend fun importFromCsv(content: String): ImportResult = withContext(Dispatchers.IO) {
        try {
            val lines = content.lines().filter { it.isNotBlank() }
            if (lines.isEmpty()) return@withContext ImportResult(success = false, errorMessage = "Empty file")

            val header = parseCsvLine(lines[0])
            fun headerIndex(vararg names: String) =
                header.indexOfFirst { col -> names.any { col.equals(it, ignoreCase = true) } }

            val idIndex = headerIndex("ID")
            val amountIndex = headerIndex("Amount")
            val descIndex = headerIndex("Description")
            val categoryIdIndex = headerIndex("CategoryId", "Category")
            val categoryNameIndex = headerIndex("CategoryName")
            val accountNameIndex = headerIndex("AccountName")
            val dateIndex = headerIndex("Date")
            val typeIndex = headerIndex("Type")
            val placeIndex = headerIndex("Place")
            val toAccountIndex = headerIndex("ToAccountName")

            if (amountIndex < 0 || typeIndex < 0) {
                return@withContext ImportResult(
                    success = false,
                    errorMessage = "CSV missing required columns: Amount, Type"
                )
            }

            val transactions = lines.drop(1).mapNotNull { line ->
                runCatching {
                    val cols = parseCsvLine(line)
                    fun col(index: Int) = if (index >= 0 && index < cols.size) cols[index].trim() else ""

                    val amount = col(amountIndex).toDoubleOrNull() ?: return@mapNotNull null
                    val type = runCatching {
                        TransactionType.valueOf(col(typeIndex).uppercase())
                    }.getOrNull() ?: return@mapNotNull null

                    TransactionExport(
                        id = col(idIndex).toLongOrNull() ?: 0L,
                        amount = amount,
                        description = col(descIndex).takeIf { it.isNotEmpty() },
                        category = col(categoryIdIndex).toLongOrNull() ?: 0L,
                        categoryName = col(categoryNameIndex),
                        accountName = col(accountNameIndex),
                        date = col(dateIndex),
                        type = type,
                        place = col(placeIndex).takeIf { it.isNotEmpty() },
                        toAccountName = col(toAccountIndex).takeIf { it.isNotEmpty() }
                    )
                }.getOrNull()
            }

            insertTransactions(transactions)
        } catch (e: Exception) {
            ImportResult(success = false, errorMessage = "Failed to parse CSV: ${e.message}")
        }
    }

    private fun insertTransactions(transactions: List<TransactionExport>): ImportResult {
        val queries = database.expenseDatabaseQueries
        val allCategories = queries.getAllCategories().executeAsList()
        val allAccounts = queries.getAllAccounts().executeAsList()

        val defaultAccount = allAccounts.firstOrNull { it.isDefault != 0L }
            ?: allAccounts.firstOrNull()
            ?: return ImportResult(
                success = false,
                errorMessage = "No accounts found. Please create an account before importing."
            )

        var importedCount = 0

        database.transaction {
            for (tx in transactions) {
                val categoryId = resolveCategory(tx, allCategories) ?: continue

                val accountId = if (tx.accountName.isNotEmpty()) {
                    allAccounts.find { it.name.equals(tx.accountName, ignoreCase = true) }?.id
                        ?: defaultAccount.id
                } else {
                    defaultAccount.id
                }

                val toAccountId = tx.toAccountName?.let { name ->
                    allAccounts.find { it.name.equals(name, ignoreCase = true) }?.id
                }

                val typeEntity = when (tx.type) {
                    TransactionType.INCOME -> TransactionTypeEntity.INCOME
                    TransactionType.EXPENSE -> TransactionTypeEntity.EXPENSE
                    TransactionType.TRANSFER -> TransactionTypeEntity.TRANSFER
                }

                queries.addTransaction(
                    type = typeEntity,
                    amount = tx.amount,
                    categoryId = categoryId,
                    accountId = accountId,
                    toAccountId = toAccountId,
                    note = tx.description,
                    createdAt = parseDate(tx.date),
                    place = tx.place
                )
                importedCount++
            }
        }

        return ImportResult(success = true, importedCount = importedCount)
    }

    private fun resolveCategory(tx: TransactionExport, allCategories: List<CategoryEntity>): Long? {
        val preferredType = when (tx.type) {
            TransactionType.INCOME -> TransactionTypeEntity.INCOME
            else -> TransactionTypeEntity.EXPENSE
        }

        if (tx.categoryName.isNotEmpty()) {
            allCategories.find { it.label.equals(tx.categoryName, ignoreCase = true) }
                ?.let { return it.id }
        }

        if (tx.category > 0) {
            allCategories.find { it.id == tx.category }?.let { return it.id }
        }

        return allCategories.firstOrNull { it.type == preferredType }?.id
            ?: allCategories.firstOrNull()?.id
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            val normalized = dateStr.trim().replace(" ", "T")
            LocalDateTime.parse(normalized)
                .toInstant(TimeZone.currentSystemDefault())
                .toEpochMilliseconds()
        } catch (_: Exception) {
            dateStr.trim().toLongOrNull() ?: Clock.System.now().toEpochMilliseconds()
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val sb = StringBuilder()
        var inQuotes = false
        var i = 0

        while (i < line.length) {
            val ch = line[i]
            when {
                ch == '"' && inQuotes && i + 1 < line.length && line[i + 1] == '"' -> {
                    sb.append('"')
                    i += 2
                    continue
                }
                ch == '"' -> inQuotes = !inQuotes
                ch == ',' && !inQuotes -> {
                    result.add(sb.toString())
                    sb.clear()
                }
                else -> sb.append(ch)
            }
            i++
        }
        result.add(sb.toString())
        return result
    }
}
