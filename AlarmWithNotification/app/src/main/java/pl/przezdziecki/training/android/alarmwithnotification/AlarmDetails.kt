package pl.przezdziecki.training.android.alarmwithnotification

import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import pl.przezdziecki.training.android.alarmwithnotification.databinding.ActivityAlarmDetailsBinding


private val TAG = "AlarmDetailsLog"

class AlarmDetails : AppCompatActivity() {
    lateinit var binding:ActivityAlarmDetailsBinding

    var alarmTitle:String=""
    var nfid:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
        Log.d(TAG, "create ")
        if(intent?.hasExtra("alarm_title")!!) {
            alarmTitle = intent?.getStringExtra("alarm_title").toString()
        }
        if(intent?.hasExtra("nfid")!!) {
            nfid = intent?.getIntExtra("nfid",0)!!
        }
        Log.d(TAG, "alarm_title: ${alarmTitle}")
        binding = ActivityAlarmDetailsBinding.inflate(layoutInflater)
        unlockScreen()
        setContentView(binding.root)
        bindings()
    }


    private fun bindings() {
        binding.btnSnooze.setOnClickListener{
            snoozeAlarm()
        }
        binding.apply {
            edtTitle.setText(alarmTitle)
            btnDismiss.setOnClickListener{
                dismissAlarm()
            }
        }
    }

    private fun dismissAlarm() {
        App.AlarmPlayer.stop()
        with(NotificationManagerCompat.from(this)) {
            cancel(nfid)
        }
        finishAndRemoveTask()
    }

    private fun snoozeAlarm() {
        App.AlarmPlayer.stop()
        Log.d(TAG, "snoozeAction notification id: ${nfid}")
        var alarmManager = this.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
        val runTime= System.currentTimeMillis()+(20*1000)
        val intent= Intent(this, AlarmReceiver::class.java).apply {
            putExtra("alarm_title",alarmTitle)
        }

        val pendingIntent= PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,runTime,pendingIntent)
        Toast.makeText(this, "Alarm snooze", Toast.LENGTH_LONG).show()
        with(NotificationManagerCompat.from(this)) {
            cancel(nfid)
        }
        finishAndRemoveTask()
    }

    private fun unlockScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            this.window.addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }
}