package com.ajay.seenu.expensetracker.android.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.ajay.seenu.expensetracker.android.R
import com.ajay.seenu.expensetracker.android.presentation.activities.MainActivity
import com.ajay.seenu.expensetracker.domain.model.budget.Budget

class NotificationService(private val context: Context) {

    fun sendBudgetAlert(budget: Budget, percentage: Double, spentAmount: Double) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted - notification won't show
                return
            }
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel (for Android 8.0+)
        createNotificationChannel(notificationManager)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_budget", budget.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context, budget.id.toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.account_balance_wallet)
            .setContentTitle("Budget Alert: ${budget.name}")
            .setContentText("You've spent ${String.format("%.1f", percentage)}% of your budget ($${String.format("%.2f", spentAmount)} / $${String.format("%.2f", budget.amount)})")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("You've spent ${String.format("%.1f", percentage)}% of your ${budget.name} budget. You've used $${String.format("%.2f", spentAmount)} out of $${String.format("%.2f", budget.amount)}."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(budget.id.toInt(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for budget threshold alerts"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "budget_alerts"
    }
}