package pl.przezdziecki.training.android.alarmmanager

import android.R
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat


private var TAG: String = "AlarmReceiver"
class AlarmReceiver: BroadcastReceiver() {
    private lateinit var ringtoneAlarm: Ringtone
    override fun onReceive(context:  Context?, intent: Intent?) {
        Log.d(TAG, "run alarm")
        var alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtoneAlarm = RingtoneManager.getRingtone(context, alarmTone)
        ringtoneAlarm.play()
        Thread.sleep(5*1000)
        ringtoneAlarm.stop()
    }
}