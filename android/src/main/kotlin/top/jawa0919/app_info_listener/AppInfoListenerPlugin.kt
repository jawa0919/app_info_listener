package top.jawa0919.app_info_listener

import android.app.Activity
import android.app.Application
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry

class AppInfoListenerPlugin : FlutterPlugin, ActivityAware {

    private var methodChannel: MethodChannel? = null
    private var eventChannel: EventChannel? = null
    private var pluginBinding: FlutterPlugin.FlutterPluginBinding? = null
    private var application: Application? = null
    private var activity: Activity? = null

    companion object {
        @Deprecated("only use flutter v1")
        fun registerWith(registrar: PluginRegistry.Registrar) {
            if (registrar.activity() == null) return
            val plugin = AppInfoListenerPlugin()
            val application = registrar.activeContext().applicationContext as Application
            plugin.setup(registrar.messenger(), application, registrar.activity())
        }
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.pluginBinding = binding
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        this.pluginBinding = null
    }

    override fun onDetachedFromActivity() {
        teardown()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        pluginBinding?.let {
            setup(it.binaryMessenger, it.applicationContext as Application, binding.activity)
        }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    private fun setup(messenger: BinaryMessenger, application: Application, activity: Activity) {
        methodChannel = MethodChannel(messenger, "top.jawa0919.app_info_listener/method")
        eventChannel = EventChannel(messenger, "top.jawa0919.app_info_listener/event")

        this.application = application
        this.activity = activity

        val handler = ApkInfoMethodCallHandler(activity, activity.packageManager)
        val receiver = ApkInfoBroadcastReceiver(activity)
        methodChannel?.setMethodCallHandler(handler)
        eventChannel?.setStreamHandler(receiver)
    }

    private fun teardown() {
        methodChannel?.setMethodCallHandler(null)
        eventChannel?.setStreamHandler(null)
        application = null
        activity = null
        methodChannel = null
        eventChannel = null
    }
}
