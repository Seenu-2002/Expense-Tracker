package com.ajay.seenu.expensetracker.android.domain.usecases

import com.ajay.seenu.expensetracker.GetTotalExpenseByPaymentType
import com.ajay.seenu.expensetracker.android.data.TransactionRepository
import com.ajay.seenu.expensetracker.android.domain.data.Filter
import com.ajay.seenu.expensetracker.android.domain.data.getDateRange
import com.ajay.seenu.expensetracker.entity.PaymentType
import javax.inject.Inject

class GetExpensesByPaymentTypeUseCase @Inject constructor(
    private val repository: TransactionRepository
) {

    suspend operator fun invoke(filter: Filter): Map<PaymentType, Double> {
        val range = filter.getDateRange()
        return if (range == null) {
            repository.getExpensePerDayByPaymentType().associate {
                it.paymentType to (it.totalAmount ?: 0.0)
            }
        } else {
            repository.getExpensePerDayByPaymentTypeBetween(range).associate {
                it.paymentType to (it.totalAmount ?: 0.0)
            }
        }
    }

}