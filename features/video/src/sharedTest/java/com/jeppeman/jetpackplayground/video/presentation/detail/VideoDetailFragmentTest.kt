package com.jeppeman.jetpackplayground.video.presentation.detail

import androidx.fragment.app.testing.FragmentScenario
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeppeman.jetpackplayground.common.presentation.AppUiContainer
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.common.presentation.extensions.observe
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.espresso.hasAnimationState
import com.jeppeman.jetpackplayground.video.presentation.espresso.hasSoundState
import com.jeppeman.jetpackplayground.video.presentation.espresso.hasTitle
import com.jeppeman.jetpackplayground.video.presentation.espresso.isLoading
import com.jeppeman.jetpackplayground.video.presentation.espresso.isVisibleToUser
import com.jeppeman.jetpackplayground.video.presentation.espresso.seekTo
import com.jeppeman.jetpackplayground.video.presentation.base.BaseFragmentTest
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import com.jeppeman.jetpackplayground.video.presentation.widget.PlayButtonAnimationBehavior
import com.jeppeman.jetpackplayground.video.presentation.widget.ToggleSoundButton
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class VideoDetailFragmentTest : BaseFragmentTest<VideoDetailFragment, VideoDetailViewModel>() {
    private lateinit var idlingResource: VideoDetailAnimationIdlingResource
    @Mock
    private lateinit var mockState: VideoDetailViewModel.State
    @Mock
    private lateinit var mockInitState: VideoDetailViewModel.InitState
    @Mock
    private lateinit var mockPausedState: VideoDetailViewModel.PausedState
    @Mock
    private lateinit var mockPlayingState: VideoDetailViewModel.PlayingState
    @Mock
    private lateinit var mockLoadingState: VideoDetailViewModel.LoadingState
    @Mock
    private lateinit var mockLoadingFinishedState: VideoDetailViewModel.LoadingFinishedState
    @Mock
    private lateinit var mockCompletedState: VideoDetailViewModel.CompletedState
    @Mock
    private lateinit var mockErrorState: VideoDetailViewModel.ErrorState
    @Mock
    private lateinit var mockVideoDetailPlayer: VideoDetailPlayer
    @Mock
    private lateinit var mockAppUiContainer: AppUiContainer
    @Mock
    override lateinit var viewModel: VideoDetailViewModel
    override val fragmentClass = VideoDetailFragment::class

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        idlingResource = VideoDetailAnimationIdlingResource()
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    private fun launchInFullscreen(
            showOverlay: Boolean = true,
            onInstantiated: (VideoDetailFragment) -> Unit = {}
    ): FragmentScenario<VideoDetailFragment> {
        return launchInLandscape { fragment ->
            whenever(viewModel.screenMode).thenReturn(mutableLiveDataOf(ScreenMode.FULLSCREEN))
            whenever(viewModel.overlayVisible).thenReturn(mutableLiveDataOf(showOverlay))
            onInstantiated(fragment)
        }
    }

    override fun onFragmentInstantiated(fragment: VideoDetailFragment) {
        viewModel.apply {
            whenever(video).thenReturn(VideoModel("", "", "", "", ""))
            whenever(currentProgressText).thenReturn(mutableLiveDataOf(""))
            whenever(videoLengthText).thenReturn(mutableLiveDataOf(""))
            whenever(videoLength).thenReturn(mutableLiveDataOf(0L))
            whenever(videoProgress).thenReturn(mutableLiveDataOf(0L))
            whenever(videoDetailPlayer).thenReturn(mockVideoDetailPlayer)
            whenever(title).thenReturn(mutableLiveDataOf(""))
            whenever(state).thenReturn(mutableLiveDataOf(mockState))
            whenever(soundOn).thenReturn(mutableLiveDataOf(false))
            whenever(overlayVisible).thenReturn(mutableLiveDataOf(false))
            whenever(screenMode).thenReturn(mutableLiveDataOf(ScreenMode.UNDEFINED))
            fragment.viewModel = this
            fragment.appUiContainer = mockAppUiContainer
        }
    }

    @Test
    fun whenNotFullscreen_initState_shouldSetPausedPlayButton() {
        launch {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockInitState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenFullscreen_initState_shouldSetPausedPlayButton() {
        launchInFullscreen {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockInitState))
        }

        onView(withId(R.id.fullScreenPlay))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenNotFullscreen_pausedState_shouldSetPausedPlayButton() {
        launch {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPausedState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenFullscreen_pausedState_shouldSetPausedPlayButton() {
        launchInFullscreen {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPausedState))
        }

        onView(withId(R.id.fullScreenPlay))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PAUSED)))
    }

    @Test
    fun whenInitial_playingState_shouldShowVideoActionsAndSetPlayButtonToPlayingState() {
        launch {
            whenever(mockPlayingState.initial).thenReturn(true)
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        listOf(
                R.id.close,
                R.id.rewind,
                R.id.mainAction,
                R.id.fastForward,
                R.id.fullScreen
        ).forEach { id ->
            onView(withId(id)).check(matches(isVisibleToUser())).perform(click())
        }

        onView(withId(R.id.mainAction))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PLAYING)))
    }

    @Test
    fun whenNotInitial_playingState_shouldSetPlayButtonToPlayingState() {
        launch {
            whenever(mockPlayingState.initial).thenReturn(false)
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.PLAYING)))
    }

    @Test
    fun loadingState_shouldShowLoader() {
        IdlingRegistry.getInstance().unregister(idlingResource)
        launch {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockLoadingState))
        }

        onView(withId(R.id.arc))
                .check(matches(isVisibleToUser()))
                .check(matches(isLoading()))

        IdlingRegistry.getInstance().register(idlingResource)
    }

    @Test
    fun loadingFinishedState_shouldHideLoaderAndNotifyViewModel() {
        var stateLiveData: MutableLiveData<VideoDetailViewModel.State>? = null
        launch {
            stateLiveData = mutableLiveDataOf(mockLoadingState)
            whenever(viewModel.state).thenReturn(stateLiveData)
        }.onFragment {
            stateLiveData?.value = mockLoadingFinishedState
        }

        onView(withId(R.id.arc))
                .check(matches(not(isVisibleToUser())))
                .check(matches(not(isLoading())))
        verify(viewModel).onLoadingAnimationFinished()
    }

    @Test
    fun whenNotFullscreen_completedState_shouldSetCompletedStateOnPlayButton() {
        launch {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockCompletedState))
        }

        onView(withId(R.id.mainAction))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.COMPLETED)))
    }

    @Test
    fun whenFullscreen_completedState_shouldSetCompletedStateOnPlayButton() {
        launchInFullscreen {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockCompletedState))
        }

        onView(withId(R.id.fullScreenPlay))
                .check(matches(isVisibleToUser()))
                .check(matches(hasAnimationState(PlayButtonAnimationBehavior.State.COMPLETED)))
    }

    @Test
    fun errorState_shouldShowSnackbar() {
        launch {
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockErrorState))
        }

        onView(withText(R.string.video_detail_dismiss_snackbar)).check(matches(isVisibleToUser()))
        onView(withText(R.string.video_detail_video_failed_to_load)).check(matches(isVisibleToUser()))
    }

    @Test
    fun whenTrue_fullscreen_shouldHideActionsAndContentAndNotifyViewModelOnCompletion() {
        launch {
            whenever(viewModel.screenMode).thenReturn(mutableLiveDataOf(ScreenMode.FULLSCREEN))
        }

        listOf(
                R.id.mainAction,
                R.id.close,
                R.id.rewind,
                R.id.fastForward,
                R.id.fullScreen,
                R.id.videoDescription
        ).forEach { id ->
            onView(withId(id)).check(matches(not(isVisibleToUser())))
        }

        verify(viewModel).onLandscapeTransitionFinished()
    }


    @Test
    fun whenTrue_toggleOverlay_shouldShowVideoOverlay() {
        launchInFullscreen()

        listOf(
                R.id.videoProgress,
                R.id.currentProgressText,
                R.id.videoLengthText,
                R.id.fullScreenClose,
                R.id.fullScreenRewind,
                R.id.fullScreenPlay,
                R.id.fullScreenFastForward,
                R.id.fullScreenVolume
        ).forEach { id ->
            onView(withId(id)).check(matches(isVisibleToUser())).perform(click())
        }
    }

    @Test
    fun whenFalse_toggleOverlay_shouldHideVideoOverlay() {
        launchInFullscreen(false)

        listOf(
                R.id.videoProgress,
                R.id.currentProgressText,
                R.id.videoLengthText,
                R.id.fullScreenClose,
                R.id.fullScreenRewind,
                R.id.fullScreenPlay,
                R.id.fullScreenFastForward,
                R.id.fullScreenVolume
        ).forEach { id ->
            onView(withId(id)).check(matches(not(isVisibleToUser())))
        }
    }

    @Test
    fun setTitle_shouldSetTitleOnCollapsingToolbar() {
        val title = "Title"
        launch {
            whenever(viewModel.title).thenReturn(mutableLiveDataOf(title))
        }

        onView(withId(R.id.collapsingToolbar))
                .check(matches(isVisibleToUser()))
                .check(matches(hasTitle(title)))
    }

    @Test
    fun whenSoundOn_toggleSoundButton_shouldSetButtonStateToOff() {
        var soundLiveData: MutableLiveData<Boolean>? = null
        launchInFullscreen {
            soundLiveData = mutableLiveDataOf(true)
            whenever(viewModel.soundOn).thenReturn(soundLiveData)
        }.onFragment {
            soundLiveData?.value = false
        }

        onView(withId(R.id.fullScreenVolume))
                .check(matches(isVisibleToUser()))
                .check(matches(hasSoundState(ToggleSoundButton.SoundState.OFF)))
    }

    @Test
    fun whenSoundOff_toggleSoundButton_shouldSetButtonStateToOn() {
        var soundLiveData: MutableLiveData<Boolean>? = null
        launchInFullscreen {
            soundLiveData = mutableLiveDataOf(false)
            whenever(viewModel.soundOn).thenReturn(soundLiveData)
        }.onFragment {
            soundLiveData?.value = true
        }

        onView(withId(R.id.fullScreenVolume))
                .check(matches(isVisibleToUser()))
                .check(matches(hasSoundState(ToggleSoundButton.SoundState.ON)))
    }

    @Test
    fun clickPlay_shouldDelegateToViewModel() {
        launch()

        onView(withId(R.id.mainAction)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onPlayClick()
    }

    @Test
    fun clickFullscreenPlay_shouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenPlay)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onPlayClick()
    }

    @Test
    fun whenPlaying_clickCloseVideo_shouldDelegateToViewModel() {
        launch {
            whenever(mockPlayingState.initial).thenReturn(true)
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.close)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onCloseVideoClick()
    }

    @Test
    fun clickCloseVideoFullscreen_shouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenClose)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onCloseVideoClick()
    }

    @Test
    fun whenPlaying_clickRewind_shouldDelegateToViewModel() {
        launch {
            whenever(mockPlayingState.initial).thenReturn(true)
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.rewind)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onRewindClick()
    }

    @Test
    fun clickRewindFullscreen_shouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenRewind)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onRewindClick()
    }

    @Test
    fun whenPlaying_clickFastForward_shouldDelegateToViewModel() {
        launch {
            whenever(mockPlayingState.initial).thenReturn(true)
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.fastForward)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onFastForwardClick()
    }

    @Test
    fun clickFastForwardFullscreen_shouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenFastForward)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onFastForwardClick()
    }

    @Test
    fun whenPlaying_clickFullscreen_shouldDelegateToViewModel() {
        launch {
            whenever(mockPlayingState.initial).thenReturn(true)
            whenever(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
        }

        onView(withId(R.id.fullScreen)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onFullscreenClick()
    }

    @Test
    fun clickVolume_shouldDelegateToViewModel() {
        launchInFullscreen()

        onView(withId(R.id.fullScreenVolume)).check(matches(isVisibleToUser())).perform(click())

        verify(viewModel).onVolumeClick()
    }

    @Test
    fun changeVideoProgress_shouldNotifyViewModel() {
        val progress = 50L
        launchInFullscreen {
            whenever(viewModel.videoLength).thenReturn(mutableLiveDataOf(100))
        }

        onView(withId(R.id.videoProgress))
                .check(matches(isVisibleToUser()))
                .perform(seekTo(progress.toInt()))

        verify(viewModel).setVideoProgress(progress)
    }
}