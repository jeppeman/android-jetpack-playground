package com.jeppeman.jetpackplayground.video.data.entity

import com.squareup.moshi.Json

data class VideoCategoryEntity(
        @Json(name = "name") val name: String,
        @Json(name = "videos") val videos: List<VideoEntity>
)