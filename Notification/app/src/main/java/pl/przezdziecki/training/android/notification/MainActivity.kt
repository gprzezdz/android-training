package pl.przezdziecki.training.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import pl.przezdziecki.training.android.notification.databinding.ActivityMainBinding


private val TAG: String = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val EXPANDABLE_CHANNEL_ID = "ExpandableChannelID"
    private val EXPANDABLE_CHANNEL_NAME = "Expandable channel"
    private val SIMPLE_CHANNEL_ID = "SimpleChannelID"
    private val SIMPLE_CHANNEL_NAME = "Simple channel"
    private lateinit var alarmPlayer: Ringtone
    private lateinit var mBroadcastReceiver: receiverAlarmBroadcaster
    var notificationCount = 0
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        simpleNotificationChannelCreate()
        expandableNotificationChannelCreate()
        alarmBroadcast()
        val notificationManager = NotificationManagerCompat.from(this)
        binding.btnNotificationShow.setOnClickListener {
            Log.d(TAG, "btnNotificationShow click simple notification")
            val simpleBuilder = simpleNotification()
            notificationManager.notify(notificationCount++, simpleBuilder.build())
        }
        binding.btnExpandableNotification.setOnClickListener {
            Log.d(TAG, "btnNotificationShow click expandable notifiation")
            Thread.sleep(4 * 1000)
            val expandableBuilder = expandableNotification()
            notificationManager.notify(notificationCount++, expandableBuilder.build())
            alarmPlayer.play()
        }
    }

    private fun alarmBroadcast() {
        Log.d(TAG, "run alarm")
        var alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        alarmPlayer = RingtoneManager.getRingtone(applicationContext, alarmTone)
        mBroadcastReceiver = receiverAlarmBroadcaster(alarmPlayer)
        val intentFilter = IntentFilter()
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT)
        intentFilter.addAction("snooze")
        intentFilter.addAction("shut")
        intentFilter.addAction("show")
        registerReceiver(mBroadcastReceiver, intentFilter)
    }

    private fun simpleNotification(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, SIMPLE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_info_24)
            .setContentTitle("Simple notification")
            .setContentText("Hello World!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    private fun expandableNotification(): NotificationCompat.Builder {
        val snoozeIntent = Intent("snooze")
        val snoozePendingIntent = PendingIntent.getBroadcast(this, 0, snoozeIntent,  PendingIntent.FLAG_IMMUTABLE)
        val shutIntent = Intent("shut")
        val shutPendingIntent = PendingIntent.getBroadcast(this, 0, shutIntent,  PendingIntent.FLAG_IMMUTABLE)
        val showIntent = Intent("show")
        val showPendingIntent = PendingIntent.getBroadcast(this, 0, showIntent,  PendingIntent.FLAG_IMMUTABLE)


        /*val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)*/
        return NotificationCompat.Builder(this, EXPANDABLE_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_info_24)
            .setContentTitle("expandable notification")
            .setContentText("Hello World in expandable!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(showPendingIntent)
            .setDeleteIntent(snoozePendingIntent)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "Big text Bla bla bla bla Bla bla bla bla " +
                                "Bla bla bla bla " +
                                "Bla bla bla bla" +
                                "Bla bla bla bla" +
                                "Bla bla bla bla" +
                                "Bla bla bla bla" +
                                "Bla bla bla bla" +
                                ""
                    )
            )
            .addAction(R.drawable.ic_baseline_info_24,"Snooze",snoozePendingIntent)
            .addAction(R.drawable.ic_baseline_info_24,"Shut",shutPendingIntent)
            .addAction(R.drawable.ic_baseline_info_24,"Show",showPendingIntent)
            .setAutoCancel(true)
            .setCategory(Notification.CATEGORY_ALARM)
    }

    private fun simpleNotificationChannelCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(SIMPLE_CHANNEL_ID, SIMPLE_CHANNEL_NAME, importance).apply {
                    description = "Training simple notification"
                }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun expandableNotificationChannelCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_MAX
            val channel = NotificationChannel(
                EXPANDABLE_CHANNEL_ID,
                EXPANDABLE_CHANNEL_NAME,
                importance
            ).apply {
                description = "Training expandable notification"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    class receiverAlarmBroadcaster(var alarmPlayer: Ringtone): BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("receiverAlarmBroadcaster","action: ${intent?.action}")
            alarmPlayer.stop()
            if(intent?.action=="show")
            {
                val intent: Intent = Intent(context, MainActivity::class.java)
                context?.startActivity(intent)
            }
        }
    }
}