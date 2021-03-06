package com.hour.hour.helper

import android.view.MotionEvent
import android.view.View

@Suppress("unused")
object Touchable {
    fun make(view: View) {
        view.setOnTouchListener { v, ev ->
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (v.isEnabled) {
                        v.animate().cancel()
                        v.animate().alpha(0.2f).duration = 50
                        return@setOnTouchListener true
                    }
                }
                MotionEvent.ACTION_CANCEL -> {
                    if (v.isEnabled) {
                        v.animate().cancel()
                        v.animate().alpha(1.0f).duration = 50
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (v.isEnabled) {
                        v.animate().cancel()
                        v.animate().alpha(1.0f).duration = 50
                        v.performClick()
                    }
                }
            }
            false
        }
    }
}
