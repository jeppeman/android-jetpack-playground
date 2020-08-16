package com.jeppeman.jetpackplayground.video.presentation

import android.os.Bundle
import android.util.Log
import com.jeppeman.jetpackplayground.common.presentation.BaseFragment
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.video.platform.VideoFeatureImpl
import com.jeppeman.jetpackplayground.video_resources.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

fun createVideoFragment(): VideoFragment = VideoFragment()

class VideoFragment : BaseFragment<VideoViewModel>(), HasAndroidInjector {
    override val layoutRes = R.layout.fragment_video

    @Inject
    override lateinit var viewModel: VideoViewModel

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun inject() {
        ((context?.applicationContext as? VideoFeature.InjectionProvider)?.videoFeature as? VideoFeatureImpl)
                ?.videoComponent
                ?.videoFragmentComponentFactory
                ?.create(this)
                ?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager?.beginTransaction()
                ?.setPrimaryNavigationFragment(this)
                ?.commit()
    }
}