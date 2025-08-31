package com.ajay.seenu.expensetracker.android.presentation.components

import android.icu.text.DecimalFormat
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.PreviewThemeWrapper
import com.ajay.seenu.expensetracker.android.presentation.screeens.CategoryIconItem
import com.ajay.seenu.expensetracker.android.presentation.theme.LocalColors
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.OverallData
import com.ajay.seenu.expensetracker.domain.model.Transaction
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Preview(showBackground = true)
@Composable
private fun OverviewCardPreview(@PreviewParameter(OverallDataProvider::class) data: OverallData) {
    PreviewThemeWrapper {
        OverviewCard(
            modifier = Modifier
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(4.dp), color = Color.Black), data = data
        )
    }
}

@Composable
fun OverviewCard(
    modifier: Modifier = Modifier, data: OverallData
) {

    val expensePercentageRaw = (data.expense / data.income).let {
        if (it == Double.POSITIVE_INFINITY) {
            0.0
        } else {
            it
        }
    }
    val expensePercentageLabel = decimalFormatter.format(expensePercentageRaw * 100) + " %"

    ConstraintLayout(modifier = modifier) {
        val (expense, income, progress) = createRefs()
        Column(
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(20.dp),
                    color = LocalColors.current.incomeColor
                )
                .constrainAs(income) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 15.dp)
                    end.linkTo(expense.start, 10.dp)
                    bottom.linkTo(expense.bottom)
                }
                .padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(modifier = Modifier.padding(bottom = 4.dp), text = "Income", fontSize = 14.sp)
            Text(
                text = "Rs. ${data.getIncomeLabel()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        }

        Column(
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(20.dp),
                    color = LocalColors.current.expenseColor
                )
                .constrainAs(expense) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(income.end, 10.dp)
                    end.linkTo(parent.end, 15.dp)
                }
                .padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(modifier = Modifier.padding(bottom = 4.dp), text = "Expense", fontSize = 14.sp)
            Text(
                text = "Rs. ${data.getExpenseLabel()}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

        }

        ConstraintLayout(
            modifier = Modifier
                .constrainAs(progress) {
                    width = Dimension.fillToConstraints
                    top.linkTo(expense.bottom, 8.dp)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(parent.end, 10.dp)
                    bottom.linkTo(parent.bottom, 8.dp)
                }
                .padding(bottom = 8.dp, start = 15.dp, end = 15.dp)) {

            val (progressBarBg, progressBar, marker, percentageText) = createRefs()
            val percentage = if (expensePercentageRaw > 1) {
                1
            } else {
                expensePercentageRaw
            }.toFloat()

            val color = if (percentage <= .65) {
                LocalColors.current.incomeColor
            } else if (percentage <= .8) {
                LocalColors.current.averagePercentColor
            } else {
                LocalColors.current.expenseColor
            }

            val textColor = LocalContentColor.current

            Box(
                modifier = Modifier
                    .constrainAs(progressBarBg) {
                        top.linkTo(marker.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(shape = RoundedCornerShape(4.dp), color = Color.LightGray)
            )

            Box(
                modifier = Modifier
                    .constrainAs(progressBar) {
                        top.linkTo(marker.bottom)
                        start.linkTo(progressBarBg.start)
                    }
                    .fillMaxWidth(percentage)
                    .height(2.dp)
                    .background(shape = RoundedCornerShape(4.dp), color = color)
            )

            Canvas(
                modifier = Modifier
                    .width(10.dp)
                    .height(10.dp)
                    .constrainAs(marker) {
                        top.linkTo(percentageText.bottom, 2.dp)
                        start.linkTo(progressBar.end)
                        end.linkTo(progressBar.end)
                    }) {
                with(Path()) {
                    moveTo(0F, 0F)
                    lineTo(size.width, 0F)
                    lineTo(size.width / 2F, size.height)
                    lineTo(0F, 0F)
                    close()

                    drawPath(path = this, color = textColor)
                }
            }

            Text(
                modifier = Modifier
                    .wrapContentSize()
                    .constrainAs(percentageText) {
                        top.linkTo(parent.top)
                        start.linkTo(progressBar.end)
                        end.linkTo(progressBar.end)
                    },
                textAlign = TextAlign.Center,
                text = expensePercentageLabel,
                fontSize = 12.sp,
                color = textColor
            )

        }
    }

}

val decimalFormatter = DecimalFormat("#0.##") // FIXME: Generic and user config
fun OverallData.getIncomeLabel(): String {
    return decimalFormatter.format(income)
}

fun OverallData.getExpenseLabel(): String {
    return decimalFormatter.format(expense)
}

class OverallDataProvider : PreviewParameterProvider<OverallData> {

    override val values: Sequence<OverallData>
        get() = listOf(
            OverallData(2121.33, 1031.44),
//            OverallData(2342.33, 1031.44),
//            OverallData(22333.33, 1031.44),
//            OverallData(22333.33, 15333.44),
//            OverallData(21333.33, 22333.44),
//            OverallData(21333.33, 0.0),
//            OverallData(0.0, 21333.33)
        ).asSequence()

}

@Preview
@Composable
fun TransactionPreviewRowPreview(@PreviewParameter(TransactionPreviewDataProvider::class) data: Transaction) {
    PreviewThemeWrapper {
        TransactionPreviewRow(
            Modifier.fillMaxWidth(), data,
            onClick = {},
            onDelete = {},
            onClone = {})
    }
}

// TODO: Use `com.ajay.seenu.expensetracker.android.presentation.common.SwipeableBox`
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionPreviewRow(
    modifier: Modifier = Modifier,
    transaction: Transaction,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onClone: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        positionalThreshold = {
            it / 4
        }
    )
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
                    painter = painterResource(id = R.drawable.baseline_content_copy_24),
                    contentDescription = "Clone"
                )
            }
            alignment = Alignment.CenterStart
            color = Color.Blue.copy(alpha = 0.3f)
        }
    }
    when (swipeState.currentValue) {
        SwipeToDismissBoxValue.EndToStart -> {
            onDelete.invoke()
        }

        SwipeToDismissBoxValue.StartToEnd -> {
            LaunchedEffect(swipeState) {
                onClone.invoke()
                delay(100)
                swipeState.snapTo(SwipeToDismissBoxValue.Settled)
            }
        }

        SwipeToDismissBoxValue.Settled -> {
        }
    }
    SwipeToDismissBox(
        modifier = Modifier.animateContentSize(),
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
        ConstraintLayout(
            modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 15.dp, vertical = 8.dp)
                .clickable(
                    remember { MutableInteractionSource() },
                    null
                ) {
                    onClick.invoke()
                }
                .clip(RoundedCornerShape(15.dp))
        ) {
            val (icon, title, paymentType, amount) = createRefs()

            val decimalFormat = DecimalFormat("#0.00").apply {
                decimalFormatSymbols.let {
                    it.decimalSeparator = '.'
                    it.groupingSeparator = ','
                }
            }
            var amountLabel = decimalFormat.format(transaction.amount)
            val (rotation, color, iconSource) = if (transaction.type == TransactionType.EXPENSE) {
                amountLabel = "- $amountLabel"
                Triple(-90F, LocalColors.current.expenseColor, R.drawable.expense)
            } else {
                amountLabel = "+ $amountLabel"
                Triple(90F, LocalColors.current.incomeColor, R.drawable.income)
            }

            CategoryIconItem(
                modifier = Modifier
                    .size(60.dp)
                    .constrainAs(icon) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(4.dp),
                iconSize = 24.dp,
                res = transaction.category.iconRes,
                iconBackground = Color(transaction.category.color)
            )

            Text(
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top, 5.dp)
                    linkTo(
                        start = icon.end,
                        startMargin = 10.dp,
                        end = amount.start,
                        endMargin = 10.dp,
                        bias = 0F
                    )

                }, text = transaction.category.label,
                fontSize = 16.sp,
                color = LocalContentColor.current
            )
            Text(
                modifier = Modifier.constrainAs(paymentType) {
                    bottom.linkTo(parent.bottom, 5.dp)
                    linkTo(
                        start = icon.end,
                        startMargin = 10.dp,
                        end = amount.start,
                        endMargin = 10.dp,
                        bias = 0F
                    )
                }, text = transaction.account.name,
                color = LocalContentColor.current.copy(alpha = 0.5F),
                fontSize = 13.sp
            )
            Text(
                modifier = Modifier.constrainAs(amount) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }, text = amountLabel,
                color = color,
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
class TransactionPreviewDataProvider : PreviewParameterProvider<Transaction> {
    override val values: Sequence<Transaction>
        get() = sequenceOf(
            Transaction(
                121L,
                TransactionType.EXPENSE,
                1000.0,
                Category(
                    12L,
                    TransactionType.EXPENSE,
                    "Food",
                    Color.Green.toArgb().toLong(),
                    R.drawable.fastfood
                ),
                account = Account(
                    1L,
                    "Cash",
                    AccountType.BANK_ACCOUNT
                ),
                Clock.System.now()
            ),
            Transaction(
                121L,
                TransactionType.INCOME,
                1000.0,
                Category(
                    12L,
                    TransactionType.EXPENSE,
                    "Food",
                    Color.Green.toArgb().toLong(),
                    R.drawable.fastfood
                ),

                account = Account(
                    1L,
                    "Cash",
                    AccountType.BANK_ACCOUNT
                ),
                Clock.System.now()
            ),
//            Transaction(
//                121L,
//                Transaction.Type.EXPENSE,
//                1000.0,
//                Transaction.Category(
//                    12L,
//                    Transaction.Type.EXPENSE,
//                    "Food",
//                    null
//                ),
//
//                PaymentType.UPI,
//                Date()
//            ),
//            Transaction(
//                121L,
//                Transaction.Type.INCOME,
//                1000.0,
//                Transaction.Category(
//                    12L,
//                    Transaction.Type.EXPENSE,
//                    "Food",
//                    null
//                ),
//                PaymentType.UPI,
//                Date()
//            ),
//            Transaction(
//                121L,
//                Transaction.Type.INCOME,
//                1000.0,
//                Transaction.Category(
//                    12L,
//                    Transaction.Type.INCOME,
//                    "Salary",
//                    null
//                ),
//
//                PaymentType.UPI,
//                Date()
//            ),
//            Transaction(
//                121L,
//                Transaction.Type.INCOME,
//                1000.0,
//                Transaction.Category(
//                    12L,
//                    Transaction.Type.EXPENSE,
//                    "Food",
//                    null
//                ),
//
//                PaymentType.UPI,
//                Date()
//            ),
        )
}