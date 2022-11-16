package pl.przezdziecki.training.android.alarmwithnotification

import android.app.*
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService

private val TAG: String = "AlarmReceiver"
private val CHANNEL_ID: String = "ALARM_TRAINING_CHANNEL_Id"
private val CHANNEL_NAME: String = "ALARM_TRAINING_CHANNEL_NAME"

class AlarmReceiver : BroadcastReceiver() {
    private lateinit var ringtoneAlarm: Ringtone
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        val fullScreenIntent = Intent(context, AlarmDetails::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder =
            NotificationCompat.Builder(context!!, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Alarm")
                .setContentText("Parse from param")
                .setContentIntent(fullScreenPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)

        val incomingCallNotification = notificationBuilder.build()
        createNotificationChannel(context)
        with(NotificationManagerCompat.from(context)) {
            notify(2, incomingCallNotification)
        }
    }


    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createNotificationChannel")
            val descriptionText = "AndroidTest"
            val importance = NotificationManager.IMPORTANCE_MAX
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}