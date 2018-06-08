package com.jeppeman.jetpackplayground.video.presentation.detail

import com.jeppeman.jetpackplayground.common.presentation.BaseViewModel
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

internal fun Long.timeFormat(): String {
    val totalSeconds = this / 1000
    val totalMinutes = totalSeconds / 60
    val totalHours = totalMinutes / 60
    val secondsInMinute = totalSeconds % 60
    val minutesInHour = totalMinutes % 60

    val secondsString = if (secondsInMinute < 10) "0$secondsInMinute" else secondsInMinute.toString()
    val minutesString = if (minutesInHour < 10) "0$minutesInHour:" else "$minutesInHour:"
    val hoursString = when {
        totalHours == 0L -> ""
        totalHours < 10 -> "0$totalHours:"
        else -> "$totalHours:"
    }

    return "$hoursString$minutesString$secondsString"
}

class VideoDetailViewModelImpl @Inject constructor(
        videoDetailParameter: VideoDetailParameter,
        private val isLandscape: () -> Boolean,
        private val stateContext: VideoDetailViewModel.StateContext,
        override var videoDetailPlayer: VideoDetailPlayer
) : BaseViewModel(), VideoDetailViewModel {

    private var ignoreProgressUpdatesFromUi = false
    private var currentProgressFromPlayer = 0L
    private var delayJob: Deferred<Unit>? = null
    private var landscape: Boolean = isLandscape()
        set(value) {
            field = value
            if (field && screenMode.value != ScreenMode.FULLSCREEN) {
                screenMode.value = ScreenMode.LANDSCAPE
                stateContext.onScreenModeChanged(ScreenMode.LANDSCAPE)
            } else if (!field && screenMode.value != ScreenMode.UNDEFINED) {
                screenMode.value = ScreenMode.UNDEFINED
                stateContext.onScreenModeChanged(ScreenMode.UNDEFINED)
            }
        }

    override val video: VideoModel = videoDetailParameter.videoModel
    override val state get() = stateContext.state
    override val overlayVisible = mutableLiveDataOf(false)
    override val soundOn = mutableLiveDataOf(true)
    override val title = mutableLiveDataOf("")
    override val videoLength = mutableLiveDataOf(0L)
    override val currentProgressText = mutableLiveDataOf("")
    override val videoLengthText = mutableLiveDataOf("")
    override val videoProgress = mutableLiveDataOf(0L)
    override var screenMode = mutableLiveDataOf(ScreenMode.UNDEFINED)

    override fun onInitialize() {
        resetState()
        title.value = video.title
        videoDetailPlayer.apply {
            videoUrl = video.source
            registerPlaybackReadyListener(::onPlaybackReady)
            registerCompletionListener(stateContext::onCompleted)
            registerErrorListener(stateContext::onError)
            registerProgressListener(::onVideoProgress)
        }
    }

    internal fun enterFullscreen() {
        screenMode.value = ScreenMode.FULLSCREEN
    }

    internal fun exitFullscreen() {
        screenMode.value = if (landscape) ScreenMode.LANDSCAPE else ScreenMode.UNDEFINED
    }

    fun resetState() {
        stateContext.state.value = InitStateImpl()
    }

    fun onOverlayCountDownFinished() {
        hideOverlay()
        enterFullscreen()
    }

    fun onVideoProgress(progress: Long) {
        currentProgressFromPlayer = progress
        setVideoProgress(currentProgressFromPlayer)
    }

    fun onPlaybackReady() {
        videoLength.value = videoDetailPlayer.duration
        videoLengthText.value = videoDetailPlayer.duration.timeFormat()
        stateContext.onPlaybackReady()
    }

    fun showOverlay() {
        if (screenMode.value == ScreenMode.FULLSCREEN) {
            overlayVisible.value = true
            launch {
                delayJob?.cancel()
                delayJob = async {
                    delay(5000)
                }
                delayJob?.invokeOnCompletion { error ->
                    if (error == null) {
                        onOverlayCountDownFinished()
                    }
                }
            }
        }
    }

    fun hideOverlay() {
        overlayVisible.value = false
        delayJob?.cancel()
    }

    override fun setVideoProgress(progress: Long) {
        if (progress != videoProgress.value) {
            videoProgress.value = progress
        }

        if (progress != currentProgressFromPlayer) {
            if (!ignoreProgressUpdatesFromUi) {
                showOverlay()
                videoDetailPlayer.progress = progress
            } else {
                ignoreProgressUpdatesFromUi = false
            }
        }

        currentProgressText.value = progress.timeFormat()
    }

    override fun onCleared() {
        super.onCleared()
        videoDetailPlayer.release()
    }

    override fun onCreate() {
        super.onCreate()
        landscape = isLandscape()
    }

    override fun onStop() {
        stateContext.onStop()
    }

    override fun onStart() {
        stateContext.onStart()
    }

    override fun onOverlayClick() {
        showOverlay()
    }

    override fun onPlayClick() {
        showOverlay()
        stateContext.play()
    }

    override fun onCloseVideoClick() {
        hideOverlay()
        resetState()
        exitFullscreen()
        videoDetailPlayer.reset()
        title.value = video.title
    }

    override fun onRewindClick() {
        showOverlay()
        videoDetailPlayer.rewind()
    }

    override fun onFastForwardClick() {
        showOverlay()
        videoDetailPlayer.fastForward()
    }

    override fun onFullscreenClick() {
        enterFullscreen()
        title.value = " "
    }

    override fun onVolumeClick() {
        showOverlay()
        val newVal = !requireNotNull(soundOn.value)
        soundOn.value = newVal
        if (newVal) {
            videoDetailPlayer.unmute()
        } else {
            videoDetailPlayer.mute()
        }
    }

    override fun onLandscapeTransitionFinished() {
        ignoreProgressUpdatesFromUi = true
        showOverlay()
    }

    override fun onLoadingAnimationFinished() {
        stateContext.onLoadingAnimationFinished()
    }

    inner class InitStateImpl : VideoDetailViewModel.InitState {
        override val videoVisible = false

        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = if (videoDetailPlayer.isReady) {
                PlayingStateImpl()
            } else {
                LoadingStateImpl()
            }
        }
    }

    inner class PausedStateImpl : VideoDetailViewModel.PausedState {
        override val subActionsTag: String? = null

        init {
            videoDetailPlayer.pause()
        }

        override fun onStart(context: VideoDetailViewModel.StateContext) {
            context.state.value = PlayingStateImpl(false)
        }

        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = if (videoDetailPlayer.isReady) {
                PlayingStateImpl(false)
            } else {
                LoadingStateImpl()
            }
        }
    }

    inner class PlayingStateImpl(override val initial: Boolean = true) : VideoDetailViewModel.PlayingState {
        override val subActionsTag: String? = null

        init {
            videoDetailPlayer.start()
            if (screenMode.value == ScreenMode.LANDSCAPE) {
                enterFullscreen()
            }
        }

        private fun enterFullscreen() {
            this@VideoDetailViewModelImpl.enterFullscreen()
            showOverlay()
        }

        override fun onStop(context: VideoDetailViewModel.StateContext) {
            context.state.value = PausedStateImpl()
        }

        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = PausedStateImpl()
        }

        override fun onCompleted(context: VideoDetailViewModel.StateContext) {
            context.state.value = CompletedStateImpl()
        }

        override fun onScreenModeChanged(context: VideoDetailViewModel.StateContext, mode: ScreenMode) {
            if (mode == ScreenMode.LANDSCAPE) {
                enterFullscreen()
            } else if (mode != ScreenMode.LANDSCAPE && mode != ScreenMode.FULLSCREEN) {
                exitFullscreen()
            }
        }
    }

    inner class LoadingStateImpl : VideoDetailViewModel.LoadingState {
        override fun onPlaybackReady(context: VideoDetailViewModel.StateContext) {
            context.state.value = LoadingFinishedStateImpl()
        }

        override fun onError(context: VideoDetailViewModel.StateContext, what: Int?) {
            context.state.value = ErrorStateImpl()
        }
    }

    inner class LoadingFinishedStateImpl : VideoDetailViewModel.LoadingFinishedState {
        override fun onLoadingAnimationFinished(context: VideoDetailViewModel.StateContext) {
            context.state.value = PlayingStateImpl()
        }
    }

    inner class CompletedStateImpl : VideoDetailViewModel.CompletedState {
        override val subActionsTag: String? = null

        override fun play(context: VideoDetailViewModel.StateContext) {
            videoDetailPlayer.restart()
            context.state.value = PlayingStateImpl(false)
        }
    }

    inner class ErrorStateImpl : VideoDetailViewModel.ErrorState {
        override fun play(context: VideoDetailViewModel.StateContext) {
            context.state.value = LoadingStateImpl()
            videoDetailPlayer.reset()
        }
    }
}