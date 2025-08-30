package com.ajay.seenu.expensetracker.android.presentation.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors


@Composable
fun SwipeableBox(
    modifier: Modifier = Modifier,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val swipeState =
        rememberSwipeToDismissBoxState(positionalThreshold = {
            it / 4
        }, confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    false
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    onEdit()
                    false
                }

                SwipeToDismissBoxValue.Settled -> true
            }
        })

    lateinit var icon: @Composable () -> Unit
    lateinit var alignment: Alignment
    val color: Color

    when (swipeState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart,
        SwipeToDismissBoxValue.Settled -> {
            icon = {
                Icon(
                    modifier = Modifier
                        .padding(end = 15.dp),
                    imageVector = Icons.Default.Delete, contentDescription = "Delete"
                )
            }
            alignment = Alignment.CenterEnd
            color = LocalColors.current.expenseColor
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            icon = {
                Icon(
                    modifier = Modifier
                        .padding(start = 15.dp),
                    painter = painterResource(id = R.drawable.edit_note),
                    contentDescription = "Clone"
                )
            }
            alignment = Alignment.CenterStart
            color = Color.Blue.copy(alpha = 0.3f)
        }
    }

    SwipeToDismissBox(
        modifier = modifier.animateContentSize(),
        state = swipeState,
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = true,
        backgroundContent = {
            Box(
                contentAlignment = alignment,
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
            ) {
                icon.invoke()
            }
        }
    ) {
        content()
    }

}