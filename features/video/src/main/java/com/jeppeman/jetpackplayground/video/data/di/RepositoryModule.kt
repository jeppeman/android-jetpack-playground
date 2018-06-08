package com.jeppeman.jetpackplayground.video.data.di

import com.jeppeman.jetpackplayground.video.data.repository.VideoRepositoryImpl
import com.jeppeman.jetpackplayground.video.domain.repository.VideoRepository
import com.jeppeman.jetpackplayground.video.platform.di.VideoScope
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class RepositoryModule {
    @Binds
    @VideoScope
    abstract fun bindVideoRepository(videoRepositoryImpl: VideoRepositoryImpl): VideoRepository
}