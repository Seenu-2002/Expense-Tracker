package com.ajay.seenu.expensetracker.android.domain.usecases

import android.content.Context
import com.ajay.seenu.expensetracker.android.data.CategoryRepository
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import com.ajay.seenu.expensetracker.android.presentation.common.CategoryDefaults
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class InsertDefaultCategories @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val repository: CategoryRepository
) {

    suspend fun invoke() {
        CategoryDefaults.getDefaultCategories(context).forEach {
            repository.addCategory(it.map())
        }
    }

}