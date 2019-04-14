package pl.dybuk.posttest.ui.view

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import pl.dybuk.posttest.R

open class CircleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var image: Drawable? = null
        private set
    private var mCircleColor: Int = 0

    init {
        initialize(attrs)
    }

    private fun initialize(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.circle_view)
        val d = typedArray.getDrawable(R.styleable.circle_view_circleColor)
        if (d != null) {
            if (d is ColorDrawable) {
                mCircleColor = d.color
            }
            setImageDrawable(d)
        }
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.color = mCircleColor
        if (image != null) {
            canvas.save()
            image!!.setBounds(
                width / 2 - image!!.minimumWidth / 2,
                height / 2 - image!!.minimumHeight / 2,
                width / 2 + image!!.minimumWidth / 2,
                height / 2 + image!!.minimumHeight / 2
            )
            image!!.draw(canvas)
            canvas.restore()
        } else {
            val cx = width / 2f
            val cy = height / 2f
            val radius = Math.min(cx, cy)
            canvas.drawCircle(cx, cy, radius, mPaint)
        }
    }

    fun setColorResource(@ColorRes colorRes: Int) {
        val color = resources.getColor(colorRes)
        setColor(color)
    }

    fun setColor(@ColorInt color: Int) {
        mCircleColor = color
        invalidate()
    }

    fun setImageDrawable(drawable: Drawable?) {
        if (image !== drawable) {
            if (drawable is ColorDrawable || drawable == null) {
                image = null
                if (drawable != null) {
                    mCircleColor = (drawable as ColorDrawable).color
                }
            } else if (drawable is BitmapDrawable) {
                val rounded = RoundedBitmapDrawableFactory.create(resources, drawable.bitmap)
                rounded.isCircular = true
                image = rounded
            } else if (drawable is RoundedBitmapDrawable) {
                val rounded = drawable as RoundedBitmapDrawable?
                if (!rounded!!.isCircular) {
                    rounded.isCircular = true
                }
                image = rounded
            } else if (drawable is Drawable) {
                image = drawable
            }
            invalidate()
        }
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        var drawable: RoundedBitmapDrawable? = null
        if (bitmap != null) {
            drawable = RoundedBitmapDrawableFactory.create(resources, bitmap)
            drawable.isCircular = true
        }
        setImageDrawable(drawable)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = CustomOutline(w, h)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class CustomOutline internal constructor(internal var width: Int, internal var height: Int) :
        ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            outline.setOval(0, 0, width, height)
        }
    }
}
