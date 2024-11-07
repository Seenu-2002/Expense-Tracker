package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.getDateRange
import com.ajay.seenu.expensetracker.entity.PaymentType
import javax.inject.Inject

class GetExpensesByPaymentTypeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(startDayOfTheWeek: Int, filter: Filter): Map<PaymentType, Double> {
        val range = filter.getDateRange(startDayOfTheWeek)
        return repository.getExpensePerDayByPaymentTypeBetween(range).associate {
            it.paymentType to (it.totalAmount ?: 0.0)
        }
    }

}