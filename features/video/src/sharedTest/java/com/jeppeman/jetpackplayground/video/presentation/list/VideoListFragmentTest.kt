package com.jeppeman.jetpackplayground.video.presentation.list

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.video.presentation.espresso.isVisibleToUser
import com.jeppeman.jetpackplayground.video.presentation.base.BaseFragmentTest
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.list.items.empty.VideoListEmptyViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.item.VideoListItemViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.items.loading.VideoListLoadingViewModel
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class VideoListFragmentTest : BaseFragmentTest<VideoListFragment, VideoListViewModel>() {
    private lateinit var videos: List<VideoListItemViewModel>
    override val fragmentClass = VideoListFragment::class
    @Mock
    override lateinit var viewModel: VideoListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        videos = listOf(
                VideoListItemViewModel(
                        VideoModel(
                                title = "First title :)",
                                subtitle = "Fun subtitle :)",
                                source = "https://fun-video.com",
                                thumb = "https://fun-thumb.com"
                        )
                ),
                VideoListItemViewModel(
                        VideoModel(
                                title = "Second title :)",
                                subtitle = "Fun subtitle :)",
                                source = "https://fun-video.com",
                                thumb = "https://fun-thumb.com"
                        )
                )
        )
    }

    override fun onFragmentInstantiated(fragment: VideoListFragment) {
        fragment.viewModel = viewModel
        fragment.videoListAdapter = VideoListAdapter()
        whenever(viewModel.items).thenReturn(mutableLiveDataOf(listOf()))
    }

//    @Test
//    fun clickVideoItem_shouldRequestNavigation() {
//        launch {
//            whenever(viewModel.items).thenReturn(mutableLiveDataOf(videos))
//        }
//
//        onView(withId(R.id.videoList)).check(matches(isVisibleToUser())).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
//        onView(withText(videos[0].videoModel.title)).check(matches(isVisibleToUser())).perform(click())
//
//        verify(viewModel).requestNavigation(NavigationRequest.ListToDetail(VideoDetailParameter(videos[0].videoModel)))
//    }

    @Test
    fun emptyDataItem_ShouldDisplayEmptyElement() {
        launch {
            whenever(viewModel.items).thenReturn(mutableLiveDataOf(listOf(VideoListEmptyViewModel(viewModel))))
        }

        onView(withId(R.id.videoList)).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withId(R.id.emptyContainer)).check(matches(isVisibleToUser()))
    }

    @Test
    fun loadingDataItem_ShouldDisplayLoaderElement() {
        launch {
            whenever(viewModel.items).thenReturn(mutableLiveDataOf(listOf(VideoListLoadingViewModel())))
        }

        onView(withId(R.id.videoList)).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withId(R.id.loader)).check(matches(isVisibleToUser()))
    }

    @Test
    fun videoDataItems_ShouldDisplayVideoElements() {
        launch {
            whenever(viewModel.items).thenReturn(mutableLiveDataOf(videos))
        }

        onView(withId(R.id.videoList)).check(matches(isVisibleToUser())).perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withText(videos[0].videoModel.title)).check(matches(isVisibleToUser()))
        onView(withText(videos[1].videoModel.title)).check(matches(isVisibleToUser()))
    }
}