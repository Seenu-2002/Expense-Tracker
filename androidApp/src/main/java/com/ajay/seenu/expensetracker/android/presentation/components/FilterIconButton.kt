package com.ajay.seenu.expensetracker.android.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.R

@Composable
fun FilterIconButton(
    isFiltered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BadgedBox(
        modifier = modifier.padding(4.dp),
        badge = {
            if (isFiltered) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                )
            }
        }
    ) {
        Icon(
            modifier = Modifier.clickable(onClick = onClick),
            painter = painterResource(id = R.drawable.icon_filter_list),
            contentDescription = "filter",
        )
    }
}
