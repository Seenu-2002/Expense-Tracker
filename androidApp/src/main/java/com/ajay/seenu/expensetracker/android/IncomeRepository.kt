package com.ajay.seenu.expensetracker.android

import android.content.Context
import com.ajay.seenu.expensetracker.DriverFactory
import com.ajay.seenu.expensetracker.Income
import com.ajay.seenu.expensetracker.IncomeDataSource
import com.ajay.seenu.expensetracker.IncomeDataSourceImpl
import com.ajay.seenu.expensetracker.createDatabase
import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.migration.DisableInstallInCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


class IncomeRepository(private val dataSource: IncomeDataSource) {

    suspend fun addIncome(income: Income) {
        withContext(Dispatchers.IO) {
            dataSource.addIncome(income)
        }
    }

    fun getAllIncomes(): Flow<List<Income>> {
        return listOf(dataSource.getAllIncomes()).asFlow()
    }
}


//@Module
//@DisableInstallInCheck
//class SimpleModule {
//    @Provides
//    fun providerIncomeDataSource(context: Context): IncomeDataSource {
//        return IncomeDataSourceImpl(createDatabase(DriverFactory(context)))
//    }
//
//    @Provides
//    fun provideIncomeRepository(dataSource: IncomeDataSource): IncomeRepository {
//        return IncomeRepository(dataSource)
//    }
//}