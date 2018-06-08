package com.jeppeman.jetpackplayground.common.presentation

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

interface LifecycleAwareCoroutineViewModel : LifecycleObserver, CoroutineScope

abstract class BaseViewModel : ViewModel(), LifecycleAwareCoroutineViewModel {
    private var isInitialized = false
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    protected open fun onInitialize() {
    }

    @CallSuper
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    protected open fun onCreate() {
        if (!isInitialized) {
            isInitialized = true
            onInitialize()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    protected open fun onStart() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    protected open fun onResume() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    protected open fun onDestroy() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    protected open fun onStop() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    protected open fun onPause() {
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        cancel()
    }
}
