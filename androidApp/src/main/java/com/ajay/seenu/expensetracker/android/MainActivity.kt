package com.ajay.seenu.expensetracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ajay.seenu.expensetracker.DriverFactory
import com.ajay.seenu.expensetracker.TransactionDataSourceImpl
import com.ajay.seenu.expensetracker.TransactionDetail
import com.ajay.seenu.expensetracker.createDatabase
import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType
import com.ajay.seenu.expensetracker.entity.TransactionType

//@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: TransactionViewModel = viewModel(
                        factory = TransactionViewModelFactory(
                            TransactionRepository(
                                TransactionDataSourceImpl(DriverFactory(context).createDriver())
                            )
                        )
                    )
                    val transactions = viewModel.transactions
                        .collectAsStateWithLifecycle(initialValue = listOf()).value
                    GreetingView(transactions,
                        onAdd = {
                            viewModel.addTransaction(type = TransactionType.INCOME,
                                amount = 20,
                                category = Category.FOOD_AND_DRINKS,
                                paymentType = PaymentType.UPI,
                                null, null, null, null)
                        },
                        onDeleteAll = {
                            viewModel.deleteAllTransactions()
                        })
                }
            }
        }
    }
}

@Composable
fun GreetingView(transactions: List<TransactionDetail>,
                 onAdd: () -> Unit,
                 onDeleteAll: () -> Unit) {
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(transactions) {
                Column {
                    Text(text = it.amount.toString())
                    Text(text = it.category.toString())
                }
            }
        }
        Row {
            Button(onClick = onAdd) {
                Text(text = "Add")
            }
            Button(onClick = onDeleteAll) {
                Text(text = "Delete All")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView(listOf(TransactionDetail(1,TransactionType.EXPENSE,100, Category.SHOPPING, PaymentType.CARD,
            null, null, null, null)
        ),
            onAdd = {}, onDeleteAll = {})
    }
}
