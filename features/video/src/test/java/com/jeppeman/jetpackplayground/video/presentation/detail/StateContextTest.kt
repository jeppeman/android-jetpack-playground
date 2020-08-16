package com.jeppeman.jetpackplayground.video.presentation.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.jeppeman.jetpackplayground.video.presentation.orientation.ScreenMode
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class StateContextTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()
    @Mock
    private lateinit var mockState: VideoDetailViewModel.State
    private lateinit var stateContextImpl: VideoDetailViewModel.StateContext

    @Before
    fun setUp() {
        stateContextImpl = VideoDetailViewModel.StateContext(mockState)
        stateContextImpl.state.value = mockState
    }

    @Test
    fun onStop_ShouldDelegateToState() {
        stateContextImpl.onStop()

        verify(mockState).onStop(stateContextImpl)
    }

    @Test
    fun onStart_ShouldDelegateToState() {
        stateContextImpl.onStart()

        verify(mockState).onStart(stateContextImpl)
    }

    @Test
    fun onCompleted_ShouldDelegateToState() {
        stateContextImpl.onCompleted()

        verify(mockState).onCompleted(stateContextImpl)
    }

    @Test
    fun onPlaybackReady_ShouldDelegateToState() {
        stateContextImpl.onPlaybackReady()

        verify(mockState).onPlaybackReady(stateContextImpl)
    }

    @Test
    fun onError_ShouldDelegateToState() {
        stateContextImpl.onError(0)

        verify(mockState).onError(stateContextImpl, 0)
    }

    @Test
    fun play_ShouldDelegateToState() {
        stateContextImpl.play()

        verify(mockState).play(stateContextImpl)
    }

    @Test
    fun onLoadingAnimationFinished_ShouldDelegateToState() {
        stateContextImpl.onLoadingAnimationFinished()

        verify(mockState).onLoadingAnimationFinished(stateContextImpl)
    }

    @Test
    fun onScreenModeChanged_ShouldDelegateToState() {
        stateContextImpl.onScreenModeChanged(ScreenMode.UNDEFINED)

        verify(mockState).onScreenModeChanged(stateContextImpl, ScreenMode.UNDEFINED)
    }
}