package com.xiao.wanandroidself.utils

import android.content.Context
import android.util.DisplayMetrics
import org.jetbrains.anko.displayMetrics

object DisplayManager {

    private var displayMetrics: DisplayMetrics? = null

    private var screenWidth: Int? = null

    private var screenHeight: Int? = null

    private var screenDpi: Int? = null

    fun init(context: Context) {
        displayMetrics = context.displayMetrics
        screenWidth = displayMetrics?.widthPixels
        screenHeight = displayMetrics?.heightPixels
        screenDpi = displayMetrics?.densityDpi
    }

    private val STANDARD_WIDTH = 1920
    private val STANDARD_HEIGHT = 1080

    fun getScreenWidth(): Int? {
        return screenWidth
    }

    fun getScreenHeight(): Int? {
        return screenHeight
    }

    fun getRealWidth(px: Int, parentWidth: Float): Int {
        return (px / parentWidth * getScreenWidth()!!).toInt()
    }

    fun getRealHeight(px: Int): Int {
        //ui图的宽度
        return getRealHeight(px, STANDARD_HEIGHT.toFloat())
    }

    fun getRealHeight(px: Int, parentHeight: Float): Int {
        return (px / parentHeight * getScreenHeight()!!).toInt()
    }

    fun dip2px(dipValue: Float): Int {
        val scale = displayMetrics?.density
        return (dipValue * scale!! + 0.5f).toInt()
    }
}