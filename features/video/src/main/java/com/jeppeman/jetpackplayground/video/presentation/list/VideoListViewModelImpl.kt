package com.jeppeman.jetpackplayground.video.presentation.list

import androidx.lifecycle.MutableLiveData
import com.jeppeman.jetpackplayground.common.presentation.BaseViewModel
import com.jeppeman.jetpackplayground.video.domain.interactor.GetVideosUseCase
import com.jeppeman.jetpackplayground.video.presentation.list.items.VideoListItem
import com.jeppeman.jetpackplayground.video.presentation.list.items.empty.VideoListEmptyViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.item.VideoListItemViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.loading.VideoListLoadingViewModel
import com.jeppeman.jetpackplayground.video.presentation.model.mapper.VideoModelMapper
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class VideoListViewModelImpl @Inject constructor(
        private val getVideosUseCase: GetVideosUseCase,
        private val videoModelMapper: VideoModelMapper
) : BaseViewModel(), VideoListViewModel {

    override val items = MutableLiveData<List<VideoListItem>>()

    private fun onFail(error: Throwable) {
        Timber.e(error)
        items.value = listOf(VideoListEmptyViewModel(this))
    }

    private fun addLoadingItem() {
        items.value = listOf(VideoListLoadingViewModel())
    }

    override fun onInitialize() {
        refresh()
    }

    override fun refresh() = launch {
        addLoadingItem()
        getVideosUseCase.execute()
                .doOnSuccess { videos ->
                    val videoModels = videoModelMapper.toModel(videos).map(::VideoListItemViewModel)
                    items.value = videoModels
                }
                .doOnFailure { exception ->
                    exception?.let(::onFail)
                }
    }
}