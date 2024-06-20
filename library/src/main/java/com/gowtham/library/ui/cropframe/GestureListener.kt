package com.gowtham.library.ui.cropframe

import android.view.GestureDetector
import android.view.MotionEvent
import com.gowtham.library.ui.cropframe.CropFrameView

class GestureListener(private val cropFrameView: CropFrameView) : GestureDetector.SimpleOnGestureListener() {

    private var lastTouchX = 0f
    private var lastTouchY = 0f

    override fun onDown(e: MotionEvent): Boolean {
        lastTouchX = e.rawX
        lastTouchY = e.rawY
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val newX = cropFrameView.getFrameX() + (e2.rawX - lastTouchX)
        val newY = cropFrameView.getFrameY() + (e2.rawY - lastTouchY)

        cropFrameView.updateFramePosition(newX, newY)

        lastTouchX = e2.rawX
        lastTouchY = e2.rawY
        return true
    }
}