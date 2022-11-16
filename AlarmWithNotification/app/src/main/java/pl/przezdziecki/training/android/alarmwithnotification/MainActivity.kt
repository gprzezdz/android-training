package pl.przezdziecki.training.android.alarmwithnotification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import pl.przezdziecki.training.android.alarmwithnotification.databinding.ActivityMainBinding

private var TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var alarmManager:AlarmManager
    private var alarmId:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            btnSetAlarm.setOnClickListener {
                AlarmSet()
            }
        }
        alarmManager=getSystemService(ALARM_SERVICE) as AlarmManager
    }

    private fun AlarmSet() {
        Log.d(TAG, "Set alarm")
        val iseconds=binding.edtSeconds.text.toString().toInt()
        val runTime= System.currentTimeMillis()+(iseconds*1000)
        val intent=Intent(this,AlarmReceiver::class.java)
        val pendingIntent=PendingIntent.getBroadcast(this,alarmId++,intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,runTime,pendingIntent)
        Toast.makeText(this, "Alarm set", Toast.LENGTH_LONG).show()
    }
}