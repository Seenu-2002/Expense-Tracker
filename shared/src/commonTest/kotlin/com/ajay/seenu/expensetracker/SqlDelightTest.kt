package com.ajay.seenu.expensetracker

import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SqlDelightTest {

    private lateinit var dbSource: TransactionDataSourceImpl

    @BeforeTest
    fun setup() = runTest {
        dbSource = TransactionDataSourceImpl(
            testDriver()
        )
    }

    @Test
    fun `Get All Incomes`() = runTest {
        val incomes = dbSource.getAllTransactionsByType(TransactionType.INCOME)
        incomes.forEach {
            assertEquals(true, it.type == TransactionType.INCOME)
        }
    }

    @Test
    fun `Get All Expenses`() = runTest {
        val incomes = dbSource.getAllTransactionsByType(TransactionType.EXPENSE)
        incomes.forEach {
            assertEquals(true, it.type == TransactionType.EXPENSE)
        }
    }

    @Test
    fun `Select Item by Id`() = runTest {
        val firstTransaction = dbSource.getAllTransactions().first()
        val id = firstTransaction.id
        assertNotNull(
            dbSource.getTransaction(id),
            "Could not retrieve Breed"
        )
    }
}