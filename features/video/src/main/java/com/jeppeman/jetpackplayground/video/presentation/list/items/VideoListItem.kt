package com.jeppeman.jetpackplayground.video.presentation.list.items

import androidx.annotation.LayoutRes

interface VideoListItem {
    val id: String
    val type: Int
        @LayoutRes get
}