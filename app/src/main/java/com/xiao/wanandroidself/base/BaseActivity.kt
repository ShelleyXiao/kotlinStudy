package com.xiao.wanandroidself.base

import android.content.Context
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.afollestad.materialdialogs.color.CircleView
import com.cxz.multiplestatusview.MultipleStatusView
import com.xiao.wanandroidself.App
import com.xiao.wanandroidself.Constant.Constant
import com.xiao.wanandroidself.R
import com.xiao.wanandroidself.receiver.NetworkChangeReceiver
import com.xiao.wanandroidself.utils.CommonUtil
import com.xiao.wanandroidself.utils.KeyboardUtil
import com.xiao.wanandroidself.utils.Preference
import com.xiao.wanandroidself.utils.SettingUtil

abstract class BaseActivity : AppCompatActivity() {

    protected var isLogin: Boolean by Preference(Constant.LOGIN_KEY, false)

    protected var hasNetwork: Boolean by Preference(Constant.HAS_NETWORK_KEY, false)

    protected var mNetworkChangeReceiver: NetworkChangeReceiver? = null


    protected var mThemeColor: Int = SettingUtil.getColor()

    protected var mLayoutStatusView: MultipleStatusView? = null

    protected lateinit var mTipView: View
    protected lateinit var mWindowManager: WindowManager
    protected lateinit var mLayoutParmss: WindowManager.LayoutParams

    protected abstract fun attachLayoutRes(): Int

    protected abstract fun initData()

    protected abstract fun initView()

    protected abstract fun start()

    open fun useEventBus(): Boolean = true

    open fun enableNetworkTip(): Boolean = true

    open fun doReconnected() {
        start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutRes())
        if (useEventBus()) {
            //todo
        }

        initData()
        initTipView()
        initView()
        start()
        initListener()
    }

    override fun onResume() {
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        mNetworkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(mNetworkChangeReceiver, filter)

        super.onResume()

        initColor()

    }

    protected fun initTipView() {
        mTipView = layoutInflater.inflate(R.layout.layout_network_tip, null)
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mLayoutParmss = WindowManager.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        mLayoutParmss.gravity = Gravity.TOP
        mLayoutParmss.x = 0
        mLayoutParmss.y = 0
        mLayoutParmss.windowAnimations = R.style.anim_style_view
    }

    open fun initColor() {
        mThemeColor = if(!SettingUtil.getIsNightMode()) {
            SettingUtil.getColor()
        } else {
            resources.getColor(R.color.colorPrimary)
        }

        if(this.supportActionBar != null) {
            this.supportActionBar?.setBackgroundDrawable(ColorDrawable(mThemeColor))
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(SettingUtil.getNavBar()) {
                window.navigationBarColor = CircleView.shiftColorDown(mThemeColor)
            } else {
                window.navigationBarColor = Color.BLACK
            }
        }

    }

    protected fun initToolbar(toolbar: Toolbar, homeAsUpEnabled: Boolean, title: String) {
        toolbar?.setTitle(title)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(homeAsUpEnabled)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev?.action == MotionEvent.ACTION_UP) {
            val focusView = currentFocus
            if(KeyboardUtil.isHideKeyboard(focusView, ev)) {
                KeyboardUtil.hideKeyBoard(this, focusView)
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home-> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val count = supportFragmentManager.backStackEntryCount
        if(count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onPause() {
        if(mNetworkChangeReceiver != null) {
            unregisterReceiver(mNetworkChangeReceiver)
            mNetworkChangeReceiver = null
        }
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        CommonUtil.fixInputMethodManagerLeak(this)
        App.getReWater(this)?.watch(this)
    }

    override fun finish() {
        super.finish()
        if(mTipView != null && mTipView.parent != null) {
            mWindowManager.removeView(mTipView)
        }
    }

    private fun initListener() {
        mLayoutStatusView?.setOnRetryClickListener(mRetryClickListener)
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        start()
    }
}