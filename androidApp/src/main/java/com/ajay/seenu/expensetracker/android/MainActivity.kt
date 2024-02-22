package com.ajay.seenu.expensetracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import com.ajay.seenu.expensetracker.Income
import com.ajay.seenu.expensetracker.IncomeDataSourceImpl
import com.ajay.seenu.expensetracker.createDatabase
import com.ajay.seenu.expensetracker.entity.Category
import com.ajay.seenu.expensetracker.entity.PaymentType

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
                    val viewModel: IncomeViewModel = viewModel(
                        factory = IncomeViewModelFactory(
                            IncomeRepository(
                                IncomeDataSourceImpl(createDatabase(DriverFactory(context)))
                            )
                        )
                    )
                    val incomes = viewModel.incomes
                        .collectAsStateWithLifecycle(initialValue = listOf()).value
                    GreetingView(incomes) {
                        viewModel.addIncome(Income(20, Category.FOOD_AND_DRINKS, PaymentType.UPI))
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingView(incomes: List<Income>, onClick: () -> Unit) {
    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(incomes) {
                Column {
                    Text(text = it.amount.toString())
                    Text(text = it.category.toString())
                }
            }
        }
        Button(onClick = onClick) {
            Text(text = "Add")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        GreetingView(listOf(Income(100, Category.SHOPPING, PaymentType.CARD))){}
    }
}
