package com.xiao.wanandroidself.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.xiao.wanandroidself.R
import java.util.jar.Attributes

class CircleImageView(context: Context, attributes: AttributeSet) :
    AppCompatImageView(context, attributes) {

    private val mType: Int
    private val mBorderColor: Int
    private val mBorderWidth: Int
    private val mRectBorderRadius: Int

    private val mPaintBitmap = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintBorder = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mRectBorder = RectF()
    private val mRectBitmap = RectF()

    private var mRawBitmap: Bitmap? = null
    private var mShader: BitmapShader? = null
    private val mMatrix = Matrix()


    init {
        val ta = context.obtainStyledAttributes(attributes, R.styleable.CircleImageView)
        mType = ta.getInt(R.styleable.CircleImageView_type, DEFAULT_TYPE)
        mBorderColor = ta.getColor(R.styleable.CircleImageView_borderColor, DEFAULT_BORDER_COLOR)
        mBorderWidth =
            ta.getDimensionPixelSize(R.styleable.CircleImageView_borderWidth, DEFAULT_BORDER_WIDTH)
        mRectBorderRadius = ta.getDimensionPixelSize(
            R.styleable.CircleImageView_rectRoundRadius,
            DEFAULT_RECT_ROUDN_RADIUS
        )
        ta.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        val rawBitmap = getBitmap(drawable)
        if (rawBitmap != null && mType != TYPE_NONE) {
            val viewWidth = width
            val viewHeight = height
            val viewMinSize = Math.min(viewWidth, viewHeight)
            val dstWidth = (if (mType == TYPE_CIRCLE) viewMinSize else viewWidth).toFloat()
            val dstHeight = (if (mType == TYPE_CIRCLE) viewMinSize else viewHeight).toFloat()

            val harBorderWidth = mBorderWidth / 2.0f
            val doubleBorderWidth = (mBorderWidth * 2).toFloat()

            if (mShader == null && rawBitmap != mRawBitmap) {
                mRawBitmap = rawBitmap
                mShader = BitmapShader(mRawBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }

            if (mShader != null) {
                mMatrix.setScale(
                    (dstWidth - doubleBorderWidth) / rawBitmap.width,
                    (dstHeight - doubleBorderWidth) / rawBitmap.height
                )
                mShader!!.setLocalMatrix(mMatrix)
            }
            mPaintBitmap.setShader(mShader)
            mPaintBitmap.style = Paint.Style.STROKE
            mPaintBitmap.strokeWidth = mBorderWidth.toFloat()
            mPaintBitmap.color = if (mBorderWidth > 0) mBorderColor else Color.TRANSPARENT

            if (mType == TYPE_CIRCLE) {
                val radius = viewMinSize / 2.0f;
                canvas?.drawCircle(radius, radius, radius - harBorderWidth, mPaintBorder)
                canvas?.translate(mBorderWidth.toFloat(), mBorderWidth.toFloat())
                canvas?.drawCircle(
                    radius - mBorderWidth,
                    radius - mBorderWidth,
                    radius - mBorderWidth,
                    mPaintBitmap
                )
            } else if (mType == TYPE_ROUND_RECT) {
                mRectBorder.set(
                    harBorderWidth,
                    harBorderWidth,
                    dstWidth - mBorderWidth,
                    dstHeight - mBorderWidth
                )
                mRectBitmap.set(
                    0.0f,
                    .0f,
                    dstWidth - doubleBorderWidth,
                    dstHeight - doubleBorderWidth
                )
                val borderRadius =
                    if (mRectBorderRadius - harBorderWidth > 0.0f) mRectBorderRadius - harBorderWidth else 0.0f
                val bitmapRadius =
                    ( if (mRectBorderRadius - mBorderWidth > 0.0f) mRectBorderRadius - mBorderWidth else 0.0f) as Float
                canvas?.drawRoundRect(mRectBorder, borderRadius, borderRadius, mPaintBorder)
                canvas?.translate(mBorderWidth.toFloat(), mBorderWidth.toFloat())
                canvas?.drawRoundRect(mRectBitmap, bitmapRadius, bitmapRadius, mPaintBitmap)

            }

        } else {
            super.onDraw(canvas)
        }
    }

    private fun dip2px(dipVal: Int): Int {
        val scale = resources.displayMetrics.density
        return (dipVal * scale + 0.5f).toInt()
    }

    private fun getBitmap(drawable: Drawable): Bitmap? {
        if (drawable == null) {
            return null
        }

        return when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is ColorDrawable -> {
                val rect = drawable.bounds
                val width = rect.right - rect.left
                val height = rect.bottom - rect.top
                val color = drawable.color
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                canvas.drawARGB(
                    Color.alpha(color),
                    Color.red(color),
                    Color.green(color),
                    Color.blue(color)
                )
                bitmap
            }
            else -> null
        }
    }

    companion object {
        val TYPE_NONE = 0

        val TYPE_CIRCLE = 1

        val TYPE_ROUND_RECT = 2

        private val DEFAULT_TYPE = TYPE_NONE
        private val DEFAULT_BORDER_COLOR = Color.TRANSPARENT
        private val DEFAULT_BORDER_WIDTH = 0
        private val DEFAULT_RECT_ROUDN_RADIUS = 0

    }
}