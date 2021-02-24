package top.jawa0919.app_info_listener

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import io.flutter.plugin.common.EventChannel

class ApkInfoBroadcastReceiver(private val activity: Activity) : BroadcastReceiver(), EventChannel.StreamHandler {
    private val TAG = "ApkInfoBroadcastReceive"

    private val mainHandler = Handler(Looper.getMainLooper())
    private var eventSink: EventChannel.EventSink? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val packageName = intent?.dataString ?: ""
        when (intent?.action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                Log.i(TAG, "安装了: $packageName")
                sendEventString("ADDED", packageName)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                Log.i(TAG, "卸载了: $packageName")
                sendEventString("REMOVED", packageName)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                Log.i(TAG, "更新了: $packageName")
                sendEventString("REPLACED", packageName)
            }
            else -> {
            }
        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        Log.i(TAG, "ApkInfoBroadcastReceiver onListen")
        events?.let { this.eventSink = it }
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_PACKAGE_ADDED)
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        filter.addDataScheme("package")
        activity.registerReceiver(this, filter)
    }

    override fun onCancel(arguments: Any?) {
        Log.i(TAG, "ApkInfoBroadcastReceiver onCancel")
        activity.unregisterReceiver(this)
        this.eventSink = null
    }

    private fun sendEventString(action: String, packageName: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["action"] = action
        hashMap["packageName"] = packageName
        val runnable = Runnable { eventSink?.success(hashMap) }
        mainHandler.post(runnable)
    }
}