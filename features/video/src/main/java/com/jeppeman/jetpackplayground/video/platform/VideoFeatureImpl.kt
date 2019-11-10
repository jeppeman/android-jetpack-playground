package com.jeppeman.jetpackplayground.video.platform

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.video.platform.di.DaggerVideoComponent
import com.jeppeman.jetpackplayground.video.platform.di.VideoComponent
import com.jeppeman.jetpackplayground.video.presentation.VideoActivity
import com.jeppeman.jetpackplayground.video.presentation.createVideoFragment

internal lateinit var videoComponent: VideoComponent
    private set

class VideoFeatureImpl : VideoFeature {
    override fun getLaunchIntent(context: Context): Intent = Intent(context, VideoActivity::class.java)

    override fun getMainScreen(): Fragment = createVideoFragment()

    override fun inject(dependencies: VideoFeature.Dependencies) {
        if (::videoComponent.isInitialized) {
            return
        }

        videoComponent = DaggerVideoComponent.factory()
                .create(dependencies, this)
    }
}