package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.PaginationData
import com.ajay.seenu.expensetracker.domain.model.Transaction
import com.ajay.seenu.expensetracker.domain.model.TransactionFilter
import com.ajay.seenu.expensetracker.domain.model.TransactionsByDate
import com.ajay.seenu.expensetracker.util.getDateLabel
import com.ajay.seenu.expensetracker.util.toLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

class GetFilteredTransactionsUseCase constructor(
    private val repository: TransactionRepository
) {

    @OptIn(ExperimentalTime::class)
    suspend fun invoke(
        dateRange: DateRange,
        pageNo: Int = 1,
        count: Int = 20,
        transactionFilter: TransactionFilter? = null
    ): Flow<PaginationData<List<TransactionsByDate>>> {
        return repository.getAllTransactionsBetween(pageNo, count, dateRange).map { data ->
            val filtered = if (transactionFilter != null && transactionFilter.hasActiveFilters) {
                data.data.filter { transaction ->
                    (transactionFilter.type == null || transaction.type == transactionFilter.type) &&
                    (transactionFilter.categoryIds.isEmpty() || transaction.category.id in transactionFilter.categoryIds) &&
                    (transactionFilter.accountIds.isEmpty() || transaction.account.id in transactionFilter.accountIds)
                }
            } else {
                data.data
            }

            val transactions = filtered.sortedByDescending { transaction -> transaction.createdAt }
            val expensesByDate = transactions
                .groupBy { it.createdAt.getDateLabel() }
                .map { (dateLabel, txns) -> TransactionsByDate(dateLabel.toLocalDate(), txns) }
            PaginationData(data = expensesByDate, hasMoreData = data.hasMoreData)
        }
    }

}