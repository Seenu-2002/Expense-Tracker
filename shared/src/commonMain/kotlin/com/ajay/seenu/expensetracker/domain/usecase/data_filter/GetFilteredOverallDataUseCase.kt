package com.ajay.seenu.expensetracker.domain.usecase.data_filter

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import com.ajay.seenu.expensetracker.domain.model.DateRange
import com.ajay.seenu.expensetracker.domain.model.OverallData
import kotlinx.coroutines.flow.Flow

class GetFilteredOverallDataUseCase constructor(
    private val repository: TransactionRepository
) {

    operator fun invoke(dateRange: DateRange): Flow<OverallData> {
        return repository.getOverallDataBetween(dateRange)
    }

}