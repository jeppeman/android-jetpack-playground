package com.jeppeman.jetpackplayground.video.presentation.detail

import android.os.Parcelable
import androidx.annotation.Keep
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class VideoDetailParameter(val videoModel: VideoModel) : Parcelable