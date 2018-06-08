package com.jeppeman.jetpackplayground.video.data.repository

import com.jeppeman.jetpackplayground.domain.Result
import com.jeppeman.jetpackplayground.video.data.entity.VideoCategoryEntity
import com.jeppeman.jetpackplayground.video.data.entity.mapper.VideoEntityMapper
import com.jeppeman.jetpackplayground.video.data.net.VideoApi
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.domain.repository.VideoRepository
import com.jeppeman.jetpackplayground.video.platform.di.VideoScope
import dagger.Lazy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class VideoRepositoryImpl @Inject constructor(
        private val videoApi: Lazy<VideoApi>,
        private val videoEntityMapper: VideoEntityMapper,
        private val backgroundDispatcher: CoroutineDispatcher
) : VideoRepository {

    override suspend fun getVideos(): Result<List<Video>> = withContext(backgroundDispatcher) {
        Result.fromSuspending {
            videoApi.get()
                    .getVideos()
                    .categories
                    .flatMap(VideoCategoryEntity::videos)
                    .map(videoEntityMapper::toDomain)
        }
    }
}