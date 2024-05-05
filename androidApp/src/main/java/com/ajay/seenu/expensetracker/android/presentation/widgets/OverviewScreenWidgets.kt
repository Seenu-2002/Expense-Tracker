package com.ajay.seenu.expensetracker.android.presentation.widgets

import android.icu.text.DecimalFormat
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.ajay.seenu.expensetracker.android.domain.data.Transaction
import com.ajay.seenu.expensetracker.entity.PaymentType
import java.util.Date
import java.util.Locale

@Preview(showBackground = true)
@Composable
private fun OverviewCardPreview(@PreviewParameter(OverallDataProvider::class) data: OverallData) {
    OverviewCard(
        modifier = Modifier
            .fillMaxWidth()
            .background(shape = RoundedCornerShape(4.dp), color = Color.Black), data = data
    )
}

@Composable
fun OverviewCard(
    modifier: Modifier = Modifier, data: OverallData
) {

    val expensePercentageRaw = data.expense / data.income
    val expensePercentageLabel =
        String.format(Locale.ENGLISH, "%.2f", expensePercentageRaw * 100) + " % "

    ConstraintLayout(modifier = modifier) {
        val (expense, income, progress) = createRefs()
        Column(
            modifier = Modifier
                .background(shape = RoundedCornerShape(6.dp), color = Color(0xFF8BC34A))
                .constrainAs(income) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                    end.linkTo(expense.start, 4.dp)
                    bottom.linkTo(expense.bottom)
                }
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(modifier = Modifier.padding(bottom = 4.dp), text = "Income", fontSize = 14.sp)
            Text(text = "Rs. ${data.getIncomeLabel()}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        }

        Column(
            modifier = Modifier
                .background(shape = RoundedCornerShape(6.dp), color = Color(0xFFEE6C62))
                .constrainAs(expense) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(income.end, 4.dp)
                    end.linkTo(parent.end, 8.dp)
                }
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            Text(modifier = Modifier.padding(bottom = 4.dp), text = "Expense", fontSize = 14.sp)
            Text(text = "Rs. ${data.getExpenseLabel()}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

        }

        ConstraintLayout(modifier = Modifier
            .constrainAs(progress) {
                width = Dimension.fillToConstraints
                top.linkTo(expense.bottom, 8.dp)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                bottom.linkTo(parent.bottom, 8.dp)
            }
            .padding(bottom = 8.dp, start = 24.dp, end = 24.dp)) {

            val (progressBarBg, progressBar, marker, percentageText) = createRefs()
            val percentage = if (expensePercentageRaw > 1) {
                1
            } else {
                expensePercentageRaw
            }.toFloat()

            val color = if (percentage <= .65) {
                Color(0xFF4A6827)
            } else if (percentage <= .8) {
                Color(0xFFFFC107)
            } else {
                Color(0xFF91423C)
            }

            Box(
                modifier = Modifier
                    .constrainAs(progressBarBg) {
                        top.linkTo(marker.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(shape = RoundedCornerShape(4.dp), color = Color.Gray)
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

            Canvas(modifier = Modifier
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

                    drawPath(path = this, color = Color.White)
                }
            }

            Text(
                modifier = Modifier
                    .wrapContentWidth()
                    .constrainAs(percentageText) {
                        top.linkTo(parent.top)
                        start.linkTo(progressBar.end)
                        end.linkTo(progressBar.end)
                    },
                textAlign = TextAlign.Center,
                text = expensePercentageLabel,
                fontSize = 12.sp
            )

        }
    }

}

data class OverallData(
    val income: Double, val expense: Double
)

val decimalFormatter = DecimalFormat("#0.0#") // FIXME: Generic and user config
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
            OverallData(2342.33, 1031.44),
            OverallData(22333.33, 1031.44),
            OverallData(22333.33, 15333.44),
            OverallData(21333.33, 22333.44),
            OverallData(21333.33, 0.0)
        ).asSequence()

}

@Preview
@Composable
fun TransactionPreviewRowPreview(@PreviewParameter(TransactionPreviewDataProvider::class) data: Transaction) {
    TransactionPreviewRow(Modifier.fillMaxWidth(), data)
}

@Composable
fun TransactionPreviewRow(modifier: Modifier = Modifier, transaction: Transaction) {
    ConstraintLayout(modifier.padding(8.dp)) {
        val (icon, title, paymentType, amount) = createRefs()

        val decimalFormat = DecimalFormat("#0.00").apply {
            decimalFormatSymbols.let {
                it.decimalSeparator = '.'
                it.groupingSeparator = ','
            }
        }
        var amountLabel = decimalFormat.format(transaction.amount)
        val (rotation, color, tintColor) = if (transaction.type == Transaction.Type.EXPENSE) {
            amountLabel = "- $amountLabel"
            Triple(-90F, Color(0xFFEE6C62), Color(0xFF91423C))
        } else {
            amountLabel = "+ $amountLabel"
            Triple(90F, Color(0xFF8BC34A), Color(0xFF4A6827))
        }

        Box(
            modifier = Modifier
                .width(32.dp)
                .height(32.dp)
                .constrainAs(icon) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .background(color = color, shape = RoundedCornerShape(3.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp)
                    .rotate(rotation),
                tint = tintColor,
                contentDescription = "Icon",
            )
        }

        Text(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                linkTo(
                    start = icon.end,
                    startMargin = 8.dp,
                    end = amount.start,
                    endMargin = 8.dp,
                    bias = 0F
                )

            }, text = transaction.category.label,
            fontSize = 14.sp
        )
        Text(
            modifier = Modifier.constrainAs(paymentType) {
                top.linkTo(title.bottom)
                linkTo(
                    start = icon.end,
                    startMargin = 8.dp,
                    end = amount.start,
                    endMargin = 8.dp,
                    bias = 0F
                )
            }, text = transaction.paymentType.name,
            color = Color(0xFFCCCCCC),
            fontSize = 12.sp
        )
        Text(
            modifier = Modifier.constrainAs(amount) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }, text = amountLabel,
            color = color,
            fontSize = 14.sp
        )
    }
}

class TransactionPreviewDataProvider : PreviewParameterProvider<Transaction> {
    override val values: Sequence<Transaction>
        get() = sequenceOf(
            Transaction(
                121L,
                Transaction.Type.EXPENSE,
                1000.0,
                Transaction.Category(
                    12L,
                    Transaction.Type.EXPENSE,
                    "Food",
                    null
                ),
                PaymentType.UPI,
                Date()
            ),
            Transaction(
                121L,
                Transaction.Type.INCOME,
                1000.0,
                Transaction.Category(
                    12L,
                    Transaction.Type.EXPENSE,
                    "Food",
                    null
                ),

                PaymentType.UPI,
                Date()
            ),
            Transaction(
                121L,
                Transaction.Type.EXPENSE,
                1000.0,
                Transaction.Category(
                    12L,
                    Transaction.Type.EXPENSE,
                    "Food",
                    null
                ),

                PaymentType.UPI,
                Date()
            ),
            Transaction(
                121L,
                Transaction.Type.INCOME,
                1000.0,
                Transaction.Category(
                    12L,
                    Transaction.Type.EXPENSE,
                    "Food",
                    null
                ),
                PaymentType.UPI,
                Date()
            ),
            Transaction(
                121L,
                Transaction.Type.INCOME,
                1000.0,
                Transaction.Category(
                    12L,
                    Transaction.Type.INCOME,
                    "Salary",
                    null
                ),

                PaymentType.UPI,
                Date()
            ),
            Transaction(
                121L,
                Transaction.Type.INCOME,
                1000.0,
                Transaction.Category(
                    12L,
                    Transaction.Type.EXPENSE,
                    "Food",
                    null
                ),

                PaymentType.UPI,
                Date()
            ),
        )
}