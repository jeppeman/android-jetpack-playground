package com.jeppeman.jetpackplayground.home.platform.di

import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.home.platform.HomeFeatureImpl
import com.jeppeman.jetpackplayground.home.presentation.HomeFragmentComponent
import dagger.BindsInstance
import dagger.Component

@HomeScope
@Component(
        modules = [HomeModule::class],
        dependencies = [HomeFeature.Dependencies::class]
)
interface HomeComponent {
    val homeFragmentComponentFactory: HomeFragmentComponent.Factory

    fun inject(homeFeature: HomeFeatureImpl)

    @Component.Factory
    interface Factory {
        fun create(
                dependencies: HomeFeature.Dependencies,
                @BindsInstance homeFeatureImpl: HomeFeatureImpl
        ): HomeComponent
    }
}