package com.jeppeman.jetpackplayground.video.presentation.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Fade
import androidx.transition.Transition
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelKey
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ChildFragmentScope
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListFragment
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListModule
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
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
    fun provideIsLandscape(context: Context): () -> Boolean =
            { context.resources.getBoolean(R.bool.landscape) }

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
            VideoDetailFragmentArgs.fromBundle(videoDetailFragment.arguments!!).videoDetailParameter

    @JvmStatic
    @Provides
    @ChildFragmentScope
    fun provideVideoDetailPlayer(videoDetailPlayerImpl: VideoDetailPlayerImpl): VideoDetailPlayer =
            videoDetailPlayerImpl
}