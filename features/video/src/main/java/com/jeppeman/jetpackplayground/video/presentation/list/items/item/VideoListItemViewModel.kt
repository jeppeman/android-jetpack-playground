package com.jeppeman.jetpackplayground.video.presentation.list.items.item

import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.jeppeman.jetpackplayground.video_resources.R
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListItem

class VideoListItemViewModel(val videoModel: VideoModel) : VideoListItem {
    override val id = videoModel.id
    override val type = R.layout.video_item_layout
}