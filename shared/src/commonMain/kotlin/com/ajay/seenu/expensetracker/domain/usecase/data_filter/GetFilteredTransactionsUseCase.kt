package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.PaginationData
import com.ajay.seenu.expensetracker.domain.model.Transaction
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
        count: Int = 20
    ): Flow<PaginationData<List<TransactionsByDate>>> {
        return repository.getAllTransactionsBetween(pageNo, count, dateRange).map { data ->
            val transactions = data.data.sortedBy { transaction -> transaction.createdAt }
            val map = HashMap<String, MutableList<Transaction>>()
            for (transaction in transactions) {
                val date = transaction.createdAt.getDateLabel()
                if (!map.contains(date)) {
                    map[date] = mutableListOf()
                }

                map[date]!!.add(transaction)
            }

            val expensesByDate = map.map { (dateLabel, transactions) ->
                val localDate = dateLabel.toLocalDate()
                TransactionsByDate(localDate, transactions)
            }
            PaginationData(data = expensesByDate, hasMoreData = data.hasMoreData)
        }
    }

}