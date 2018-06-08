package com.jeppeman.jetpackplayground.video.presentation.list

import androidx.lifecycle.LiveData
import com.jeppeman.jetpackplayground.common.presentation.LifecycleAwareCoroutineViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListItem
import kotlinx.coroutines.Job

interface VideoListViewModel : LifecycleAwareCoroutineViewModel {
    val items: LiveData<List<VideoListItem>>
    fun refresh(): Job
}
