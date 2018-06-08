package com.jeppeman.jetpackplayground.video.test

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jeppeman.jetpackplayground.appComponent
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.platform.VideoFeatureImpl
import com.jeppeman.jetpackplayground.video.presentation.VideoFragment
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient

class TestActivity : AppCompatActivity(), AppUiContainer {
    override fun enterFullscreen() {

    }

    override fun exitFullscreen() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        VideoFeatureImpl().inject(object : VideoFeature.Dependencies {
            override val okHttpClient: OkHttpClient = appComponent.okHttpClient
            override val context: Context = appComponent.context
            override val handler: Handler = appComponent.handler
            override val backgroundDispatcher: CoroutineDispatcher = appComponent.backgroundDispatcher
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainer, VideoFragment())
                    .commit()
        }
    }
}