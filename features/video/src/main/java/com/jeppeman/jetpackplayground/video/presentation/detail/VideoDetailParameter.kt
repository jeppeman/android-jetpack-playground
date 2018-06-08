package com.jeppeman.jetpackplayground.video.presentation.detail

import android.os.Parcelable
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoDetailParameter(val videoModel: VideoModel) : Parcelable