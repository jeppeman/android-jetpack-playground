package com.jeppeman.jetpackplayground.video.presentation.list.items.item

import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.jeppeman.jetpackplayground.common.presentation.extensions.findViewWithTransitionName
import com.jeppeman.jetpackplayground.common.presentation.extensions.setImageUrl
import com.jeppeman.jetpackplayground.video.presentation.detail.VideoDetailParameter
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListViewHolder
import com.jeppeman.jetpackplayground.video.presentation.navigation.NavigationRequest
import kotlinx.android.synthetic.main.video_item_layout.view.*

class VideoListItemViewHolder(private val view: ViewGroup) : VideoListViewHolder<VideoListItemViewModel>(view) {
    override fun bind(item: VideoListItemViewModel) {
        view.apply {
            videoThumb?.transitionName = item.videoModel.id
            videoThumb?.setImageUrl(item.videoModel.thumb)
            videoTitle?.text = item.videoModel.title
            videoSubTitle?.text = item.videoModel.subtitle
            root?.setOnClickListener {
                val navigationRequest = NavigationRequest.ListToDetail(VideoDetailParameter(item.videoModel))
                findNavController().navigate(
                        navigationRequest.destination,
                        findViewWithTransitionName(item.videoModel.id)?.let { view ->
                            FragmentNavigatorExtras(view to item.videoModel.id)
                        } ?: FragmentNavigatorExtras()
                )
            }
        }
    }
}