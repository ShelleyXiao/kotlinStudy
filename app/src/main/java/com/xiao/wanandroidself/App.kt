package com.xiao.wanandroidself

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.squareup.leakcanary.AndroidDebuggerControl
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import com.xiao.wanandroidself.utils.DisplayManager
import com.xiao.wanandroidself.utils.SettingUtil
import org.litepal.LitePal
import kotlin.properties.Delegates

class App : Application() {
    private var refWatcher: RefWatcher ?= null

    companion object {
        val TAG = "WAN_ANDROID"

        var context: Context by Delegates.notNull()
            private set

        lateinit var instance: Application

        fun getReWater(context: Context): RefWatcher? {
            val app = context.applicationContext as App
            return app.refWatcher
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        context = applicationContext
        refWatcher = setLeakCanary()
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        initTheme()
        initLitPal()
    }

    private fun setLeakCanary(): RefWatcher {
        return if(LeakCanary.isInAnalyzerProcess(this)) {
            RefWatcher.DISABLED
        } else {
            LeakCanary.install(this)
        }
    }
    private fun initConfig() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false)
            .methodCount(0)
            .methodOffset(7)
            .tag(TAG)
            .build()
        Logger.addLogAdapter(object :AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return true
            }
        })
    }

    private fun initTheme() {
        if(SettingUtil.getIsAutoNightMode()) {

        } else {

        }
    }

    private fun initLitPal() {
        LitePal.initialize(this)
    }


    private val mActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onActivityResumed(activity: Activity?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onActivityStarted(activity: Activity?) {
            Logger.d("onStart: " + activity?.componentName?.className)
        }

        override fun onActivityDestroyed(activity: Activity?) {
            Logger.d("onDestory: " +  activity?.componentName?.className)
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onActivityStopped(activity: Activity?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

}