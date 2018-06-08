package com.jeppeman.jetpackplayground.video.platform.di

import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.video.data.di.NetModule
import com.jeppeman.jetpackplayground.video.data.di.RepositoryModule
import com.jeppeman.jetpackplayground.video.platform.VideoFeatureImpl
import com.jeppeman.jetpackplayground.video.presentation.VideoFragmentComponent
import com.jeppeman.jetpackplayground.video.test.TestActivity
import dagger.BindsInstance
import dagger.Component

@VideoScope
@Component(
        modules = [
            VideoModule::class,
            NetModule::class,
            RepositoryModule::class
        ],
        dependencies = [VideoFeature.Dependencies::class]
)
interface VideoComponent {
    val videoFragmentComponentFactory: VideoFragmentComponent.Factory

    fun inject(videoFeatureImpl: VideoFeatureImpl)

    fun inject(testActivity: TestActivity)

    @Component.Factory
    interface Factory {
        fun create(
                dependencies: VideoFeature.Dependencies,
                @BindsInstance videoFeatureImpl: VideoFeatureImpl
        ): VideoComponent
    }
}