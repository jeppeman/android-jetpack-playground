package com.jeppeman.jetpackplayground.video.data.entity.reponse

import androidx.annotation.Keep
import com.jeppeman.jetpackplayground.video.data.entity.VideoCategoryEntity
import com.squareup.moshi.Json

@Keep
data class VideoResponseEntity(@Json(name = "categories") val categories: List<VideoCategoryEntity>)