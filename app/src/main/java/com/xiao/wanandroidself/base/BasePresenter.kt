package com.xiao.wanandroidself.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.cxz.wanandroid.base.IModel
import com.cxz.wanandroid.base.IPresenter
import com.cxz.wanandroid.base.IView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus


/**
 * User: ShaudXiao
 * Date: 2020-03-11
 * Time: 11:07
 * Company: zx
 * Description:
 * FIXME
 */

abstract class BasePresenter<M : IModel, V : IView> : IPresenter<V>, LifecycleObserver {
    private var mModel: M? = null
    private var mView: V? = null

    private val isViewAttached: Boolean
        get() = mView != null

    private var mCompositeDisposable: CompositeDisposable? = null

    open fun createModel(): M? = null

    open fun useEventBus(): Boolean = false

    override fun attachView(mView: V) {
        this.mView = mView
        mModel = createModel()
        if (mView is LifecycleOwner) {
            (mView as LifecycleOwner).lifecycle.addObserver(this)
            if (mModel != null && mModel is LifecycleObserver) {
                (mView as LifecycleOwner).lifecycle.addObserver(mModel as LifecycleObserver)
            }

            if (useEventBus()) {
                EventBus.getDefault().register(this)
            }
        }
    }

    override fun detachView() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }

        unDipose()
        mModel?.onDetach()
        this.mModel = null
        this.mView = null
        this.mCompositeDisposable = null
    }

    open fun checkViewAttacted() {
        if (!isViewAttached) {
            throw MvpViewNotAttachedException()
        }
    }

    open fun addSubscrpition(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable?.add(it) }
    }

    open fun addDisposable(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        disposable?.let { mCompositeDisposable?.add(it) }
    }

    private fun unDipose() {
        mCompositeDisposable?.clear()
        mCompositeDisposable = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }

    private class MvpViewNotAttachedException internal constructor() :
        RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")
}