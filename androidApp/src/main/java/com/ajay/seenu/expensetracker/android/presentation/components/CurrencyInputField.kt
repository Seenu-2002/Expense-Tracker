package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun CurrencyInputField(modifier: Modifier, defValue: String = "") {
    var value by remember {
        mutableStateOf(defValue)
    }

    OutlinedTextField(value = value, label = {

    }, visualTransformation = { it ->

        TODO()
    },
        onValueChange = {

    })

}