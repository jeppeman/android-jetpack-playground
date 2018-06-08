package com.jeppeman.jetpackplayground

import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.jeppeman.jetpackplayground.di.AppComponent
import com.jeppeman.jetpackplayground.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

lateinit var appComponent: AppComponent

open class MainApplication : SplitCompatApplication(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector() = dispatchingActivityInjector

    open fun inject() {
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        inject()
    }
}