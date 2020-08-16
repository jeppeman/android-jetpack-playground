package com.jeppeman.jetpackplayground.video.presentation.list.items.empty

import android.view.View
import android.view.ViewGroup
import com.jeppeman.jetpackplayground.video_resources.R
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListViewHolder

class VideoListEmptyViewHolder(private val view: ViewGroup) : VideoListViewHolder<VideoListEmptyViewModel>(view) {
    override fun bind(item: VideoListEmptyViewModel) {
        view.findViewById<View>(R.id.refreshFab)?.setOnClickListener {
            item.handleClick()
        }
    }
}