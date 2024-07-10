package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.PaginationData
import com.ajay.seenu.expensetracker.android.data.PER_PAGE
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.data.sortByDate
import com.ajay.seenu.expensetracker.android.domain.data.TransactionsByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import java.text.SimpleDateFormat
import javax.inject.Inject

class GetFilteredTransactionsUseCase@Inject constructor(
    private val repository: TransactionRepository,
    private val dateFormat: SimpleDateFormat
) {
    suspend fun invoke(pageNo: Int, fromValue: Long, toValue: Long): Flow<PaginationData<List<TransactionsByDate>>> {
        val data = repository.getAllTransactionsBetween(pageNo, PER_PAGE, fromValue, toValue)
        return listOf(PaginationData(data.data.sortByDate(dateFormat), data.hasMoreData)).asFlow()
    }
}