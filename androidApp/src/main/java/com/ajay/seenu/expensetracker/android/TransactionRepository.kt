package com.ajay.seenu.expensetracker.android

import com.ajay.seenu.expensetracker.TransactionDataSource
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext


class TransactionRepository(private val dataSource: TransactionDataSource) {

    fun getAllTransactions(): Flow<List<TransactionDetail>> {
        return listOf(dataSource.getAllTransactions()).asFlow()
    }

    fun getAllTransactionsByType(type: TransactionType): Flow<List<TransactionDetail>> {
        return listOf(dataSource.getAllTransactionsByType(type)).asFlow()
    }

    fun getTransaction(id: Long): TransactionDetail {
        return dataSource.getTransaction(id)
    }

    suspend fun addTransaction(type: TransactionType,
                               amount: Long,
                               category: Category,
                               paymentType: PaymentType,
                               note: String?,
                               date: String?,
                               payer: String?,
                               place: String?) {
        withContext(Dispatchers.IO) {
            dataSource.addTransaction(type = type,
                amount = amount,
                category = category,
                paymentType = paymentType,
                note = note,
                date = date,
                payer = payer,
                place = place)
        }
    }

    suspend fun deleteAllTransactions() {
        withContext(Dispatchers.IO) {
            dataSource.deleteAllTransactions()
        }
    }

    suspend fun deleteAllTransactionsByType(type: TransactionType) {
        withContext(Dispatchers.IO) {
            dataSource.deleteAllTransactionsByType(type)
        }
    }

    suspend fun deleteTransaction(id: Long) {
        withContext(Dispatchers.IO) {
            dataSource.deleteTransaction(id)
        }
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