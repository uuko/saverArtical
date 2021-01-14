package com.example.saverartical

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button

class FloatingButtonService : Service() {
    var isStart = false
    var show = false
    lateinit var windowsManager:WindowManager
    lateinit var layoutParams :WindowManager.LayoutParams
    lateinit var button: Button


    fun getStart():Boolean{
        return isStart;
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()
        isStart=true
        windowsManager= getSystemService(Context.WINDOW_SERVICE) as WindowManager;
        layoutParams=WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = 500
        layoutParams.height = 100
        layoutParams.x = 300
        layoutParams.y = 300

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showFloating()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showFloating() {
        if (Settings.canDrawOverlays(this)) {
            button = Button(applicationContext)
            button.text = "Floating Window"
            button.setBackgroundColor(Color.BLUE)
            windowsManager.addView(button, layoutParams)

            button.setOnTouchListener(FloatingOnTouchListener())
            button.setOnClickListener {
            val intent = Intent(this@FloatingButtonService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            }
        }
    }

    private inner class FloatingOnTouchListener : View.OnTouchListener {
        private var x: Int = 0
        private var y: Int = 0

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    x = event.rawX.toInt()
                    y = event.rawY.toInt()
                }
                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - x
                    val movedY = nowY - y
                    x = nowX
                    y = nowY
                    layoutParams.x = layoutParams.x + movedX
                    layoutParams.y = layoutParams.y + movedY
                    windowsManager.updateViewLayout(view, layoutParams)
                }
                else -> {
                }
            }
            return false
        }
    }
}