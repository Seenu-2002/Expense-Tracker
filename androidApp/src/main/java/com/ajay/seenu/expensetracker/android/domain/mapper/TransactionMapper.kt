package com.ajay.seenu.expensetracker.android.domain.mapper

import com.ajay.seenu.expensetracker.Category
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.domain.mapper.CategoryMapper.map
import java.util.Date

object TransactionMapper {

    fun TransactionDetail.map(category: Transaction.Category): Transaction {
        if (this.category != category.id) {
            throw IllegalArgumentException("category id ${category.id} is not valid")
        }

        return Transaction(
            this.id,
            this.type.map(),
            this.amount,
            category,
            this.paymentType,
            Date(this.date),
            this.note
        )
    }

}