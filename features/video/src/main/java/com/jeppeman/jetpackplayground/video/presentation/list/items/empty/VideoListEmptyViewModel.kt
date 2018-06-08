package com.jeppeman.jetpackplayground.video.presentation.list.items.empty

import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListItem
import javax.inject.Inject

class VideoListEmptyViewModel @Inject constructor(val parent: VideoListViewModel) : VideoListItem {
    override val id = "video_list_empty"
    override val type = R.layout.video_list_empty_layout

    fun handleClick() {
        parent.refresh()
    }
}