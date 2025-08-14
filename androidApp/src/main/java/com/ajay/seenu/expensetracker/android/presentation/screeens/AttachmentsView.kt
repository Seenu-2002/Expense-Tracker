package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.ajay.seenu.expensetracker.Attachment
import com.ajay.seenu.expensetracker.android.ExpenseTrackerTheme
import com.ajay.seenu.expensetracker.android.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AttachmentsView(modifier: Modifier = Modifier,
                             attachments: List<Attachment>,
                             onAttachmentCanceled: (Attachment) -> Unit,
                             onClick: () -> Unit) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier
        .fillMaxWidth()
        .border(1.dp, LocalContentColor.current.copy(alpha = 0.2F), RoundedCornerShape(10.dp))
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp)) {
            Text(
                text = "Attachments (${attachments.size})",
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp),
                color = LocalContentColor.current.copy(alpha = 0.5F)
            )
            Icon(
                modifier = Modifier.padding(end = 15.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onClick.invoke()
                    },
                imageVector = Icons.Default.Add,
                contentDescription = "add attachment",
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
                attachments.forEach { attachment ->
                    Box(modifier = Modifier.padding(10.dp)
                        .size(100.dp)
                        .border(1.dp, LocalContentColor.current.copy(alpha = 0.2F))) {
                        AsyncImage(model = attachment.imageUri.toUri(),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            onState = {
                                when(it) {
                                    is AsyncImagePainter.State.Error -> {
                                        Log.e("DetailTransactionView", "Error loading image: ${it.result.throwable.message}")
                                    }
                                    is AsyncImagePainter.State.Success -> {
                                        Log.d("DetailTransactionView", "Image loaded successfully")
                                    }
                                    else -> {
                                        Log.d("DetailTransactionView", "Loading image...")
                                    }
                                }
                            })
                        Icon(painter = painterResource(R.drawable.baseline_close_24),
                            contentDescription = "cancel",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .clickable {
                                    onAttachmentCanceled.invoke(attachment)
                                },
                            tint = LocalContentColor.current.copy(alpha = 0.5F)
                        )
                    }
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
            AttachmentsView(
                attachments = emptyList(),
                onAttachmentCanceled = {

                },
                onClick = {

                },
            )
        }
    }
}