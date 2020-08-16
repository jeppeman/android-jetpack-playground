package com.jeppeman.jetpackplayground.video.presentation.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.ChildFragmentScope
import com.jeppeman.jetpackplayground.common.presentation.di.scopes.FragmentScope
import com.jeppeman.jetpackplayground.video_resources.R
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListItem
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListViewHolder
import com.jeppeman.jetpackplayground.video.presentation.list.items.empty.VideoListEmptyViewHolder
import com.jeppeman.jetpackplayground.video.presentation.list.items.empty.VideoListEmptyViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.item.VideoListItemViewHolder
import com.jeppeman.jetpackplayground.video.presentation.list.items.item.VideoListItemViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.loading.VideoListLoadingViewHolder
import com.jeppeman.jetpackplayground.video.presentation.list.items.loading.VideoListLoadingViewModel
import javax.inject.Inject

@ChildFragmentScope
class VideoListAdapter @Inject constructor() : RecyclerView.Adapter<VideoListViewHolder<out VideoListItem>>() {
    private var dataSet = listOf<VideoListItem>()

    val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (dataSet[position]) {
                is VideoListItemViewModel -> 1
                else -> 2
            }
        }
    }

    fun updateItems(newDataSet: List<VideoListItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(dataSet, newDataSet))
        diffResult.dispatchUpdatesTo(this)
        dataSet = newDataSet
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder<out VideoListItem> {
        return LayoutInflater.from(parent.context).inflate(viewType, parent, false).run {
            val view = this as ViewGroup
            when (viewType) {
                R.layout.video_item_layout -> VideoListItemViewHolder(view)
                R.layout.video_list_empty_layout -> VideoListEmptyViewHolder(view)
                R.layout.video_list_loading_layout -> VideoListLoadingViewHolder(view)
                else -> throw IllegalArgumentException("Unexpected viewType $viewType")
            }
        }
    }

    override fun getItemViewType(position: Int): Int = dataSet[position].type

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: VideoListViewHolder<out VideoListItem>, position: Int) {
        val item = dataSet[position]
        when (holder) {
            is VideoListItemViewHolder -> holder.bind(item as VideoListItemViewModel)
            is VideoListEmptyViewHolder -> holder.bind(item as VideoListEmptyViewModel)
            is VideoListLoadingViewHolder -> holder.bind(item as VideoListLoadingViewModel)
        }
    }

    class DiffCallback(
            private val oldItems: List<VideoListItem>,
            private val newItems: List<VideoListItem>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldItems[oldPosition].id == newItems[newPosition].id
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return areItemsTheSame(oldPosition, newPosition)
        }

        override fun getOldListSize(): Int = oldItems.size

        override fun getNewListSize(): Int = newItems.size
    }
}