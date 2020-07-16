package com.example.yeniappwkotlin.data.firebaseNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.ui.activity.home.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FcmMessagingService : FirebaseMessagingService() {
    private val MY_CHANNEL_ID = "19465"
    private val notifyId = 1
    override fun onMessageReceived(p0: RemoteMessage) {
        showNotification(p0.data["message"])
    }

    private fun showNotification(s: String?) {
        val notificationManager: NotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val gelenDeger = s!!.split(":")
        //notificationStatues==> 0 = tamamlama beğendi, 1= yorum yaptı, 2= chat, 3= paylaşımını beğendi.

        val title = gelenDeger[0]
        val mainValue = gelenDeger[1]
        val notificationStatues = gelenDeger[2]
        val name = getString(R.string.voveyle)
        val soundUri = Settings.System.DEFAULT_NOTIFICATION_URI

        val mLong: LongArray = longArrayOf(1000,1000)
        val intent = Intent(this, MainActivity::class.java)
        val importance = NotificationManager.IMPORTANCE_HIGH

        if (notificationStatues == "2"){
            intent.putExtra("push_fragment","chat_fragment")
        }else{
            intent.putExtra("push_fragment","like_fragment")
        }
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                MY_CHANNEL_ID, name, importance
            )
            mChannel.lightColor = Color.GRAY
            mChannel.enableLights(true)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            mChannel.setSound(soundUri, audioAttributes)
            notificationManager.createNotificationChannel(mChannel)
        }


        val builder = NotificationCompat.Builder(this,MY_CHANNEL_ID)
        builder.let {
            it.setAutoCancel(true)
            it.setContentTitle(title)
            it.setContentText(mainValue)


            it.setSmallIcon(R.drawable.splash_1)
            it.setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.logo))
            it.setContentIntent(pendingIntent)
            it.setVibrate(mLong)
            it.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            it.setChannelId(MY_CHANNEL_ID)
        }
        notificationManager.notify(notifyId,builder.build())
    }
}