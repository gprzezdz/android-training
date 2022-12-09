package pl.przezdziecki.training.android.servicesandalarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

private const val TAG ="BootBroadcastReceiver-LOG"
class BootBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"OnReceive")
        if (intent!!.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG,"Action boot")
            val serviceIntent = Intent(context, AlarmForegroundService::class.java)
            context!!.startForegroundService(serviceIntent)
        }
    }
}