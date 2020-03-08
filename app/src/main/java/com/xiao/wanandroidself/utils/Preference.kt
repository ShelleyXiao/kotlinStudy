package com.xiao.wanandroidself.utils

import android.content.Context
import android.content.SharedPreferences
import com.xiao.wanandroidself.App
import java.io.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preference<T>(val name: String, private val default: T) {

    companion object {
        private val file_name = "wan_android_file"

        private val perfes: SharedPreferences by lazy {
            App.context.getSharedPreferences(file_name, Context.MODE_PRIVATE)
        }

        fun clearPreference() {
            perfes.edit().clear().apply()
        }

        fun clearPreference(key: String) {
            perfes.edit().remove(key).apply()
        }

        fun contains(key: String): Boolean {
            return perfes.contains(key)
        }

        fun getAll(): Map<String, *> {
            return perfes.all
        }
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putSharePreferecnes(name, value)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return getSharePreferecne(name, default)
    }

    private fun putSharePreferecnes(name: String, value: T) = with(perfes.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> {
                putString(name, serailize(value))
            }
        }.apply()
    }

    private fun getSharePreferecne(name: String, default: T): T = with(perfes) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> {
                deSerailize(getString(name, serailize(default)))
            }
        }
        return res as T
    }

    @Throws(IOException::class)
    private fun <A> serailize(obj: A): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val objectOutputStream = ObjectOutputStream(byteArrayOutputStream)
        objectOutputStream.writeObject(obj)
        var serStr = byteArrayOutputStream.toString("ISO-8859-1")
        serStr = java.net.URLEncoder.encode(serStr, "UTF-8")
        objectOutputStream.close()
        byteArrayOutputStream.close()
        return serStr

    }

    private fun <A> deSerailize(str: String): A {
        val redStr = java.net.URLDecoder.decode(str, "UTF-8")
        val byteArrayInputStream = ByteArrayInputStream(redStr.toByteArray(charset("ISO-8859-1")))
        val objInputStream = ObjectInputStream(byteArrayInputStream)
        val obj = objInputStream.readObject() as A
        objInputStream.close()
        byteArrayInputStream.close()
        return obj
    }
}