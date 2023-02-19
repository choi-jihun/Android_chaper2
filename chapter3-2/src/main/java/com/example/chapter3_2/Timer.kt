package com.example.chapter3_2

import android.os.Looper
import android.os.Handler
import java.time.Duration

class Timer(listener: OnTimerTickListener) {
    private var duration : Long = 0L
    private val handler = Handler(Looper.getMainLooper())
    private val runnable : Runnable = object : Runnable{
        override fun run() {
            duration += 10L
            handler.postDelayed(this,10L)
            listener.onTick(duration)
        }

    }

    fun start(){
        handler.postDelayed(runnable,10L)
    }

    fun stop(){
        handler.removeCallbacks(runnable)
    }

}

interface OnTimerTickListener{
    fun onTick(duration: Long)
}