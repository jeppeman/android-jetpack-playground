package com.jeppeman.jetpackplayground.video.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.video.presentation.util.setProperty
import com.jeppeman.jetpackplayground.video.presentation.base.BaseViewModelTest
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.never
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class VideoDetailViewModelImplTest : BaseViewModelTest<VideoDetailViewModelImpl>() {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var mockVideoDetailPlayer: VideoDetailPlayer
    @Mock
    private lateinit var mockStateContext: VideoDetailViewModel.StateContext

    private val videoModel = VideoModel(
            title = "Title",
            subtitle = "Subtitle",
            source = "http://source.com",
            thumb = "http://thumb.com"
    )
    override lateinit var viewModel: VideoDetailViewModelImpl

    override fun before() {
        whenever(mockStateContext.state).thenReturn(mutableLiveDataOf())
        viewModel = spy(VideoDetailViewModelImpl(
                videoDetailPlayer = mockVideoDetailPlayer,
                stateContext = mockStateContext,
                videoDetailParameter = VideoDetailParameter(videoModel),
                isLandscape = {false}
        ))
    }

    @Test
    fun timeFormat_ShouldFormatProperly() {
        assertThat((1000 * 2L).timeFormat()).isEqualTo("00:02")
        assertThat((1000 * 20L).timeFormat()).isEqualTo("00:20")
        assertThat((1000 * 62L).timeFormat()).isEqualTo("01:02")
        assertThat((1000 * 602L).timeFormat()).isEqualTo("10:02")
        assertThat((1000 * 620L).timeFormat()).isEqualTo("10:20")
        assertThat((1000 * 3602L).timeFormat()).isEqualTo("01:00:02")
        assertThat((1000 * 36620L).timeFormat()).isEqualTo("10:10:20")
    }

    @Test
    fun onInitialize_shouldSetTitleInitializePlayerAndObserveScreenMode() {
        viewModel.onInitialize()

        assertThat(viewModel.video).isEqualTo(videoModel)
        assertThat(viewModel.title.value).isEqualTo(videoModel.title)
        verify(viewModel).resetState()
        verify(mockVideoDetailPlayer).videoUrl = videoModel.source
        verify(mockVideoDetailPlayer).registerCompletionListener(mockStateContext::onCompleted)
        verify(mockVideoDetailPlayer).registerErrorListener(mockStateContext::onError)
        verify(mockVideoDetailPlayer).registerPlaybackReadyListener(viewModel::onPlaybackReady)
        verify(mockVideoDetailPlayer).registerProgressListener(viewModel::onVideoProgress)
    }

    @Test
    fun whenLeaping_setVideoProgress_shouldShowOverlayAndSetProgressOnPlayer() {
        val fakeProgress = 3L

        viewModel.setVideoProgress(fakeProgress)

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).progress = fakeProgress
        assertThat(viewModel.currentProgressText.value).isEqualTo(fakeProgress.timeFormat())
    }

    @Test
    fun whenNotLeaping_setVideoProgress_shouldNotShowOverlayNorSetProgressOnPlayer() {
        val fakeProgress = 3L
        viewModel.setProperty("currentProgressFromPlayer", fakeProgress)

        viewModel.setVideoProgress(fakeProgress)

        verify(viewModel, never()).showOverlay()
        verify(mockVideoDetailPlayer, never()).progress = fakeProgress
        assertThat(viewModel.currentProgressText.value).isEqualTo(fakeProgress.timeFormat())
    }

    @Test
    fun onPlaybackReady_shouldSetVideoLengthAndDelegateToStateContext() {
        val fakeVideoLength = 500L
        whenever(mockVideoDetailPlayer.duration).thenReturn(fakeVideoLength)

        viewModel.onPlaybackReady()

        assertThat(viewModel.videoLength.value).isEqualTo(fakeVideoLength)
        assertThat(viewModel.videoLengthText.value).isEqualTo(fakeVideoLength.timeFormat())
        verify(mockStateContext).onPlaybackReady()
    }

    @Test
    fun resetState_shouldSetStateToInitState() {
        viewModel.resetState()

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.InitStateImpl::class.java)
    }

    @Test
    fun hideOverlay_shouldSetOverlayVisibleToFalseAndDisposeCountDownDisposable() {
        viewModel.hideOverlay()

        assertThat(viewModel.overlayVisible.value).isFalse()
    }

    @Test
    fun overlayCountDownFinished_shouldHideOverlayAndEnterFullscreen() {
        viewModel.onOverlayCountDownFinished()

        verify(viewModel).hideOverlay()
        verify(viewModel).enterFullscreen()
    }

    @Test
    fun whenScreenModeIsNotLandscape_showOverlay_shouldDoNothing() {
        whenever(viewModel.screenMode).thenReturn(mutableLiveDataOf(ScreenMode.UNDEFINED))

        viewModel.showOverlay()

        assertThat(viewModel.overlayVisible.value).isFalse()
    }

    @Test
    fun onCleared_shouldHideOverlayAndReleaseMediaPlayerAndExitFullscreen() {
        viewModel.onCleared()

        verify(mockVideoDetailPlayer).release()
    }

    @Test
    fun onStop_shouldDelegateToStateContext() {
        viewModel.onStop()

        verify(mockStateContext).onStop()
    }

    @Test
    fun onStart_shouldDelegateToStateContext() {
        viewModel.onStart()

        verify(mockStateContext).onStart()
    }

    @Test
    fun onOverlayClick_shouldShowOverlay() {
        viewModel.onOverlayClick()

        verify(viewModel).showOverlay()
    }

    @Test
    fun onPlayClick_shouldShowOverlayAndDelegateToStateContext() {
        viewModel.onPlayClick()

        verify(viewModel).showOverlay()
        verify(mockStateContext).play()
    }

    @Test
    fun onCloseVideoClick_shouldResetAllState() {
        viewModel.onCloseVideoClick()

        verify(viewModel).hideOverlay()
        verify(viewModel).resetState()
        verify(viewModel).exitFullscreen()
        verify(mockVideoDetailPlayer).reset()
        assertThat(viewModel.screenMode.value).isNotEqualTo(ScreenMode.FULLSCREEN)
        assertThat(viewModel.title.value).isEqualTo(videoModel.title)
    }

    @Test
    fun onRewindClick_shouldShowOverlayAndDelegateToStatePlayer() {
        viewModel.onRewindClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).rewind()
    }

    @Test
    fun onFastForwardClick_shouldShowOverlayAndDelegateToStatePlayer() {
        viewModel.onFastForwardClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).fastForward()
    }

    @Test
    fun whenSoundOn_onVolumeClick_shouldShowOverlayAndMutePlayer() {
        viewModel.soundOn.value = true
        viewModel.onVolumeClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).mute()
        assertThat(viewModel.soundOn.value).isFalse()
    }

    @Test
    fun whenSoundOff_onVolumeClick_shouldShowOverlayAndUnmutePlayer() {
        viewModel.soundOn.value = false
        viewModel.onVolumeClick()

        verify(viewModel).showOverlay()
        verify(mockVideoDetailPlayer).unmute()
        assertThat(viewModel.soundOn.value).isTrue()
    }

    @Test
    fun onLandscapeTransitionFinished_shouldEnterFullscreenAndShowOverlay() {
        viewModel.onLandscapeTransitionFinished()

        verify(viewModel).showOverlay()
    }

    @Test
    fun onLoadingAnimationFinished_shouldDelegateToStateContext() {
        viewModel.onLoadingAnimationFinished()

        verify(mockStateContext).onLoadingAnimationFinished()
    }

    @Test
    fun whenInitStateAndVideoPlayerIsReady_play_shouldSetStateToPlaying() {
        whenever(mockVideoDetailPlayer.isReady).thenReturn(true)

        viewModel.InitStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenInitStateAndVideoPlayerIsNotReady_play_shouldSetStateToLoading() {
        whenever(mockVideoDetailPlayer.isReady).thenReturn(false)

        viewModel.InitStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingStateImpl::class.java)
    }

    @Test
    fun initPausedState_shouldPausePlayer() {
        viewModel.PausedStateImpl()

        verify(mockVideoDetailPlayer).pause()
    }

    @Test
    fun whenPausedStateAndVideoPlayerIsReady_play_shouldSetStateToPlaying() {
        whenever(mockVideoDetailPlayer.isReady).thenReturn(true)

        viewModel.PausedStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenPausedStateAndVideoPlayerIsNotReady_play_shouldSetStateToLoading() {
        whenever(mockVideoDetailPlayer.isReady).thenReturn(false)

        viewModel.PausedStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingStateImpl::class.java)
    }

    @Test
    fun whenPausedState_onStart_shouldSetStateToPlaying() {
        viewModel.PausedStateImpl().onStart(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenScreenModeIsLandscape_initPlayingState_shouldStartPlayerAndEnterFullscreen() {
        whenever(viewModel.screenMode).thenReturn(mutableLiveDataOf(ScreenMode.LANDSCAPE))

        viewModel.PlayingStateImpl()

        verify(mockVideoDetailPlayer).start()
        assertThat(viewModel.screenMode.value).isEqualTo(ScreenMode.FULLSCREEN)
        verify(viewModel, atLeast(2)).enterFullscreen()
        verify(viewModel).showOverlay()
    }

    @Test
    fun whenScreenModeIsNotLandscape_initPlayingState_shouldOnlyStartPlayer() {
        whenever(viewModel.screenMode).thenReturn(mutableLiveDataOf(ScreenMode.UNDEFINED))

        viewModel.PlayingStateImpl()

        verify(mockVideoDetailPlayer).start()
        assertThat(viewModel.screenMode.value).isNotEqualTo(ScreenMode.FULLSCREEN)
        verify(viewModel, never()).enterFullscreen()
        verify(viewModel, never()).showOverlay()
    }

    @Test
    fun whenPlayingState_onStop_shouldSetStateToPaused() {
        viewModel.PlayingStateImpl().onStop(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PausedStateImpl::class.java)
    }

    @Test
    fun whenPlayingState_play_shouldSetStateToPaused() {
        viewModel.PlayingStateImpl().play(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PausedStateImpl::class.java)
    }

    @Test
    fun whenPlayingState_onCompleted_shouldSetStateToCompleted() {
        viewModel.PlayingStateImpl().onCompleted(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.CompletedStateImpl::class.java)
    }

    @Test
    fun whenPlayingStateScreenModeIsLandscapeAndFullscreenIsFalse_onScreenModeChanged_shouldEnterFullscreen() {
        viewModel.screenMode.value = ScreenMode.UNDEFINED

        viewModel.PlayingStateImpl().onScreenModeChanged(mockStateContext, ScreenMode.LANDSCAPE)

        assertThat(viewModel.screenMode.value).isEqualTo(ScreenMode.FULLSCREEN)
        verify(viewModel, atLeast(1)).enterFullscreen()
        verify(viewModel).showOverlay()
    }

    @Test
    fun whenPlayingStateScreenModeIsNotLandscapeAndFullscreenIsTrue_onScreenModeChanged_shouldExitFullscreen() {
        viewModel.screenMode.value = ScreenMode.FULLSCREEN

        viewModel.PlayingStateImpl().onScreenModeChanged(mockStateContext, ScreenMode.UNDEFINED)

        assertThat(viewModel.screenMode.value).isNotEqualTo(ScreenMode.FULLSCREEN)
    }

    @Test
    fun whenLoadingState_onPlaybackReady_shouldSetStateToLoadingFinished() {
        viewModel.LoadingStateImpl().onPlaybackReady(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingFinishedStateImpl::class.java)
    }

    @Test
    fun whenLoadingState_onError_shouldSetStateToError() {
        viewModel.LoadingStateImpl().onError(mockStateContext, 0)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.ErrorStateImpl::class.java)
    }

    @Test
    fun whenLoadingFinishedState_onLoadingAnimationFinished_shouldSetStateToPlaying() {
        viewModel.LoadingFinishedStateImpl().onLoadingAnimationFinished(mockStateContext)

        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenCompletedState_play_shouldRestartPlayerAndSetStateToPlaying() {
        viewModel.CompletedStateImpl().play(mockStateContext)

        verify(mockVideoDetailPlayer).restart()
        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.PlayingStateImpl::class.java)
    }

    @Test
    fun whenErrorState_play_shouldSetStateToLoadingAndResetPlayer() {
        viewModel.ErrorStateImpl().play(mockStateContext)

        verify(mockVideoDetailPlayer).reset()
        assertThat(mockStateContext.state.value).isInstanceOf(VideoDetailViewModelImpl.LoadingStateImpl::class.java)
    }
}