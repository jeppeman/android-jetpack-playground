package com.jeppeman.jetpackplayground.video.presentation.detail

import android.content.Context
import android.os.Handler
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.video.presentation.util.getProperty
import com.jeppeman.jetpackplayground.video.presentation.util.invokeMethod
import com.jeppeman.jetpackplayground.video.R
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@Ignore("Binary resources are broken when running tests in a dynamic feature module")
class VideoDetailPlayerImplTest {
    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()
    private lateinit var videoDetailPlayer: VideoDetailPlayerImpl
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var context: Context
    private lateinit var handler: Handler
    private lateinit var testMediaSource: MediaSource

    private fun preparePlayer() {
        exoPlayer.prepare(testMediaSource)
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
        handler = Handler()
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context)
        val defaultDataSource = DefaultDataSourceFactory(context, "app")
        val mediaSourceFactory = ProgressiveMediaSource.Factory(defaultDataSource)
        videoDetailPlayer = VideoDetailPlayerImpl(
                handler = handler,
                exoPlayer = exoPlayer,
                mediaSourceFactory = mediaSourceFactory
        )
        val rawDataSource = RawResourceDataSource(context)
        rawDataSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.test_video)))

        testMediaSource = ProgressiveMediaSource.Factory(defaultDataSource).createMediaSource(rawDataSource.uri)
    }

    @Test
    fun setVideoUrl_shouldCallPrepare() {
        exceptionRule.expect(IOException::class.java)

        videoDetailPlayer.videoUrl = "This url has no chance of working"

        verify(videoDetailPlayer).invokeMethod("prepare")
    }

    @Test
    fun exoPlayerPrepared_shouldCallOnReadyListenersAndSetStateToReady() {
        var didInvokeCallback = false
        val onPlaybackReadyListener = { didInvokeCallback = true }
        videoDetailPlayer.registerPlaybackReadyListener(onPlaybackReadyListener)
        assertThat(videoDetailPlayer.isReady).isFalse()

        preparePlayer()

        assertThat(didInvokeCallback).isTrue()
        assertThat(videoDetailPlayer.isReady).isTrue()
    }

    @Test
    fun exoPlayerCompleted_shouldCallOnCompletedListenersAndSetStateToNotPlaying() {
        var didInvokeCallback = false
        val onCompletedListener = { didInvokeCallback = true }
        videoDetailPlayer.registerCompletionListener(onCompletedListener)
        preparePlayer()

        videoDetailPlayer.start()
        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isTrue()
        exoPlayer.seekTo(exoPlayer.duration)

        assertThat(didInvokeCallback).isTrue()
        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isFalse()
    }

    @Test
    fun start_shouldStartexoPlayerAndNotifyProgression() {
        var didInvokeCallback = false
        val progressListener = { _: Long -> didInvokeCallback = true }
        videoDetailPlayer.registerProgressListener(progressListener)
        preparePlayer()

        videoDetailPlayer.start()

        assertThat(exoPlayer.isPlaying).isTrue()
        assertThat(didInvokeCallback).isTrue()
    }

    @Test
    fun pause_shouldStopPlayback() {
        preparePlayer()

        videoDetailPlayer.start()
        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isTrue()
        assertThat(exoPlayer.isPlaying).isTrue()
        videoDetailPlayer.pause()

        assertThat(videoDetailPlayer.getProperty<Boolean>("isPlaying")).isFalse()
        assertThat(exoPlayer.isPlaying).isFalse()
    }

    @Test
    fun restart_shouldResetToBeginning() {
        preparePlayer()

        videoDetailPlayer.start()
        exoPlayer.seekTo(500)
        assertThat(exoPlayer.currentPosition).isEqualTo(500)
        videoDetailPlayer.restart()

        assertThat(exoPlayer.currentPosition).isEqualTo(0)
    }

    @Test
    fun fastForward_shouldAdvancePlayer() {
        preparePlayer()

        videoDetailPlayer.start()
        val progressBefore = exoPlayer.currentPosition
        videoDetailPlayer.fastForward()

        assertThat(progressBefore).isLessThan(exoPlayer.currentPosition)
    }

    @Test
    fun rewind_shouldRetractPlayer() {
        preparePlayer()

        videoDetailPlayer.start()
        videoDetailPlayer.fastForward()
        val progressBefore = exoPlayer.currentPosition
        videoDetailPlayer.rewind()

        assertThat(progressBefore).isGreaterThan(exoPlayer.currentPosition)
    }
}