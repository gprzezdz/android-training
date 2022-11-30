package pl.przezdziecki.training.android.alarmwithnotification


import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.FILL_IN_DATA
import android.content.Intent.getIntent
import android.hardware.display.DisplayManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.Display
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService


private val TAG: String = "AlarmReceiverLog"
private val CHANNEL_SCREEN_OFF_ID: String = "SCREEN_OFF_ID"
private val CHANNEL_SCREEN_OFF_NAME: String = "SCREEN OFF"
private val CHANNEL_SCREEN_ON_ID: String = "SCREEN_ON_ID"
private val CHANNEL_SCREEN_ON_NAME: String = "SCREEN ON"

//needs refactoring ;-)
class AlarmReceiver : BroadcastReceiver() {
 var alarmTitle:String=""

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "onReceive")
        //check  screen if off or  on
        //if off then show notify with setFullScreenIntent
        if(intent?.hasExtra("alarm_title")!!) {
            alarmTitle = intent?.getStringExtra("alarm_title").toString()
        }
        Log.d(TAG, "alarm_title: ${alarmTitle}")

        if (intent?.action != null) {
            Log.d(TAG, "onReceive action: ${intent?.action} nfid: ${intent.getIntExtra("nfid", 0)}")
            when (intent?.action) {
                "snooze" -> snoozeAction(context, intent?.getIntExtra("nfid", 0)!!)
                "show" -> showAction(context, intent?.getIntExtra("nfid", 0)!!)
                "dismiss" -> dismissAction(context, intent?.getIntExtra("nfid", 0)!!)
            }
            return
        }
        App.AlarmPlayer.player = RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        )
        if (isScreenActive(context)) {
            Log.d(TAG, "Screen if on")
            screenOn(context)
        } else {
            Log.d(TAG, "Screen if off")
            screenOff(context)
        }
    }

    private fun dismissAction(context: Context?, i: Int) {
        Log.d(TAG, "showAction notification id: ${i}")
        App.AlarmPlayer.stop()
        with(NotificationManagerCompat.from(context!!)) {
            cancel(i)
        }
    }

    private fun snoozeAction(context: Context?, i: Int) {
        Log.d(TAG, "snoozeAction notification id: ${i}")
        var alarmManager = context?.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val runTime= System.currentTimeMillis()+(20*1000)
        val intent=Intent(context, AlarmReceiver::class.java).apply {
            putExtra("alarm_title",alarmTitle)
            putExtra("nfid", 2)
        }

        val pendingIntent=PendingIntent.getBroadcast(context,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,runTime,pendingIntent)
        App.AlarmPlayer.stop()
        Toast.makeText(context, "Alarm snooze", Toast.LENGTH_LONG).show()
        with(NotificationManagerCompat.from(context!!)) {
            cancel(i)
        }
    }

    private fun showAction(context: Context?, i: Int) {
        Log.d(TAG, "showAction notification id: ${i}")
        App.AlarmPlayer.stop()
        with(NotificationManagerCompat.from(context!!)) {
            cancel(i)
        }
        val intent = Intent(context, AlarmDetails::class.java).apply {
            putExtra("alarm_title",alarmTitle)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    private fun screenOn(context: Context?) {
        Log.d(TAG, "Screen if on w")
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "snooze"
            putExtra("nfid", 2)
            putExtra("alarm_title",alarmTitle)
        }
        val snoozePendingIntent =
            PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)


        val showIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "show"
            putExtra("nfid", 2)
            putExtra("alarm_title",alarmTitle)
        }
        val showPendingIntent =
            PendingIntent.getBroadcast(context, 0, showIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        Log.d(TAG, "Screen if on w")
        val dismissIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "dismiss"
            putExtra("nfid", 2)
        }
        val dismissPendingIntent =
            PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(context!!, CHANNEL_SCREEN_ON_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
            .setContentTitle("ToDo alarm")
            .setContentText(alarmTitle)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(showPendingIntent)
            .setDeleteIntent(dismissPendingIntent)
            .addAction(
                R.drawable.ic_baseline_notifications_active_24,
                "Snooze",
                snoozePendingIntent
            )
            .addAction(R.drawable.ic_baseline_notifications_active_24, "Show", showPendingIntent)
            .addAction(R.drawable.ic_baseline_notifications_active_24, "Dismiss", dismissPendingIntent)
            .setAutoCancel(false)
            .setCategory(Notification.CATEGORY_ALARM)
        val alarmNotification = notificationBuilder.build()
        createOnNotificationChannel(context)
        with(NotificationManagerCompat.from(context)) {
            notify(2, alarmNotification)
        }
        App.AlarmPlayer.play()
    }

    private fun screenOff(context: Context?) {
        val fullScreenIntent = Intent(context, AlarmDetails::class.java).apply {
            putExtra("alarm_title",alarmTitle)
            putExtra("nfid", 2)
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val snoozeIntent = Intent(context, AlarmReceiver::class.java).apply {
            action = "snooze"
            putExtra("nfid", 2)
            putExtra("alarm_title",alarmTitle)
        }
        val snoozePendingIntent =
            PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder =
            NotificationCompat.Builder(context!!, CHANNEL_SCREEN_OFF_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Todo alarm")
                .setContentText(alarmTitle)
                .setDeleteIntent(snoozePendingIntent)
                .setContentIntent(fullScreenPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(fullScreenPendingIntent, true)

        val incomingCallNotification = notificationBuilder.build()
        createOffNotificationChannel(context)
        with(NotificationManagerCompat.from(context)) {
            notify(2, incomingCallNotification)
        }
        Log.d(TAG, "run alarm")
        App.AlarmPlayer.play()
    }

    /**
     * check is Screen is on
     */
    private fun isScreenActive(context: Context?): Boolean {
        val dm: DisplayManager =
            context?.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        for (display in dm.displays) {
            if (display.state != Display.STATE_OFF) {
                val keyguardManager =
                    context!!.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                if (!keyguardManager.isDeviceLocked) {
                    return true
                }
            }
        }
        return false
    }


    private fun createOffNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createOffNotificationChannel")
            val descriptionText = "AndroidTest"
            val importance = NotificationManager.IMPORTANCE_MAX
            val channel = NotificationChannel(
                CHANNEL_SCREEN_OFF_ID,
                CHANNEL_SCREEN_OFF_NAME,
                importance
            ).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createOnNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "createOnNotificationChannel")
            val descriptionText = "AndroidTest"
            val importance = NotificationManager.IMPORTANCE_MAX
            val channel = NotificationChannel(
                CHANNEL_SCREEN_ON_ID,
                CHANNEL_SCREEN_ON_NAME,
                importance
            ).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}