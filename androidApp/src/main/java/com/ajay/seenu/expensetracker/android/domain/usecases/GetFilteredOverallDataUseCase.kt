package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.presentation.widgets.OverallData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetFilteredOverallDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(fromValue: Long, toValue: Long): Flow<OverallData> {
        return withContext(Dispatchers.IO) {
            flowOf(repository.getOverallDataBetween(fromValue, toValue))
        }
    }

}