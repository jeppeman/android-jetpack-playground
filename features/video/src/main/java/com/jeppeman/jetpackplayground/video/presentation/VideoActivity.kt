package com.jeppeman.jetpackplayground.video.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.jeppeman.jetpackplayground.MainApplication
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.BaseActivity
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.common_features.getFeature
import com.jeppeman.jetpackplayground.video_resources.R

class VideoActivity : BaseActivity(), AppUiContainer {
    private val appComponent get() = (application as MainApplication).appComponent
    private val featureManager: FeatureManager by lazy { appComponent.featureManager }

    override fun enterFullscreen() {
        window?.decorView?.systemUiVisibility =
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    override fun exitFullscreen() {
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        if (savedInstanceState == null) {
            val feature = featureManager.getFeature<VideoFeature, VideoFeature.Dependencies>(
                    dependencies = appComponent.videoFeatureDependencies
            ) ?: throw IllegalStateException("Could not retrieve video feature")

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, feature.getMainScreen())
                    .commit()
        }
    }
}