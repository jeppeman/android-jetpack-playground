package com.jeppeman.jetpackplayground.video.presentation.list.items

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class VideoListViewHolder<T : VideoListItem>(view: ViewGroup) : RecyclerView.ViewHolder(view) {
    abstract fun bind(item: T)
}