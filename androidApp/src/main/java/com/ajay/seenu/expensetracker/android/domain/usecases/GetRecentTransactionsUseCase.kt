package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.PaginationData
import com.ajay.seenu.expensetracker.UserConfigurationsManager
import com.ajay.seenu.expensetracker.android.data.PER_PAGE
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.data.getDateFormat
import com.ajay.seenu.expensetracker.android.data.sortByDate
import com.ajay.seenu.expensetracker.android.domain.data.TransactionsByDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import javax.inject.Inject

class GetRecentTransactionsUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val userConfigurationsManager: UserConfigurationsManager
) {
    suspend fun invoke(pageNo: Int): Flow<PaginationData<List<TransactionsByDate>>> {
        val data = repository.getAllTransactions(pageNo, PER_PAGE)
        return listOf(PaginationData(data.data.sortByDate(userConfigurationsManager.getConfigs().getDateFormat()), data.hasMoreData)).asFlow()
    }
}