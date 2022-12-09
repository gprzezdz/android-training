package pl.przezdziecki.training.android.servicesandalarm

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import pl.przezdziecki.training.android.servicesandalarm.databinding.ActivityMainBinding

private const val TAG = "MainActivity-LOG"

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatus()
        binding.apply {
            btnStartService.setOnClickListener {
                if (!isAlarmForegroundServiceRunning()) {
                    Log.d(TAG, "start service")
                    startForegroundService(
                        Intent(
                            applicationContext,
                            AlarmForegroundService::class.java
                        )
                    )
                    setStatus()
                }
            }
            btnRestatrService.setOnClickListener {
                Log.d(TAG, "restart service")
                stopService(Intent(applicationContext, AlarmForegroundService::class.java))
                startForegroundService(
                    Intent(
                        applicationContext,
                        AlarmForegroundService::class.java
                    )
                )
                setStatus()
            }
            btnStopService.setOnClickListener {
                Log.d(TAG, "stop service")
                stopService(Intent(applicationContext, AlarmForegroundService::class.java))
                setStatus()
            }
        }

    }

    private fun setStatus() {
        if (isAlarmForegroundServiceRunning()) {
            binding.texStatusService.text = "Service is running ..."
        } else {
            binding.texStatusService.text = "Service not running ..."
        }
    }

    private fun isAlarmForegroundServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (AlarmForegroundService::class.java.getName() == service.service.className) {
                return true
            }
        }
        return false
    }

}