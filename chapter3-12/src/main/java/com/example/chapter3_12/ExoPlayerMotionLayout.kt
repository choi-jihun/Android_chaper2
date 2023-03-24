package com.example.chapter3_12

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

class ExoPlayerMotionLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : MotionLayout(context, attributeSet, defStyle) {

    var targetView: View? = null
    val gestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return targetView?.containTouchArea(e1.x.toInt(), e1.y.toInt()) ?: false
            }
        })
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            return gestureDetector.onTouchEvent(event)
        } ?: return false
    }


    private fun View.containTouchArea(x: Int, y: Int): Boolean {
        return (x in this.left..this.right && y in this.top..this.bottom)
    }
}