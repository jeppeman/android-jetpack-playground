package com.jeppeman.jetpackplayground.video.presentation.list.items.empty

import android.view.ViewGroup
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListViewHolder
import kotlinx.android.synthetic.main.video_list_empty_layout.view.*

class VideoListEmptyViewHolder(private val view: ViewGroup) : VideoListViewHolder<VideoListEmptyViewModel>(view) {
    override fun bind(item: VideoListEmptyViewModel) {
        view.refreshFab?.setOnClickListener {
            item.handleClick()
        }
    }
}