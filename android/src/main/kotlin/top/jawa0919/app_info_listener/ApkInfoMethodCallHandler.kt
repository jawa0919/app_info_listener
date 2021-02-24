package top.jawa0919.app_info_listener

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class ApkInfoMethodCallHandler(private val activity: Activity,
                               private val manager: PackageManager) : MethodChannel.MethodCallHandler {
    private val TAG = "ApkInfoMethodCallHandle"
    private var receiver: ApkInfoBroadcastReceiver? = null

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        Log.i(TAG, "onMethodCall: ${call.method}")
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "searchPackageInfo" -> {
                val packageName = call.argument("packageName") ?: ""
                try {
                    manager.getPackageInfo(packageName, PackageManager.GET_GIDS)?.let {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap["appName"] = it.applicationInfo.loadLabel(manager).toString()
                        hashMap["packageName"] = it.packageName
                        hashMap["version"] = it.versionName
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            hashMap["buildNumber"] = "${it.longVersionCode}"
                        } else {
                            hashMap["buildNumber"] = "${it.versionCode}"
                        }
                        result.success(hashMap)
                    } ?: let {
                        result.error("404", "no find app", null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    result.error("500", e.message, null)
                }
            }
            "launchPackageInfo" -> {
                val packageName = call.argument("packageName") ?: ""
                val intent = manager.getLaunchIntentForPackage(packageName)
                activity.startActivity(intent)
                result.success(null)
            }
            "startPackageInfoChangeListen" -> {
                val filter = IntentFilter()
                filter.addAction(Intent.ACTION_PACKAGE_ADDED)
                filter.addAction(Intent.ACTION_PACKAGE_REMOVED)
                filter.addAction(Intent.ACTION_PACKAGE_REPLACED)
                filter.addDataScheme("package")
                receiver = ApkInfoBroadcastReceiver(activity)
                activity.registerReceiver(receiver, filter)
                result.success(null)
            }
            "stopPackageInfoChangeListen" -> {
                activity.unregisterReceiver(receiver)
                receiver = null
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }
}