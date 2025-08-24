package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.OverallData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext

class GetFilteredOverallDataUseCase constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(dateRange: DateRange): Flow<OverallData> {
        return withContext(Dispatchers.IO) {
            // TODO: Change to flow from SQLDelight and combine the queries to one query
            flowOf(repository.getOverallDataBetween(dateRange))
        }
    }

}