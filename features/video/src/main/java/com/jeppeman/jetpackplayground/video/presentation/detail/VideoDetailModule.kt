package com.jeppeman.jetpackplayground.video.presentation.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelKey
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ChildFragmentScope
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object VideoDetailModule {
    @JvmStatic
    @Provides
    @IntoMap
    @ViewModelKey(VideoDetailViewModelImpl::class)
    fun provideVideoDetailViewModelIntoMap(videoDetailViewModelImpl: VideoDetailViewModelImpl): ViewModel =
            videoDetailViewModelImpl

    @JvmStatic
    @Provides
    fun provideExoPlayer(context: Context): SimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(context)

    @JvmStatic
    @Provides
    fun provideMediaSourceFactory(context: Context): ProgressiveMediaSource.Factory {
        val defaultDataSource = DefaultDataSourceFactory(context, "app")
        return ProgressiveMediaSource.Factory(defaultDataSource)
    }

    @JvmStatic
    @Provides
    @ChildFragmentScope
    fun provideVideoDetailViewModel(
            videoDetailFragment: VideoDetailFragment,
            viewModelFactory: ViewModelFactory
    ): VideoDetailViewModel =
            ViewModelProviders.of(videoDetailFragment, viewModelFactory)[VideoDetailViewModelImpl::class.java]

    @JvmStatic
    @Provides
    @ChildFragmentScope
    fun provideVideoDetailParameter(videoDetailFragment: VideoDetailFragment): VideoDetailParameter =
            VideoDetailFragmentArgs.fromBundle(videoDetailFragment.requireArguments()).videoDetailParameter as VideoDetailParameter

    @JvmStatic
    @Provides
    @ChildFragmentScope
    fun provideVideoDetailPlayer(videoDetailPlayerImpl: VideoDetailPlayerImpl): VideoDetailPlayer =
            videoDetailPlayerImpl
}