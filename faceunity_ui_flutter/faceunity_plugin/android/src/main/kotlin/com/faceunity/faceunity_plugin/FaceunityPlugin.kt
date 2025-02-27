package com.faceunity.faceunity_plugin

import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import com.faceunity.faceunity_plugin.modules.FUBodyBeautyPlugin
import com.faceunity.faceunity_plugin.modules.FUMakeupPlugin
import com.faceunity.faceunity_plugin.modules.FUStickerPlugin
import com.faceunity.faceunity_plugin.modules.FUFaceBeautyPlugin
import com.tencent.trtc.TRTCCloud
import com.tencent.trtc.TRTCCloudDef
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/** FaceunityPlugin */
class FaceunityPlugin : FlutterPlugin, MethodCallHandler{
    companion object {
        private const val TAG = "FaceunityPlugin"
    }

    private lateinit var channel: MethodChannel
    private val faceBeautyPlugin by lazy { FUFaceBeautyPlugin() }
    private val stickerPlugin by lazy { FUStickerPlugin() }
    private val makeupPlugin by lazy { FUMakeupPlugin() }
    private val bodyPlugin by lazy { FUBodyBeautyPlugin() }
    private val sensorHandler by lazy { SensorHandler() }
    private lateinit var context: Context
    private lateinit var fuVideoProcessor: FUVideoProcessor
    private val mainScope = MainScope()
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "faceunity_plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        fuVideoProcessor = FUVideoProcessor(context)

        sensorHandler.register(flutterPluginBinding.applicationContext) {
            fuVideoProcessor.setDeviceOrientation(it)
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        Log.i(TAG, "onMethodCall: ${call.method}, arguments: ${call.arguments}")
        when {
            faceBeautyPlugin.containsMethod(call.method) -> faceBeautyPlugin.handleMethod(call, result)
            makeupPlugin.containsMethod(call.method) -> makeupPlugin.handleMethod(call, result)
            stickerPlugin.containsMethod(call.method) -> stickerPlugin.handleMethod(call, result)
            bodyPlugin.containsMethod(call.method) -> bodyPlugin.handleMethod(call, result)
            else -> methodCall(call, result)
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        sensorHandler.unregister()
        mainScope.cancel()
    }

    private fun methodCall(call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }

            "devicePerformanceLevel" -> {
                result.success(FaceunityKit.devicePerformanceLevel)
            }

            "isNPUSupported" -> {
                result.success(false)
            }

            "turnOffEffects" -> {
                turnOnOffEffects(false)
            }

            "turnOnEffects" -> {
                turnOnOffEffects(true)
            }
            "setupRenderKit" -> setupRenderKit()
            "destoryRenderKit" -> destroyRenderKit()
            "restrictedSkinParams" -> restrictedSkinParams(call, result)
        }
    }

    private fun turnOnOffEffects(enable: Boolean) {
        FaceunityKit.isEffectsOn = enable
        faceBeautyPlugin.enableBeauty(enable)
        makeupPlugin.enableMakeup(enable)
        bodyPlugin.enableBody(enable)
        stickerPlugin.enableSticker(enable)
    }

    private fun setupRenderKit() {
        FaceunityKit.setupKit(context) {
            val trtcCloud = TRTCCloud.sharedInstance(context)
            trtcCloud.setLocalVideoProcessListener(
                TRTCCloudDef.TRTC_VIDEO_PIXEL_FORMAT_Texture_2D
                , TRTCCloudDef.TRTC_VIDEO_BUFFER_TYPE_TEXTURE, fuVideoProcessor)
            fuVideoProcessor.setHighLeveDeice(FaceunityKit.highLeveDeice)
            fuVideoProcessor.enableRender(true)
        }
    }

    private fun destroyRenderKit() {
        FaceunityKit.releaseKit()
        fuVideoProcessor.enableRender(false)
    }

        private fun restrictedSkinParams(call: MethodCall, result: Result) {
        mainScope.launch(Dispatchers.IO) {
            result.success(RestrictedSkinTool.restrictedSkinParams)
        }
    }
}
