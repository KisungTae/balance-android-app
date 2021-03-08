package com.beeswork.balance.ui.common

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF

class SwipeButton(
    private val context: Context,
    private val text: String,
    private val textSize: Int,
    private val textColor: Int,
    private val backgroundColor: Int,
    private val swipeButtonClickListener: SwipeButtonClickListener
) {
    private var pos: Int = 0
    private var clickRegion: RectF? = null
    private val resources: Resources = context.resources

    fun onClick(x: Float, y: Float): Boolean {
        clickRegion?.let {
            if (it.contains(x, y)) swipeButtonClickListener.onClick(pos)
            return true
        } ?: return false
    }

    fun onDraw(canvas: Canvas, rectF: RectF, pos: Int) {
        val paint = Paint()
        paint.color = backgroundColor
        canvas.drawRect(rectF, paint)

        paint.color = textColor
        paint.textSize = textSize.toFloat()

        val rect = Rect()
        val width = rectF.width()
        val height = rectF.height()

        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, rect)

        val x = width / 2f - rect.width() / 2f - rect.left.toFloat()
        val y = height / 2f + rect.height() / 2f - rect.bottom.toFloat()

        canvas.drawText(text, rectF.left + x, rectF.top + y, paint)

        clickRegion = rectF
        this.pos = pos
    }
}