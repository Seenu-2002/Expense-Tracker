package com.ajay.seenu.expensetracker.domain.usecase.transaction

import com.ajay.seenu.expensetracker.data.repository.TransactionRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
class PurgeOldTrashUseCase constructor(
    private val repository: TransactionRepository
) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(retentionDays: Int = 30) {
        val cutoffMs = Clock.System.now().toEpochMilliseconds() - retentionDays * 24 * 60 * 60 * 1000L
        repository.purgeOldDeletedTransactions(cutoffMs)
    }
}
