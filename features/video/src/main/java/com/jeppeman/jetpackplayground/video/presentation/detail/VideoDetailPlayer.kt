package com.jeppeman.jetpackplayground.video.presentation.detail

import android.view.Surface
import com.google.android.exoplayer2.ui.PlayerView

interface VideoDetailPlayer {
    var videoUrl: String
    val isReady: Boolean
    var progress: Long
    val duration: Long
    fun mute()
    fun unmute()
    fun registerProgressListener(onProgressListener: (Long) -> Unit)
    fun unregisterProgressListener(onProgressListener: (Long) -> Unit)
    fun registerErrorListener(onErrorListener: (Int?) -> Unit)
    fun registerPlaybackReadyListener(onPlaybackReadyListener: () -> Unit)
    fun registerCompletionListener(onCompletedListener: () -> Unit)
    fun rewind()
    fun fastForward()
    fun pause()
    fun start()
    fun restart()
    fun reset()
    fun release()
    fun attachSurface(surface: Surface)
}