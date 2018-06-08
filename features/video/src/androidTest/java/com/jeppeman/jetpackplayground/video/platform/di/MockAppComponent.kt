package com.jeppeman.jetpackplayground.video.platform.di

import com.jeppeman.jetpackplayground.di.AppComponent
import com.jeppeman.jetpackplayground.video.MockApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [MockAppModule::class, AndroidInjectionModule::class])
interface MockAppComponent : AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance mockApplication: MockApplication): MockAppComponent
    }
}