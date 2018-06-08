package com.jeppeman.jetpackplayground.video.presentation

import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ChildFragmentScope
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.detail.VideoDetailFragment
import com.jeppeman.jetpackplayground.video.presentation.detail.VideoDetailModule
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListFragment
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListModule
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

@Module
object VideoFragmentModule {
    @JvmStatic
    @Provides
    @FragmentScope
    fun provideVideoViewModel(
            videoFragment: VideoFragment
    ): VideoViewModel =
            ViewModelProviders.of(videoFragment)[VideoViewModel::class.java]

    @JvmStatic
    @Provides
    fun provideAppUiContainer(videoFragment: VideoFragment): AppUiContainer =
            videoFragment.activity as AppUiContainer

    @JvmStatic
    @Provides
    @FragmentScope
    fun provideVideoFragmentParameter(videoFragment: VideoFragment): VideoParameter =
            VideoParameter(videoFragment.resources.getBoolean(R.bool.landscape))

    @Module
    interface Contributor {
        @ChildFragmentScope
        @ContributesAndroidInjector(modules = [VideoListModule::class])
        fun contributeVideoListFragment(): VideoListFragment

        @ChildFragmentScope
        @ContributesAndroidInjector(modules = [VideoDetailModule::class])
        fun contributeVideoDetailFragment(): VideoDetailFragment
    }
}