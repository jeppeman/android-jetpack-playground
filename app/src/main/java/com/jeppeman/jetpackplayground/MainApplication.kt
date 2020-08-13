package com.jeppeman.jetpackplayground

import com.jeppeman.globallydynamic.globalsplitcompat.GlobalSplitCompatApplication
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.common_features.getFeature
import com.jeppeman.jetpackplayground.di.AppComponent
import com.jeppeman.jetpackplayground.di.DaggerAppComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


open class MainApplication : GlobalSplitCompatApplication(),
        HasAndroidInjector,
        HomeFeature.InjectionProvider,
        VideoFeature.InjectionProvider {
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Any>
    lateinit var appComponent: AppComponent

    override fun androidInjector() = dispatchingActivityInjector

    open fun inject() {
        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)
    }

    override fun onCreate() {
        super.onCreate()
        inject()
    }

    override val homeFeature: HomeFeature get() = appComponent.featureManager
            .getFeature(appComponent.homeFeatureDependencies)!!

    override val videoFeature: VideoFeature get() = appComponent.featureManager
            .getFeature(appComponent.videoFeatureDependencies)!!
}