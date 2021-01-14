package com.example.saverartical

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.Button

import android.view.LayoutInflater
import kotlinx.android.synthetic.main.layout_floating_window.view.*


class FloatingButtonService : Service() {
    var isStart = false
    var show = false
    lateinit var windowsManager:WindowManager
    lateinit var layoutParams :WindowManager.LayoutParams
    lateinit var button: Button
    lateinit var   floatView: View

    fun getStart():Boolean{
        return isStart;
    }

    override fun onBind(p0: Intent?): IBinder? {
        initWindows()
        showFloating()
        return MyBinder()
    }
     class MyBinder : Binder() {

        fun getServces():FloatingButtonService{
            return FloatingButtonService()
        }
    }

    private fun initWindows(){
        isStart=true
        windowsManager= getSystemService(Context.WINDOW_SERVICE) as WindowManager;
        layoutParams=WindowManager.LayoutParams()
        floatView =
            LayoutInflater.from(applicationContext).inflate(R.layout.layout_floating_window, null)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams.format = PixelFormat.RGBA_8888
        layoutParams.gravity = Gravity.LEFT or Gravity.TOP
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.x = 300
        layoutParams.y = 300
//        layoutParams.width = 500
//        layoutParams.height = 100
//        layoutParams.x = 300
//        layoutParams.y = 300
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        return super.onStartCommand(intent, flags, startId)
    }

    private fun showFloating() {
        if (Settings.canDrawOverlays(this)) {
            button = Button(applicationContext)
            button.text = "Floating Window"
            button.setBackgroundColor(Color.BLUE)
            windowsManager.addView(floatView, layoutParams)

            floatView.setOnTouchListener(FloatingOnTouchListener())
            floatView.setOnClickListener {
            val intent = Intent(this@FloatingButtonService, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

            }
            floatView.closeImageButton.setOnClickListener(View.OnClickListener {
                closeWindow()
            })
        }
    }
    fun closeWindow() {
        windowsManager.removeView(button)
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
                    windowsManager.updateViewLayout(floatView, layoutParams)
                }
                else -> {
                }
            }
            return false
        }
    }
}