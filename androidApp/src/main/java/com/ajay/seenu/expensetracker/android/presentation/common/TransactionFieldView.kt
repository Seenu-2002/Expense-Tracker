package com.ajay.seenu.expensetracker.android.presentation.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.R

@Composable
fun TransactionFieldView(modifier: Modifier = Modifier,
                         text: String,
                         onClick: () -> Unit,
                         color: Color = LocalContentColor.current,
                         isError: Boolean = false,
                         errorView: @Composable (() -> Unit)? = null) {
    ErrorWrapper(errorView) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable(interactionSource = null,
                    indication = null) {
                    onClick.invoke()
                }
                .border(
                    width = 1.dp,
                    color = if (isError) Color(0xFFFD3C4A) else LocalContentColor.current.copy(alpha = 0.2F),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = color
            )
            Icon(
                modifier = Modifier
                    .size(24.dp),
                painter = painterResource(id = R.drawable.outline_arrow_drop_down_24),
                contentDescription = "Select Payment Type",
                tint = LocalContentColor.current.copy(alpha = 0.5F)
            )
        }
    }
}

@Composable
fun ErrorWrapper(errorView: @Composable (() -> Unit)? = null,
                 content: @Composable () -> Unit) {
    if (errorView != null) {
        Column {
            content.invoke()
            errorView.invoke()
        }
    } else {
        content.invoke()
    }

}

@Preview
@Composable
private fun TransactionFieldPreview() {
    TransactionFieldView(
        modifier = Modifier.fillMaxWidth(),
        "Hey", {

        }
    )
}