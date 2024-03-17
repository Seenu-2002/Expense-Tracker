package com.ajay.seenu.expensetracker.android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType
import kotlinx.coroutines.launch

//@HiltViewModel
class TransactionViewModel(
    private val transactionRepository: TransactionRepository
): ViewModel() {
    val transactions = transactionRepository.getAllTransactions()

    fun addTransaction(type: TransactionType,
                       amount: Long,
                       category: Category,
                       paymentType: PaymentType,
                       note: String?,
                       date: String?,
                       payer: String?,
                       place: String?) {
        viewModelScope.launch {
            transactionRepository.addTransaction(type = type,
                amount = amount,
                category = category,
                paymentType = paymentType,
                note = note,
                date = date,
                payer = payer,
                place = place)
        }
    }

    fun deleteAllTransactions() {
        viewModelScope.launch {
            transactionRepository.deleteAllTransactions()
        }
    }
}