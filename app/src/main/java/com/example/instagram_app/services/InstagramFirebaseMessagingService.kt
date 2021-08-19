package com.example.instagram_app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.instagram_app.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class InstagramFirebaseMessagingService : FirebaseMessagingService() {

    val TAG = "FirebaseService"
    private val CHANNEL_ID = "channelId"
    private val CHANNEL_NAME = "channelName"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        createNotificationChannel()

        var notificationTitle: String = ""
        var notificationMessage: String = ""

        message.notification?.let {
            Log.d(TAG, "onMessageReceived: from console")

            notificationTitle = message.notification?.title.toString()
            notificationMessage = message.notification?.body.toString()
        }

        if (message.data["title"] != null || message.data["message"] != null) {
            Log.d(TAG, "onMessageReceived: from client device")

            notificationTitle = message.data["title"].toString()
            notificationMessage = message.data["message"].toString()
        }

        Log.d(TAG, "onMessageReceived: notificationTitle: $notificationTitle")
        Log.d(TAG, "onMessageReceived: notificationMessage: $notificationMessage")

        val bundle = Bundle()

        bundle.putString("notificationTitle", notificationTitle)
        bundle.putString("notificationMessage", notificationMessage)

//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            putExtra("bundle", bundle)
//        }

//        val pendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            intent,
//            PendingIntent.FLAG_ONE_SHOT
//        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .apply {
                setContentTitle(notificationTitle)
                setContentText(notificationMessage)
                setSmallIcon(R.mipmap.ic_launcher_round)
                setAutoCancel(true)
//                setContentIntent(pendingIntent)
            }.build()

        val notificationManagerCompat = NotificationManagerCompat.from(this)
        notificationManagerCompat.notify(Random.nextInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        Log.d(TAG, "onNewToken: called -> newToken: $newToken")
    }
}