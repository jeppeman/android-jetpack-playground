package com.jeppeman.jetpackplayground.video.presentation.list

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.jeppeman.jetpackplayground.common.presentation.BaseFragment
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListItem
import kotlinx.android.synthetic.main.fragment_video_list.*
import javax.inject.Inject


class VideoListFragment : BaseFragment<VideoListViewModel>() {
    @Inject
    override lateinit var viewModel: VideoListViewModel
    @Inject
    internal lateinit var videoListAdapter: VideoListAdapter
    override val layoutRes = R.layout.fragment_video_list

    private fun onItemsFetched(items: List<VideoListItem>) {
        videoListAdapter.updateItems(items)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoList?.apply {
            adapter = videoListAdapter
            itemAnimator = DefaultItemAnimator().apply {
                addDuration = 1000
                removeDuration = 1000
            }
            (layoutManager as? GridLayoutManager)?.spanSizeLookup = videoListAdapter.spanSizeLookup
            doOnLayout {
                startPostponedEnterTransition()
            }
        }
        viewModel.items.observe(viewLifecycleOwner, ::onItemsFetched)
    }

    override fun onStop() {
        super.onStop()
        postponeEnterTransition()
    }
}
