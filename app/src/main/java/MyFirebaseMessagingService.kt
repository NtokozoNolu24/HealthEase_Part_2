
package com.example.healthease_part_2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "healthease_notifications"
        const val CHANNEL_NAME = "HealthEase Notifications"

        private const val TAG = "HealthEaseFCM"
    }

    // Called when Firebase generates a new device token.
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.d(TAG, "A new FCM token was generated.")

        FcmTokenManager.saveToken(token)
    }

    // Called when an FCM message is delivered to the app.
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d(TAG, "FCM MESSAGE RECEIVED!")
        Log.d(TAG, "Message ID: ${message.messageId}")
        Log.d(TAG, "From: ${message.from}")
        Log.d(TAG, "Data: ${message.data}")

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "HealthEase"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "You have a new reminder."

        Log.d(TAG, "Notification title: $title")
        Log.d(TAG, "Notification body: $body")

        showNotification(title, body)
    }

    // Display the notification.
    private fun showNotification(title: String, body: String) {

        Log.d(TAG, "Preparing to display notification.")

        createNotificationChannel()

        // Check Android 13+ notification permission.
        if (
            Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification permission has not been granted.")
            return
        }

        try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)

            val pendingIntent = intent?.let {
                PendingIntent.getActivity(
                    this,
                    0,
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )
            }

            val notification = NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(body)
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .apply {
                    if (pendingIntent != null) {
                        setContentIntent(pendingIntent)
                    }
                }
                .build()

            NotificationManagerCompat.from(this).notify(
                System.currentTimeMillis().toInt(),
                notification
            )

            Log.d(TAG, "Notification displayed successfully.")

        } catch (error: SecurityException) {
            Log.e(TAG, "Notification permission error.", error)

        } catch (error: Exception) {
            Log.e(TAG, "Failed to display notification.", error)
        }
    }

    // Create notification channel for Android 8 and newer.
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Medication and appointment reminders"
            }

            val manager = getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            manager.createNotificationChannel(channel)

            Log.d(TAG, "Notification channel created or already exists.")
        }
    }
}
