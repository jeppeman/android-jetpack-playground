package com.jeppeman.jetpackplayground.video.presentation.navigation

import androidx.navigation.NavDirections
import com.jeppeman.jetpackplayground.video.presentation.detail.VideoDetailParameter
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListFragmentDirections

sealed class NavigationRequest(val destination: NavDirections) {
    data class ListToDetail(val param: VideoDetailParameter)
        : NavigationRequest(VideoListFragmentDirections.actionVideoListFragmentToVideoDetailFragment(param))
}