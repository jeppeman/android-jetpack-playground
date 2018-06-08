package com.jeppeman.jetpackplayground.video.data.entity

import com.squareup.moshi.Json

data class VideoEntity(
        val title: String?,
        val subtitle: String?,
        val sources: List<String>?,
        @Json(name = "image-480x270")
        val thumb: String?
)