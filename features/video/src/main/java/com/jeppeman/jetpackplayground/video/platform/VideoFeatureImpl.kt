package com.jeppeman.jetpackplayground.video.platform

import androidx.fragment.app.Fragment
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.video.platform.di.DaggerVideoComponent
import com.jeppeman.jetpackplayground.video.platform.di.VideoComponent
import com.jeppeman.jetpackplayground.video.presentation.createVideoFragment

internal lateinit var videoComponent: VideoComponent
    private set

class VideoFeatureImpl : VideoFeature {
    override fun getEntryPoint(): Fragment = createVideoFragment()

    override fun inject(dependencies: VideoFeature.Dependencies) {
        videoComponent = DaggerVideoComponent.factory()
                .create(dependencies, this)
    }
}