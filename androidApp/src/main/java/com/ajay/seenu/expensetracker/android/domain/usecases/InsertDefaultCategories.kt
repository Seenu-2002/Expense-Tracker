package com.ajay.seenu.expensetracker.android.domain.usecases

import android.content.Context
import com.ajay.seenu.expensetracker.android.presentation.common.CategoryDefaults
import com.ajay.seenu.expensetracker.data.repository.CategoryRepository
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
    }

}