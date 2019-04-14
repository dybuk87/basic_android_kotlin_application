package pl.dybuk.posttest.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import pl.dybuk.posttest.R

class CircleInitialView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    CircleView(context, attrs) {

    private val mTextPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
    private var mInitial = ""

    init {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.circle_initial_view)

        val resourceId = typedArray.getResourceId(
            R.styleable.circle_initial_view_android_textAppearance,
            android.R.style.TextAppearance_Large_Inverse
        )
        val textView = TextView(context)
        textView.setTextAppearance(context, resourceId)
        mTextPaint.color = textView.textColors.defaultColor
        mTextPaint.textSize = textView.textSize
        mTextPaint.typeface = textView.typeface
        mTextPaint.textAlign = Paint.Align.CENTER

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (image == null || image !is BitmapDrawable) {
            canvas.drawText(
                mInitial, width / 2f,
                height / 2f - (mTextPaint.descent() + mTextPaint.ascent()) / 2, mTextPaint
            )
        }
    }

    fun setInitial(initial: String) {
        if (TextUtils.isEmpty(initial)) {
            mInitial = ""
        } else {
            mInitial = initial[0].toString().toUpperCase()
            mTextPaint.color = ColorGenerator.MATERIAL.getColor(mInitial)
        }
        setImageDrawable(null)
        invalidate()
    }

    fun setInitial(initial: String, size: Float) {
        if (TextUtils.isEmpty(initial)) {
            mInitial = ""
        } else {
            mInitial = initial[0].toString().toUpperCase()
            mTextPaint.color = ColorGenerator.MATERIAL.getColor(mInitial)
            mTextPaint.textSize = size
        }
        setImageDrawable(null)
        invalidate()
    }

    fun setInitial(icon: Int) {

        val wrapDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, icon)!!)
        wrapDrawable.setBounds(0, 0, wrapDrawable.intrinsicWidth, wrapDrawable.intrinsicHeight)
        setInitial("")
        setImageDrawable(wrapDrawable)
        invalidate()
    }
}
