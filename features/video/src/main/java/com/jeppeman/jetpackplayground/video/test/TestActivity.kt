package com.jeppeman.jetpackplayground.video.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jeppeman.jetpackplayground.appComponent
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.platform.VideoFeatureImpl
import com.jeppeman.jetpackplayground.video.presentation.VideoFragment

class TestActivity : AppCompatActivity(), AppUiContainer {
    override fun enterFullscreen() {

    }

    override fun exitFullscreen() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        VideoFeatureImpl().inject(appComponent)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, VideoFragment())
                    .commit()
        }
    }
}