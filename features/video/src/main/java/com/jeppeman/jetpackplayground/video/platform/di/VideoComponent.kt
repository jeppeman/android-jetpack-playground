package com.jeppeman.jetpackplayground.video.platform.di

import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.video.data.di.VideoApiModule
import com.jeppeman.jetpackplayground.video.data.di.VideoRepositoryModule
import com.jeppeman.jetpackplayground.video.platform.VideoFeatureImpl
import com.jeppeman.jetpackplayground.video.presentation.VideoFragmentComponent
import dagger.BindsInstance
import dagger.Component

@VideoScope
@Component(
        modules = [
            VideoModule::class,
            VideoApiModule::class,
            VideoRepositoryModule::class
        ],
        dependencies = [VideoFeature.Dependencies::class]
)
interface VideoComponent {
    val videoFragmentComponentFactory: VideoFragmentComponent.Factory

    fun inject(videoFeatureImpl: VideoFeatureImpl)

    @Component.Factory
    interface Factory {
        fun create(
                dependencies: VideoFeature.Dependencies,
                @BindsInstance videoFeatureImpl: VideoFeatureImpl
        ): VideoComponent
    }
}