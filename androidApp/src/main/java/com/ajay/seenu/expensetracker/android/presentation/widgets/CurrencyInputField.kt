package com.ajay.seenu.expensetracker.android.presentation.widgets

import android.app.Activity
import android.view.View
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat

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