package com.jeppeman.jetpackplayground.home.platform.di

import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.home.platform.HomeFeatureImpl
import com.jeppeman.jetpackplayground.home.presentation.HomeFragmentComponent
import com.jeppeman.jetpackplayground.home.presentation.download.DownloadComponent
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule

@HomeScope
@Component(
        modules = [HomeModule::class, AndroidInjectionModule::class],
        dependencies = [HomeFeature.Dependencies::class]
)
interface HomeComponent {
    val homeFragmentComponentFactory: HomeFragmentComponent.Factory
    val downloadComponentFactory: DownloadComponent.Factory
    val featureManager: FeatureManager

    fun inject(homeFeature: HomeFeatureImpl)

    @Component.Factory
    interface Factory {
        fun create(
                dependencies: HomeFeature.Dependencies,
                @BindsInstance homeFeatureImpl: HomeFeatureImpl
        ): HomeComponent
    }
}