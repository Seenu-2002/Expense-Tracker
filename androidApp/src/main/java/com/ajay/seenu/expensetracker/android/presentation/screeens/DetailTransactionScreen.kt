package com.ajay.seenu.expensetracker.android.presentation.screeens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.common.PreviewThemeWrapper
import com.ajay.seenu.expensetracker.android.presentation.viewmodels.DetailTransactionViewModel
import com.ajay.seenu.expensetracker.domain.model.Account
import com.ajay.seenu.expensetracker.domain.model.AccountType
import com.ajay.seenu.expensetracker.domain.model.Attachment
import com.ajay.seenu.expensetracker.domain.model.Category
import com.ajay.seenu.expensetracker.domain.model.Transaction
import com.ajay.seenu.expensetracker.domain.model.TransactionType
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.time.ExperimentalTime

@Composable
fun DetailTransactionScreen(
    transactionId: Long? = null,
    viewModel: DetailTransactionViewModel = hiltViewModel(),
    onEditTransaction: () -> Unit,
    onNavigateBack: () -> Unit
) {
    if(transactionId == null)
        return

    LaunchedEffect(Unit) {
        viewModel.getTransaction(transactionId)
    }
    val transactionState by viewModel.transaction.collectAsStateWithLifecycle()
    val attachments by viewModel.attachments.collectAsStateWithLifecycle()

    transactionState?.let { transaction ->
        DetailTransactionView(transaction = transaction,
            attachments = attachments,
            onEditTransaction = onEditTransaction,
            onNavigateBack = onNavigateBack)
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalTime::class)
@Composable
fun DetailTransactionView(modifier: Modifier = Modifier,
                          transaction: Transaction,
                          attachments: List<Attachment>,
                          onEditTransaction: () -> Unit,
                          onNavigateBack: () -> Unit) {
    val context = LocalContext.current

    // TODO: Need to be in UiState
    val formatter: DateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault())
    val date = formatter.format(transaction.createdAt.toEpochMilliseconds())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3F)
                .background(
                    color = if (transaction.type == TransactionType.EXPENSE)
                        Color(0xFFFD3C4A)
                    else Color(0xFF00A86B),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 0.dp,
                        bottomStart = 25.dp,
                        bottomEnd = 25.dp
                    )
                )
                .align(Alignment.TopCenter)
        ) {
            Row(modifier = Modifier.fillMaxWidth()
                .systemBarsPadding(),
                verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier
                        .clip(RoundedCornerShape(percent = 50))
                        .clickable(
                            onClick = {
                                onNavigateBack.invoke()
                            }
                        )
                        .padding(8.dp),
                    tint = Color.White,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "Detail Transaction",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$${transaction.amount}",
                    style = TextStyle(
                        fontSize = 60.sp,
                        color = Color.White
                    )
                )
                Text(text = date,
                    modifier = Modifier.padding(top = 10.dp),
                    color = Color.White)
            }
        }
        Column(modifier = Modifier.fillMaxWidth()
            .fillMaxHeight(0.7F)
            .align(Alignment.BottomCenter)) {
            Row(modifier = Modifier.fillMaxWidth()
                .offset(y = (-30).dp)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color = MaterialTheme.colorScheme.background)
                .border(width = 0.5.dp, color = Color.LightGray, shape = RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp)) {
                Column(modifier = Modifier.weight(1f)
                    .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Type",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = transaction.type.name.lowercase(Locale.getDefault()).replaceFirstChar { it.uppercase() },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)
                    .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Account", // TODO: String resource
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = transaction.account.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold)
                }
                Column(modifier = Modifier.weight(1f)
                    .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Category",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = transaction.category.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold)
                }
            }

            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), 0f)
            Canvas(Modifier.fillMaxWidth().height(1.dp)
                .padding(horizontal = 15.dp)) {

                drawLine(
                    color = Color.Gray,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    pathEffect = pathEffect
                )
            }

            Column(modifier = Modifier.fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 10.dp)
                .verticalScroll(rememberScrollState())) {
                Text(modifier = Modifier.padding(vertical = 15.dp),
                    text = "Description",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold)
                transaction.note?.let {
                    Text(text = it)
                }?: run {
                    Column (modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(modifier = Modifier.size(100.dp),
                            painter = painterResource(R.drawable.icon_description),
                            contentDescription = "No description",
                            tint = MaterialTheme.colorScheme.primary)
                        Text(text = "--No description available--",
                            color = LocalContentColor.current.copy(alpha = 0.5F))
                    }
                }
                Text(modifier = Modifier.padding(vertical = 15.dp),
                    text = "Attachments",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold)
                if(attachments.isEmpty()) {
                    Column (modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "--No attachments available--",
                            color = LocalContentColor.current.copy(alpha = 0.5F))
                    }
                } else {
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
                                .border(1.dp, LocalContentColor.current.copy(alpha = 0.2F))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(attachment.imageUri.toUri(), "image/*")
                                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                                    }
                                    context.startActivity(intent)
                                }) {
                                AsyncImage(
                                    model = attachment.imageUri.toUri(),
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
                                    }
                                )
                            }
                        }
                    }
                }

            }

            Button(modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
                onClick = {
                    onEditTransaction.invoke()
            }) {
                Text(text = "Edit")
            }
            Spacer(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.systemBars
                )
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@Preview()
@Composable
private fun DetailTransactionScreenPreview() {
    PreviewThemeWrapper {
        DetailTransactionView(
            transaction = Transaction(
                id = 0L,
                amount = (10).toDouble(),
                category = Category(
                    id = 0L,
                    label = "Test",
                    type = TransactionType.INCOME,
                    color = Color.Red.toArgb().toLong(),
                    iconRes = R.drawable.attach_money
                ),
                createdAt = kotlin.time.Clock.System.now(),
                account = Account(
                    id = 0L,
                    name = "Test Account",
                    type = AccountType.BANK_ACCOUNT
                ),
                type = TransactionType.INCOME
            ),
            attachments = emptyList(),
            onEditTransaction = {},
            onNavigateBack = {}
        )
    }
}