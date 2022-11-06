package pl.przezdziecki.training.android.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import pl.przezdziecki.training.android.alarmmanager.databinding.ActivityMainBinding
import java.time.Clock

private var TAG: String = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var alarmManager: AlarmManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSetAlarm.setOnClickListener {
            Log.d(TAG, "set alarm")
            val time: Int=binding.edtTime.text.toString().toInt()
            val triggerTime= Clock.systemDefaultZone().millis()+(time*1000)
            val i = Intent(application, AlarmReceiver::class.java)
            var pi: PendingIntent = PendingIntent.getBroadcast(
             applicationContext,
                100, i, PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.set(AlarmManager.RTC_WAKEUP,triggerTime,pi)
            Toast.makeText(applicationContext, "Alarm set", Toast.LENGTH_LONG).show();
        }
        alarmManager= getSystemService(ALARM_SERVICE) as AlarmManager
    }
}