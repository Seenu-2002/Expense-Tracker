package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.PaginationData
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class GetRecentTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val dateFormat: SimpleDateFormat
) {

    companion object {
        private const val PER_PAGE = 100
    }

    suspend fun invoke(pageNo: Int): Flow<PaginationData<List<TransactionsByDate>>> {
        val data = repository.getAllTransactions(pageNo, PER_PAGE)
        return listOf(PaginationData(data.data.sortByDate(), data.hasMoreData)).asFlow()
    }

    private fun List<Transaction>.sortByDate(): List<TransactionsByDate> {
        val map = hashMapOf<String, ArrayList<Transaction>>()
        this.forEach { transaction ->
            val dateLabel = dateFormat.format(transaction.date)
            map[dateLabel]?.add(transaction) ?: run {
                map[dateLabel] = arrayListOf(transaction)
            }
        }
        return map.entries.map {
            TransactionsByDate(dateFormat.parse(it.key)!!, it.key, it.value.sortedByDescending { transaction -> transaction.date })
        }.sortedByDescending {
            it.rawDate
        }
    }

}

data class TransactionsByDate(
    val rawDate: Date,
    val dateLabel: String,
    val transactions: List<Transaction>
)