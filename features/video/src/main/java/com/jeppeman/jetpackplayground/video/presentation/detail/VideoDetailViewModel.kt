package com.jeppeman.jetpackplayground.video.presentation.detail

import androidx.lifecycle.LiveData
import com.jeppeman.jetpackplayground.common.presentation.LifecycleAwareCoroutineViewModel
import com.jeppeman.jetpackplayground.common.presentation.extensions.mutableLiveDataOf
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import javax.inject.Inject

interface VideoDetailViewModel : LifecycleAwareCoroutineViewModel {
    var landscape: Boolean
    val video: VideoModel
    val videoDetailPlayer: VideoDetailPlayer
    val screenMode: LiveData<ScreenMode>
    val state: LiveData<State>
    val overlayVisible: LiveData<Boolean>
    val soundOn: LiveData<Boolean>
    val videoLength: LiveData<Long>
    val title: LiveData<String>
    val currentProgressText: LiveData<String>
    val videoLengthText: LiveData<String>
    val videoProgress: LiveData<Long>

    fun enterFullscreen()
    fun setVideoProgress(progress: Long)
    fun onLoadingAnimationFinished()
    fun onLandscapeTransitionFinished()
    fun onOverlayClick()
    fun onPlayClick()
    fun onCloseVideoClick()
    fun onRewindClick()
    fun onFastForwardClick()
    fun onFullscreenClick()
    fun onVolumeClick()

    interface State {
        val videoVisible get() = true
        val subActionsTag: String? get() = FabContainerBehavior.TAG_FAB_EXCLUDED
        fun play(context: StateContext) {}
        fun onPlaybackReady(context: StateContext) {}
        fun onCompleted(context: StateContext) {}
        fun onError(context: StateContext, what: Int?) {}
        fun onLoadingAnimationFinished(context: StateContext) {}
        fun onScreenModeChanged(context: StateContext, mode: ScreenMode) {}
        fun onCreate(context: StateContext) {}
        fun onStop(context: StateContext) {}
        fun onStart(context: StateContext) {}
    }

    interface InitState : State
    interface PausedState : State
    interface PlayingState : State {
        val initial: Boolean
    }
    interface LoadingState : State
    interface LoadingFinishedState : State
    interface CompletedState : State
    interface ErrorState : State

    class StateContext constructor(initialState: State) {
        private val nonNullState get() = requireNotNull(state.value)
        val state = mutableLiveDataOf(initialState)

        fun onCreate() {
            nonNullState.onCreate(this)
        }

        fun onStop() {
            nonNullState.onStop(this)
        }

        fun onStart() {
            nonNullState.onStart(this)
        }

        fun onCompleted() {
            nonNullState.onCompleted(this)
        }

        fun onPlaybackReady() {
            nonNullState.onPlaybackReady(this)
        }

        fun onError(what: Int?) {
            nonNullState.onError(this, what)
        }

        fun play() {
            nonNullState.play(this)
        }

        fun onLoadingAnimationFinished() {
            nonNullState.onLoadingAnimationFinished(this)
        }

        fun onScreenModeChanged(mode: ScreenMode) {
            nonNullState.onScreenModeChanged(this, mode)
        }
    }
}