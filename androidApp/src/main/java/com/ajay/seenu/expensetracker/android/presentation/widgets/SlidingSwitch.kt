package com.ajay.seenu.expensetracker.android.presentation.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.domain.data.Transaction

@Composable
fun SlidingSwitch(
    selectedValue: String,
    values: List<String>,
    modifier: Modifier = Modifier.fillMaxWidth(),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    sliderColor: Color = MaterialTheme.colorScheme.primary,
    selectedValueColor: Color = LocalContentColor.current,
    unselectedValueColor: Color = LocalContentColor.current,
    shape: Shape = RoundedCornerShape(10.dp),
    onSelectedValue: (index: Int, value: String) -> Unit
) {
    var currentValue by rememberSaveable {
        mutableStateOf(selectedValue)
    }
    var width by remember { mutableFloatStateOf(0f) }
    val offsetAnim by animateFloatAsState(
        targetValue = (width / values.size) * values.indexOf(currentValue),
        animationSpec = tween(500),
        label = "Offset Value"
    )
    Box(modifier = modifier
        .padding(horizontal = 10.dp)
        .clip(shape)
        .background(color = containerColor)
        .padding(horizontal = 5.dp)

        .onGloballyPositioned {
            width = it.size.width.toFloat()
        }) {
        Row(
            modifier = Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(with(LocalDensity.current) { (width / values.size).toDp() })
                    .height(35.dp)
                    .offset(with(LocalDensity.current) { offsetAnim.toDp() }, 0.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(sliderColor)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            values.forEachIndexed { index, it ->
                Text(text = it,
                    modifier = Modifier
                        .width(with(LocalDensity.current) { (width / values.size).toDp() })
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {
                            if (it != currentValue) {
                                currentValue = it
                                onSelectedValue.invoke(index, it)
                            }
                        }
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center,
                    color = if (it == currentValue) selectedValueColor else unselectedValueColor
                )
            }
        }
    }
}

@Composable
fun SlidingSwitch(
    modifier: Modifier = Modifier,
    selectedValue: Transaction.Type,
    values: List<Transaction.Type>,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    sliderColor: Color = MaterialTheme.colorScheme.primary,
    selectedValueColor: Color = Color.Unspecified,
    unselectedValueColor: Color = Color.Unspecified,
    onSelectedValue: (Transaction.Type) -> Unit
) {
    var currentValue by rememberSaveable {
        mutableStateOf(selectedValue)
    }
    var width by remember { mutableFloatStateOf(0f) }
    val offsetAnim by animateFloatAsState(
        targetValue = (width / values.size) * values.indexOf(currentValue),
        animationSpec = tween(500),
        label = "Offset Value"
    )
    Box(modifier = modifier
        .height(50.dp)
        .onGloballyPositioned {
            width = it.size.width.toFloat()
        }
    ) {
        Row(
            modifier = Modifier.matchParentSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(with(LocalDensity.current) { (width / values.size).toDp() })
                    .height(40.dp)
                    .offset(with(LocalDensity.current) { offsetAnim.toDp() }, 0.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(sliderColor)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            values.forEach {
                Icon(painter = painterResource(it.resourceId),
                    modifier = Modifier
                        .width(with(LocalDensity.current) { (width / values.size).toDp() })
                        .clickable(
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {
                            if (it != currentValue) {
                                currentValue = it
                                onSelectedValue.invoke(it)
                            }
                        }
                        .padding(vertical = 10.dp),
                    tint = if (it == currentValue) selectedValueColor else unselectedValueColor,
                    contentDescription = null
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SlidingSwitchPreview() {
    Column(modifier = Modifier.width(300.dp)) {
        SlidingSwitch(
            selectedValue = "One",
            values = listOf("Three", "Two", "One")
        ) { _, _ ->

        }
        Spacer(modifier = Modifier.height(10.dp))
        SlidingSwitch(
            selectedValue = "One",
            values = listOf("Three", "Two", "One", "Four", "Five")
        ) { _, _ ->

        }
        Spacer(modifier = Modifier.height(10.dp))
        SlidingSwitch(
            selectedValue = Transaction.Type.INCOME,
            values = Transaction.Type.entries
        ) {

        }
        Spacer(modifier = Modifier.height(10.dp))
        SlidingSwitch(
            selectedValue = "One",
            values = listOf("Three", "Two", "One", "Four", "Five", "Six", "Seven")
        ) { _, _ ->

        }
    }
}