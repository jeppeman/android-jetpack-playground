package com.jeppeman.jetpackplayground.video.data.entity

import androidx.annotation.Keep
import com.squareup.moshi.Json

@Keep
data class VideoCategoryEntity(
        @Json(name = "name") val name: String,
        @Json(name = "videos") val videos: List<VideoEntity>
)