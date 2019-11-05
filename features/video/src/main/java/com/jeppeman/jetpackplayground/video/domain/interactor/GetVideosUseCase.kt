package com.jeppeman.jetpackplayground.video.domain.interactor

import com.jeppeman.jetpackplayground.domain.Result
import com.jeppeman.jetpackplayground.domain.interactor.UseCase
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.domain.repository.VideoRepository
import javax.inject.Inject

class GetVideosUseCase(private val videoRepository: VideoRepository)
    : UseCase<Result<List<Video>>>{
    override suspend fun execute(): Result<List<Video>> = videoRepository.getVideos()
}