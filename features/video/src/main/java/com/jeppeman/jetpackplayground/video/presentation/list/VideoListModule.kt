package com.jeppeman.jetpackplayground.video.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.jeppeman.jetpackplayground.common.presentation.ViewModelFactory
import com.jeppeman.jetpackplayground.common.presentation.ViewModelKey
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ChildFragmentScope
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
object VideoListModule {
    @JvmStatic
    @Provides
    @IntoMap
    @ViewModelKey(VideoListViewModelImpl::class)
    fun provideVideoListViewModelIntoMap(videoListViewModelImpl: VideoListViewModelImpl): ViewModel =
            videoListViewModelImpl

    @JvmStatic
    @Provides
    @ChildFragmentScope
    fun provideVideoListViewModel(
            videoListFragment: VideoListFragment,
            viewModelFactory: ViewModelFactory
    ): VideoListViewModel = ViewModelProviders.of(
            videoListFragment,
            viewModelFactory
    )[VideoListViewModelImpl::class.java]
}