package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository

class GetTotalExpenseByCategoryUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(
        categoryId: Long?,
        startDate: Long,
        endDate: Long
    ): Double {
        return transactionRepository.getTotalExpenseByCategoryInPeriod(
            categoryId, startDate, endDate)
    }
}