package com.jeppeman.jetpackplayground.video.presentation.widget

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.video.presentation.util.getProperty
import com.jeppeman.jetpackplayground.video.presentation.util.setProperty
import com.jeppeman.jetpackplayground.video.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class PlayButtonAnimationBehaviorTest {

    private lateinit var playButtonAnimationBehavior: PlayButtonAnimationBehavior
    private lateinit var imageButton: ImageButton
    private lateinit var playToPause: AnimatedVectorDrawable
    private lateinit var pauseToPlay: AnimatedVectorDrawable
    private lateinit var replay: Drawable

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        imageButton = ImageButton(context)
        playToPause = spy(ContextCompat.getDrawable(context, R.drawable.ic_play_to_pause) as AnimatedVectorDrawable)
        pauseToPlay = spy(ContextCompat.getDrawable(context, R.drawable.ic_pause_to_play) as AnimatedVectorDrawable)
        replay = spy(ContextCompat.getDrawable(context, R.drawable.ic_replay) as Drawable)
        playButtonAnimationBehavior = PlayButtonAnimationBehavior(
                target = imageButton,
                replay = replay,
                playToPause = playToPause,
                pauseToPlay = pauseToPlay
        )
    }

    @Test
    fun markCompleted_ShouldSetReplayIconAndChangeStateToCompleted() {
        var state = playButtonAnimationBehavior.getProperty<PlayButtonAnimationBehavior.State>("state")
        assertThat(state).isNotEqualTo(PlayButtonAnimationBehavior.State.COMPLETED)
        assertThat(imageButton.drawable).isNotEqualTo(replay)

        playButtonAnimationBehavior.markCompleted()

        state = playButtonAnimationBehavior.getProperty("state")
        assertThat(state).isEqualTo(PlayButtonAnimationBehavior.State.COMPLETED)
        assertThat(imageButton.drawable).isEqualTo(replay)
    }

    @Test
    fun runPauseToPlay_ShouldSetDrawableAndPlayAnimationAndSetStateToPlayingOnCompletion() {
        val callback = playButtonAnimationBehavior.getProperty<Animatable2.AnimationCallback>("pauseToPlayCallback")
        playButtonAnimationBehavior.setProperty("state", PlayButtonAnimationBehavior.State.PLAYING)
        var state = playButtonAnimationBehavior.getProperty<PlayButtonAnimationBehavior.State>("state")
        assertThat(state).isEqualTo(PlayButtonAnimationBehavior.State.PLAYING)

        playButtonAnimationBehavior.runPauseToPlay()

        state = playButtonAnimationBehavior.getProperty("state")
        assertThat(state).isEqualTo(PlayButtonAnimationBehavior.State.PAUSED)
        assertThat(imageButton.drawable).isEqualTo(playToPause)
        verify(pauseToPlay).unregisterAnimationCallback(callback)
    }

    @Test
    fun runPlayToPause_ShouldSetDrawableAndPlayAnimationAndSetStateToPausedOnCompletion() {
        val callback = playButtonAnimationBehavior.getProperty<Animatable2.AnimationCallback>("playToPauseCallback")
        var state = playButtonAnimationBehavior.getProperty<PlayButtonAnimationBehavior.State>("state")
        assertThat(state).isEqualTo(PlayButtonAnimationBehavior.State.PAUSED)

        playButtonAnimationBehavior.runPlayToPause()

        state = playButtonAnimationBehavior.getProperty("state")
        assertThat(state).isEqualTo(PlayButtonAnimationBehavior.State.PLAYING)
        assertThat(imageButton.drawable).isEqualTo(pauseToPlay)
        verify(playToPause).unregisterAnimationCallback(callback)
    }
}