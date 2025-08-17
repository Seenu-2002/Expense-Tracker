package com.ajay.seenu.expensetracker.android

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.text.Text

object AddTransactionWidget: GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            AddTransactionWidgetView()
        }
    }
}

class AddTransactionWidgetReceiver: GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget
        get() = AddTransactionWidget
}

@Composable
fun AddTransactionWidgetView(modifier: GlanceModifier = GlanceModifier) {
    Column(modifier = GlanceModifier.fillMaxSize()
        .background(Color.Gray)) {
        Text(text = "Add Transaction")
    }
}