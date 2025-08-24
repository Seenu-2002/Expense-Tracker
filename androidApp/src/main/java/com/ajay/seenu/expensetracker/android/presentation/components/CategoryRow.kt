package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.presentation.screeens.CategoryIconItem
import com.ajay.seenu.expensetracker.domain.model.Category

@Composable
fun CategoryRow(
    modifier: Modifier = Modifier,
    category: Category,
    iconBoxSize: Dp = 52.dp,
    iconSize: Dp = 24.dp,
    clickable: Boolean = false,
    onClicked: (Category) -> Unit = {}
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
            res = category.iconRes,
            iconBackground = Color(category.color)
        )
        Text(
            modifier = Modifier
                .weight(1F)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            text = category.label
        )
    }
}