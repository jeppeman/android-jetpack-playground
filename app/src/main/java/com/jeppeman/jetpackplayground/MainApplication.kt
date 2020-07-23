package com.jeppeman.jetpackplayground

import com.jeppeman.globallydynamic.globalsplitcompat.GlobalSplitCompatApplication
import com.jeppeman.jetpackplayground.di.AppComponent
import com.jeppeman.jetpackplayground.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

lateinit var appComponent: AppComponent

open class MainApplication : GlobalSplitCompatApplication(), HasAndroidInjector {
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