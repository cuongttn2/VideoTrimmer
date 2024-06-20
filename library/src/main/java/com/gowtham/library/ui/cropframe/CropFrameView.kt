package com.gowtham.library.ui.cropframe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.gowtham.library.R
import kotlin.math.min

class CropFrameView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var playerViewLeft = 0
    private var playerViewTop = 0
    private var playerViewRight = 0
    private var playerViewBottom = 0

    private var frameX = 0f
    private var frameY = 0f
    private var frameWidth = 0f
    private var frameHeight = 0f
    private var aspectRatio = AspectRatioEnum.ORIGIN.ordinal

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val gestureDetector = GestureDetector(context, GestureListener(this))

    init {
        if (attrs != null) {
            val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CropFrameView)
            aspectRatio = styledAttrs.getInt(
                R.styleable.CropFrameView_aspect_ratio,
                AspectRatioEnum.ORIGIN.ordinal
            )
            styledAttrs.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(frameX, frameY, frameX + frameWidth, frameY + frameHeight, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    fun updateFramePosition(newX: Float, newY: Float) {
        frameX = newX.coerceIn(playerViewLeft.toFloat(), playerViewRight.toFloat() - frameWidth)
        frameY = newY.coerceIn(playerViewTop.toFloat(), playerViewBottom.toFloat() - frameHeight)
        invalidate()
    }

    fun setPlayerViewBounds(left: Int, top: Int, right: Int, bottom: Int) {
        playerViewLeft = left
        playerViewTop = top
        playerViewRight = right
        playerViewBottom = bottom
        updateFrameSize()
    }

    private fun updateFrameSize() {
        val width = playerViewRight - playerViewLeft
        val height = playerViewBottom - playerViewTop

        when (aspectRatio) {
            AspectRatioEnum.AR_1_1.ordinal -> {
                val size = min(width, height)
                frameWidth = size.toFloat()
                frameHeight = size.toFloat()
            }

            AspectRatioEnum.AR_4_5.ordinal -> {
                frameWidth = min(width.toFloat(), height * 4f / 5f)
                frameHeight = frameWidth * 5 / 4
                if (frameHeight > height) {
                    frameHeight = height.toFloat()
                    frameWidth = frameHeight * 4 / 5
                }
            }

            AspectRatioEnum.AR_16_9.ordinal -> {
                frameWidth = width.toFloat()
                frameHeight = frameWidth * 9 / 16
                if (frameHeight > height) {
                    frameHeight = height.toFloat()
                    frameWidth = frameHeight * 16 / 9
                }
            }

            AspectRatioEnum.AR_9_16.ordinal -> {
                frameHeight = height.toFloat()
                frameWidth = frameHeight * 9 / 16
                if (frameWidth > width) {
                    frameWidth = width.toFloat()
                    frameHeight = frameWidth * 16 / 9
                }
            }
        }
        // Recalculate frame position
        frameX = playerViewLeft + (width - frameWidth) / 2
        frameY = playerViewTop + (height - frameHeight) / 2
        invalidate()
    }

    fun changeAspectRatio(newRatio: AspectRatioEnum) {
        aspectRatio = newRatio.ordinal
        updateFrameSize()  // Make sure this method updates the frame and calls invalidate()
        invalidate()
    }

    fun getFrameX(): Float = frameX
    fun getFrameY(): Float = frameY
    fun getFrameWidth(): Float = frameWidth
    fun getFrameHeight(): Float = frameHeight

    fun getDistancesToPlayerView(): Rect {
        val playerViewRect = Rect(playerViewLeft, playerViewTop, playerViewRight, playerViewBottom)
        val cropFrameRect = Rect(
            frameX.toInt(),
            frameY.toInt(),
            (frameX + frameWidth).toInt(),
            (frameY + frameHeight).toInt()
        )
        Log.d(
            "DISTANCES",
            "frameWidth: $frameWidth\n" +
                    "frameHeight: $frameHeight"
        )
        return Rect(
            cropFrameRect.left - playerViewRect.left, // Left
            cropFrameRect.top - playerViewRect.top,   // Top
            playerViewRect.right - cropFrameRect.right, // Right
            playerViewRect.bottom - cropFrameRect.bottom // Bottom
        )
    }
}