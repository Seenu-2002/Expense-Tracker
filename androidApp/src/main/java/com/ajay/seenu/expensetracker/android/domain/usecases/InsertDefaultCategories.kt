package com.ajay.seenu.expensetracker.android.domain.usecases

import android.content.Context
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.CategoryDefaults
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InsertDefaultCategories @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val repository: CategoryRepository
) {

    suspend fun invoke() {
        CategoryDefaults.getDefaultCategories(context).forEach {
            repository.addCategory(it)
        }
        // System-only category used internally for account transfers (never shown in user category pickers)
        repository.addCategory("Transfer", TransactionType.TRANSFER, R.drawable.baseline_import_export_24, 0xFF1A73E8)
    }

}