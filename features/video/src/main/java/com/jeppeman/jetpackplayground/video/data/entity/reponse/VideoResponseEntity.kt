package com.jeppeman.jetpackplayground.video.data.entity.reponse

import com.jeppeman.jetpackplayground.video.data.entity.VideoCategoryEntity
import com.squareup.moshi.Json

data class VideoResponseEntity(@Json(name = "categories") val categories: List<VideoCategoryEntity>)