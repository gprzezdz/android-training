package pl.przezdziecki.training.android.alarmwithnotification

import android.app.KeyguardManager
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import pl.przezdziecki.training.android.alarmwithnotification.databinding.ActivityAlarmDetailsBinding
import pl.przezdziecki.training.android.alarmwithnotification.databinding.ActivityMainBinding

private val TAG = "AlarmDetails"

class AlarmDetails : AppCompatActivity() {
    lateinit var binding:ActivityAlarmDetailsBinding
    private lateinit var alarmPlayer: Ringtone
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmDetailsBinding.inflate(layoutInflater)
        unlockScreen()
        setContentView(binding.root)
        bindings()
        playAlarmSound()
    }

    private fun bindings() {
        binding.btnSleep.setOnClickListener{
            sleepAlarm()
        }
    }

    private fun sleepAlarm() {
        alarmPlayer.stop()
    }

    private fun playAlarmSound() {
        Log.d(TAG, "run alarm")
        var alarmTone: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        alarmPlayer = RingtoneManager.getRingtone(applicationContext, alarmTone)
        alarmPlayer.play()
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