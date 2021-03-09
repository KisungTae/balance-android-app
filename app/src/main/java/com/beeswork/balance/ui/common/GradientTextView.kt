package com.beeswork.balance.ui.common

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.beeswork.balance.R


class GradientTextView : AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    var startColor: Int = 0
    var endColor: Int = 0

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val styledAttrs = context.obtainStyledAttributes(it, R.styleable.GradientTextView, 0, 0)
            startColor = styledAttrs.getInt(R.styleable.GradientTextView_startColor, 0)
            endColor = styledAttrs.getInt(R.styleable.GradientTextView_endColor, 0)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(),
                startColor,
                endColor,
                Shader.TileMode.CLAMP
            )
        }
    }
}