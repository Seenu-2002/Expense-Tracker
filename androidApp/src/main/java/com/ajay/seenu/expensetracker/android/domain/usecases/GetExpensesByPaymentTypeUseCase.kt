package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.GetTotalExpenseByPaymentType
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import javax.inject.Inject

class GetExpensesByPaymentTypeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(): List<GetTotalExpenseByPaymentType> {
        return repository.getExpensePerDayByPaymentType()
    }

}