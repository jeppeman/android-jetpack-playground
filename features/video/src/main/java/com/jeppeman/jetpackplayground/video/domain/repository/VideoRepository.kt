package com.jeppeman.jetpackplayground.video.domain.repository

import com.jeppeman.jetpackplayground.domain.Result
import com.jeppeman.jetpackplayground.video.domain.model.Video

interface VideoRepository {
    suspend fun getVideos(): Result<List<Video>>
}