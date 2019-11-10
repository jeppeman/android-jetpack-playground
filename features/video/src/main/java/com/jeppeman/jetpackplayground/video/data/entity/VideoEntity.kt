package com.jeppeman.jetpackplayground.video.data.entity

import com.squareup.moshi.Json

data class VideoEntity(
        @Json(name = "title")
        val title: String?,
        @Json(name = "subtitle")
        val subtitle: String?,
        @Json(name = "sources")
        val sources: List<String>?,
        @Json(name = "image-480x270")
        val thumb: String?
)