package com.jeppeman.jetpackplayground.video.platform.di

import com.jeppeman.jetpackplayground.video.presentation.VideoFragmentComponent
import com.jeppeman.jetpackplayground.video.presentation.model.mapper.VideoModelMapper
import dagger.Module
import dagger.Provides

@Module(subcomponents = [VideoFragmentComponent::class])
object VideoModule {
    @JvmStatic
    @Provides
    @VideoScope
    fun provideVideoModelMapper(): VideoModelMapper = VideoModelMapper()
}