package com.jqueue.openglapp

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.jqueue.openglapp.renderer.AirRender
import com.jqueue.openglapp.renderer.AirSmoothShaderRenderer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG = this.javaClass.simpleName

    private lateinit var activityManager: ActivityManager

    private fun log(msg: String) {
        Log.d(TAG, "Thread(${Thread.currentThread().name})-$msg")
    }

    private fun checkOpenGLVersion() =
        activityManager.deviceConfigurationInfo.reqGlEsVersion >= 0x20000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        window.addFlags(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        log("${checkOpenGLVersion()}")
        log("${activityManager.deviceConfigurationInfo.glEsVersion}")
        initView()
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }

    private fun initView() {
        glView.apply {
            setEGLContextClientVersion(2)
            setRenderer(AirSmoothShaderRenderer(this@MainActivity))
            renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
        }
    }
}
