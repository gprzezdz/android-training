package pl.przezdziecki.training.android.servicesandalarm

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val TAG = "AlarmForegroundService-LOG"
private const val CHANNEL_ID = "Services and Alarm id"

class AlarmForegroundService : Service() {

    private var run = false
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "Service onStartCommand")
        run = true
        var t = Thread {
            while (run) {
                Log.e(TAG, "Service is running... ${startId} ${getTime()}")
                try {
                    Thread.sleep(5000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            Log.e(TAG, "Service is finishing... ${startId} ${getTime()}")
        }
        t.start()
        val intent = Intent(this, MainActivity::class.java).apply {

        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val nfc =
            NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
        getSystemService(NotificationManager::class.java).createNotificationChannel(nfc)
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentText("Alarm scheduler")
            .setContentTitle("Services and Alarm")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentIntent(pendingIntent)

        startForeground(1001, notification.build())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        Log.e(TAG, "Service onDestroy ${getTime()}")
        run = false
        super.onDestroy()
    }

    private fun getTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return current.format(formatter)
    }
}