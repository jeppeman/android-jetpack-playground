package com.jeppeman.jetpackplayground.video.presentation.list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.domain.asResult
import com.jeppeman.jetpackplayground.video.presentation.base.BaseViewModelTest
import com.jeppeman.jetpackplayground.video.domain.interactor.GetVideosUseCase
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.presentation.list.items.empty.VideoListEmptyViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.item.VideoListItemViewModel
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.jeppeman.jetpackplayground.video.presentation.model.mapper.VideoModelMapper
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@SmallTest
@RunWith(MockitoJUnitRunner::class)
class VideoListViewModelImplTest : BaseViewModelTest<VideoListViewModelImpl>() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var mockVideoModelMapper: VideoModelMapper
    @Mock
    private lateinit var mockGetVideosUseCase: GetVideosUseCase
    override lateinit var viewModel: VideoListViewModelImpl
    private lateinit var videoItemViewModels: List<VideoListItemViewModel>

    private val videoModels = listOf(
            VideoModel(
                    title = "Fun title :)",
                    subtitle = "Fun subtitle :)",
                    source = "https://fun-video.com",
                    thumb = "https://fun-thumb.com"
            )
    )

    private val videos = listOf(
            Video(
                    title = "Fun title :)",
                    subtitle = "Fun subtitle :)",
                    source = "https://fun-video.com",
                    thumb = "https://fun-thumb.com"
            )
    )

    override fun before() = runBlocking {
        viewModel = spy(VideoListViewModelImpl(
                getVideosUseCase = mockGetVideosUseCase.apply {
                    whenever(execute()).thenReturn(listOf<Video>().asResult())
                },
                videoModelMapper = mockVideoModelMapper
        ))
        videoItemViewModels = listOf(VideoListItemViewModel(videoModels[0]))
    }

    @Test
    fun onInitialize_shouldTriggerRefresh() {
        runBlocking {
            whenever(mockGetVideosUseCase.execute()).thenReturn(listOf<Video>().asResult())

            viewModel.onInitialize()

            verify(viewModel).refresh()
        }
    }

    @Test
    fun whenVideosAvailable_refresh_shouldGenerateItemViewModels() = runBlocking {
        whenever(mockGetVideosUseCase.execute()).thenReturn(videos.asResult())
        whenever(mockVideoModelMapper.toModel(videos)).thenReturn(videoModels)

        viewModel.refresh()

        assertThat((viewModel.items.value?.first() as VideoListItemViewModel).videoModel).isEqualTo(videoItemViewModels[0].videoModel)
    }

    @Test
    fun whenError_refresh_shouldGenerateEmptyItemViewModel() = runBlocking {
        whenever(mockGetVideosUseCase.execute()).thenReturn(RuntimeException().asResult())

        viewModel.refresh()

        assertThat(viewModel.items.value?.first() is VideoListEmptyViewModel).isTrue()
    }
}