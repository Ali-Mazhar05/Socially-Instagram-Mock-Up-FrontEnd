package com.example.smd_assignment_i230796

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New Token: $token")
        saveTokenToDatabase(token)
    }

    private fun saveTokenToDatabase(token: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(uid)
                .child("fcmToken")
                .setValue(token)
                .addOnSuccessListener {
                    Log.d("FCM", "Token saved for $uid")
                }
                .addOnFailureListener {
                    Log.e("FCM", "Failed to save token: ${it.message}")
                }
        } else {
            Log.w("FCM", " No user logged in — cannot save token now.")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Notification"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""
        val data = remoteMessage.data

        Log.d("FCM", "Notification Data: $data")

        handleNotification(title, body, data)
    }

    private fun handleNotification(title: String, messageBody: String, data: Map<String, String>) {
        val type = data["type"]
        val senderId = data["senderId"]
        val receiverId = data["receiverId"]
        val chatId = data["chatId"]
        val senderName =data["senderName"]

        // Fetch profile image dynamically from Firebase before showing notification
        if (senderId != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                .child(senderId)
                .child("profileImage")
                .get()
                .addOnSuccessListener { snapshot ->
                    val senderProfileBase64 = snapshot.value as? String ?: ""
                    sendNotification(
                        title,
                        messageBody,
                        data,
                        senderProfileBase64
                    )
                }
                .addOnFailureListener {
                    Log.e("FCM", "Failed to fetch sender profile: ${it.message}")
                    sendNotification(title, messageBody, data, "")
                }
        } else {
            sendNotification(title, messageBody, data, "")
        }
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        data: Map<String, String>,
        senderProfileBase64: String
    ) {
        val type = data["type"]
        var intent: Intent? = null
        val sender = data["senderName"]

        when (type) {
            "follow_request" -> {
                intent = Intent(this, FollowRequestActivity::class.java)
                intent.putExtra("senderId", data["senderId"])
                intent.putExtra("senderName", data["senderName"])
                intent.putExtra("senderProfileBase64", senderProfileBase64)
            }

            "chat_message", "screenshot_alert" -> {
                intent = Intent(this, chat_screen::class.java)
                intent.putExtra("chatId", data["chatId"])
                intent.putExtra("receiverId", data["senderId"])
                intent.putExtra("receiverName", data["senderName"])
                intent.putExtra("receiverProfileBase64", senderProfileBase64)
            }

            else -> {
                intent = Intent(this, main_feed::class.java)
            }
        }

        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)

        val pendingIntent = PendingIntent.getActivity(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setColor(Color.parseColor("#3F51B5"))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "App Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Notifications for chat, follow, and alerts"
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    companion object {
        fun updateTokenAfterLogin() {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(uid)
                        .child("fcmToken")
                        .setValue(token)
                        .addOnSuccessListener {
                            Log.d("FCM", "✅ Token updated after login: $token")
                        }
                        .addOnFailureListener {
                            Log.e("FCM", "❌ Failed to update token after login: ${it.message}")
                        }
                }
            }
        }
    }
}
