package com.xiao.wanandroidself.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import java.lang.reflect.Field

object CommonUtil {

    fun  fixInputMethodManagerLeak(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val arr = arrayOf("mCurRootView", "mServedView", "mNextServedView")
        var field: Field? = null
        var objGet: Any? = null
        for(i in arr.indices) {
            try {
                field = imm.javaClass.getDeclaredField(arr[i])
                if(field.isAccessible === false) {
                    field.isAccessible = true
                }
                objGet = field.get(imm)
                if(objGet != null && objGet is View) {
                    val view = objGet
                    if(view.context == context) {
                        field.set(imm, null)
                    } else {
                        break
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }

        }
    }

}