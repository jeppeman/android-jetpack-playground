package com.jeppeman.jetpackplayground.video.data.net

import com.jeppeman.jetpackplayground.video.data.entity.reponse.VideoResponseEntity
import retrofit2.http.GET

interface VideoApi {
    @GET("videos-enhanced-c.json")
    suspend fun getVideos(): VideoResponseEntity
}