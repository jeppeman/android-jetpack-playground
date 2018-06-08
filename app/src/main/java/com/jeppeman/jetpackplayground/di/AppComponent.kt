package com.jeppeman.jetpackplayground.di

import android.content.Context
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.MainApplication
import com.jeppeman.jetpackplayground.common.data.DataModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    DataModule::class,
    AndroidInjectionModule::class,
    ActivityContributor::class
])
interface AppComponent: HomeFeature.Dependencies, VideoFeature.Dependencies {
    override val context: Context

    fun inject(application: MainApplication)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance mainApplication: MainApplication): AppComponent
    }
}