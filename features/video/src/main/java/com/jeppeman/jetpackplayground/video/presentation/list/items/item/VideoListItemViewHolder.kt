package com.jeppeman.jetpackplayground.video.presentation.list.items.item

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.jeppeman.jetpackplayground.common.presentation.extensions.findViewWithTransitionName
import com.jeppeman.jetpackplayground.common.presentation.extensions.setImageUrl
import com.jeppeman.jetpackplayground.video_resources.R
import com.jeppeman.jetpackplayground.video.presentation.detail.VideoDetailParameter
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListViewHolder
import com.jeppeman.jetpackplayground.video.presentation.navigation.NavigationRequest

class VideoListItemViewHolder(private val view: ViewGroup) : VideoListViewHolder<VideoListItemViewModel>(view) {
    override fun bind(item: VideoListItemViewModel) {
        view.apply {
            findViewById<View>(R.id.videoThumb)?.transitionName = item.videoModel.id
            findViewById<ImageView>(R.id.videoThumb)?.setImageUrl(item.videoModel.thumb)
            findViewById<TextView>(R.id.videoTitle)?.text = item.videoModel.title
            findViewById<TextView>(R.id.videoSubTitle)?.text = item.videoModel.subtitle
            findViewById<View>(R.id.root)?.setOnClickListener {
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