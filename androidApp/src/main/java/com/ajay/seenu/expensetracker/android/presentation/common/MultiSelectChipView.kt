package com.ajay.seenu.expensetracker.android.presentation.common

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ajay.seenu.expensetracker.android.ExpenseTrackerTheme
import com.ajay.seenu.expensetracker.android.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun <T> MultiSelectChipsView(modifier: Modifier = Modifier,
                                      selectedOptions: List<T>,
                                      selectionOptionView: @Composable (T) -> Unit,
                                      onOptionCanceled: (T) -> Unit,
                                      onClick: () -> Unit,
                                      expandIcon: Int = R.drawable.outline_arrow_drop_down_24,
                                      chipTrailingIcon: Int = R.drawable.baseline_close_24,
                                      borderColor: Color = LocalContentColor.current,
                                      chipBorderColor: Color = LocalContentColor.current.copy(alpha = 0.5F),
                                      chipContainerColor: Color = Color.Transparent,
                                      chipTrailingIconBackgroundColor: Color = Color.Transparent,
                                      chipTrailingIconColor: Color = LocalContentColor.current,) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier
        .fillMaxWidth()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick.invoke()
        }
        .border(1.dp, borderColor, RoundedCornerShape(10.dp))) {
        Row(modifier = Modifier.fillMaxWidth()
            .padding(top = 10.dp)) {
            Text(
                text = "Attachments (${selectedOptions.size})",
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                color = LocalContentColor.current.copy(alpha = 0.5F)
            )
            Icon(
                modifier = Modifier.padding(end = 15.dp),
                painter = painterResource(id = expandIcon), contentDescription = "drop down",
                tint = LocalContentColor.current.copy(alpha = 0.5F)
            )
        }
        Row(modifier = modifier
            .fillMaxWidth()
            .heightIn(MinContainerHeight, MaxFlowRowHeight)
            .verticalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(10.dp)
            ) {
                selectedOptions.forEach {
                    InputChip(
                        modifier = Modifier,
                        selected = false,
                        enabled = true,
                        onClick = {},
                        label = {
                            selectionOptionView.invoke(it)
                        },
                        trailingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(chipTrailingIconBackgroundColor)
                                    .clickable {
                                        onOptionCanceled.invoke(it)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = chipTrailingIcon),
                                    contentDescription = "cancel",
                                    tint = chipTrailingIconColor
                                )
                            }
                        },
                        colors = InputChipDefaults.inputChipColors(
                            containerColor = chipContainerColor
                        ),
                        border = InputChipDefaults.inputChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = chipBorderColor
                        )
                    )
                }
            }
        }
    }
}

private val MinContainerHeight = 40.dp
private val MaxFlowRowHeight = 120.dp

@Preview(showBackground = true)
@Composable
private fun MultiSelectChipPreview() {
    ExpenseTrackerTheme(darkTheme = false) {
        Column(modifier = Modifier.background(color = Color.White)) {
            MultiSelectChipsView(
                selectedOptions = emptyList<String>(),
                selectionOptionView = {
                    Text(it)
                },
                onOptionCanceled = {

                },
                onClick = {

                }
            )
        }
    }
}