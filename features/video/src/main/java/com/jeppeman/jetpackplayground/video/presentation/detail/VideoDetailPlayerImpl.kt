package com.jeppeman.jetpackplayground.video.presentation.detail

import android.os.Handler
import android.util.Log
import android.view.Surface
import androidx.core.net.toUri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MediaSourceEventListener
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

/**
 * Wrapper for a {@link MediaPlayer}
 */
class VideoDetailPlayerImpl @Inject constructor(
        private val exoPlayer: SimpleExoPlayer,
        private val mediaSourceFactory: ProgressiveMediaSource.Factory,
        private val handler: Handler
) : VideoDetailPlayer, Player.EventListener {

    private var isPlaying = false
    private val progressListeners = mutableListOf<(Long) -> Unit>()
    private val playbackReadyListeners = mutableListOf<() -> Unit>()
    private val completedListeners = mutableListOf<() -> Unit>()
    private val errorListeners = mutableListOf<(Int?) -> Unit>()

    init {
        exoPlayer.addListener(this)
        exoPlayer.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
        exoPlayer.playWhenReady = false
    }

    override var videoUrl: String = ""
        set(value) {
            field = value
            prepare()
        }

    override var isReady = false
        private set

    override var progress: Long = 0
        set(value) {
            field = value
            exoPlayer.seekTo(value)
        }

    override val duration get() = exoPlayer.duration

    private fun prepare() {
        isReady = false
        val mediaSource = mediaSourceFactory.createMediaSource(videoUrl.toUri())
        exoPlayer.prepare(mediaSource)
    }

    private fun progressTick() {
        if (!isPlaying) {
            return
        }

        progressListeners.forEach { listener ->
            listener(exoPlayer.currentPosition)
        }

        handler.postDelayed(::progressTick, 30)
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                isReady = true
                playbackReadyListeners.forEach { listener -> listener() }
            }
            Player.STATE_ENDED -> {
                completedListeners.forEach { onCompletedListener -> onCompletedListener() }
            }
        }
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        if (error?.cause is IllegalStateException && isPlaying) {
            val progress = exoPlayer.currentPosition
            videoUrl = videoUrl
            exoPlayer.seekTo(progress)
        }
        errorListeners.forEach { errorListener -> errorListener(error?.type) }
    }

    override fun mute() {
        exoPlayer.volume = 0f
    }

    override fun unmute() {
        exoPlayer.volume = 1f
    }

    override fun registerProgressListener(onProgressListener: (Long) -> Unit) {
        progressListeners.add(onProgressListener)
    }

    override fun registerErrorListener(onErrorListener: (Int?) -> Unit) {
        errorListeners.add(onErrorListener)
    }

    override fun registerPlaybackReadyListener(onPlaybackReadyListener: () -> Unit) {
        playbackReadyListeners.add(onPlaybackReadyListener)
    }

    override fun registerCompletionListener(onCompletedListener: () -> Unit) {
        completedListeners.add(onCompletedListener)
    }

    override fun rewind() {
        exoPlayer.seekTo(0L.coerceAtLeast((exoPlayer.currentPosition - exoPlayer.duration * 0.1).toLong()))
    }

    override fun fastForward() {
        exoPlayer.seekTo(exoPlayer.duration.coerceAtMost((exoPlayer.currentPosition + exoPlayer.duration * 0.1).toLong()))
    }

    override fun pause() {
        isPlaying = false
        exoPlayer.playWhenReady = false
    }

    override fun start() {
        isPlaying = true
        exoPlayer.playWhenReady = true
        progressTick()
    }

    override fun restart() {
        exoPlayer.seekTo(0)
    }

    override fun reset() {
        pause()
        restart()
        prepare()
    }

    override fun release() {
        pause()
        handler.removeCallbacksAndMessages(null)
        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun attachSurface(surface: Surface) {
        exoPlayer.setVideoSurface(surface)
    }
}