package com.xiao.wanandroidself.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.cxz.wanandroid.base.IModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


/**
 * User: ShaudXiao
 * Date: 2020-03-11
 * Time: 10:56
 * Company: zx
 * Description:
 * FIXME
 */

abstract class BaseModel : IModel, LifecycleObserver {
    private var mCompositeDisposable: CompositeDisposable? = null

    override fun addDisposable(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable?.add(it) }
    }

    override fun onDetach() {
        unDipose()
    }

    private fun unDipose() {
        mCompositeDisposable?.clear()
        mCompositeDisposable = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }
}