package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.android.presentation.screeens.CategoryIconItem

@Composable
fun CategoryRow(
    modifier: Modifier = Modifier,
    category: Transaction.Category,
    iconBoxSize: Dp = 52.dp,
    iconSize: Dp = 24.dp,
    clickable: Boolean = false,
    onClicked: (Transaction.Category) -> Unit = {}
) {
    Row(
        modifier = modifier
        .clickable(clickable) {
            onClicked(category)
        }, verticalAlignment = Alignment.CenterVertically
    ) {
        CategoryIconItem(
            modifier = Modifier
                .size(iconBoxSize)
                .padding(4.dp),
            iconSize = iconSize,
            res = category.res,
            iconBackground = category.color
        )
        Text(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            text = category.label
        )
    }
}