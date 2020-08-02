package com.jeppeman.jetpackplayground.video.presentation.widget

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.jeppeman.jetpackplayground.video_resources.R

class PlayButtonAnimationBehavior(
        private val target: ImageButton,
        private val replay: Drawable? = ContextCompat.getDrawable(target.context, R.drawable.ic_replay),
        private var playToPause: AnimatedVectorDrawable? =
                ContextCompat.getDrawable(target.context, R.drawable.ic_play_to_pause) as? AnimatedVectorDrawable,
        private var pauseToPlay: AnimatedVectorDrawable? =
                ContextCompat.getDrawable(target.context, R.drawable.ic_pause_to_play) as? AnimatedVectorDrawable
) {
    var state = State.PAUSED
        private set

    private val playToPauseCallback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            state = State.PLAYING
            target.setImageDrawable(pauseToPlay)
            playToPause?.unregisterAnimationCallback(this)
            playToPause?.stop()
            playToPause?.reset()
        }
    }
    private val pauseToPlayCallback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            state = State.PAUSED
            target.setImageDrawable(playToPause)
            pauseToPlay?.unregisterAnimationCallback(this)
            pauseToPlay?.stop()
            pauseToPlay?.reset()
        }
    }

    init {
        target.setImageDrawable(playToPause?.apply {
            stop()
            reset()
        })
    }

    fun markCompleted() {
        state = State.COMPLETED
        target.setImageDrawable(replay)
    }

    fun runPlayToPause() {
        state = State.PLAYING
        pauseToPlay?.unregisterAnimationCallback(pauseToPlayCallback)
        pauseToPlay?.stop()
        pauseToPlay?.reset()
        playToPause?.apply {
            target.setImageDrawable(this)
            registerAnimationCallback(playToPauseCallback)
            start()
        }
    }

    fun runPauseToPlay() {
        state = State.PAUSED
        playToPause?.unregisterAnimationCallback(playToPauseCallback)
        playToPause?.stop()
        playToPause?.reset()
        pauseToPlay?.apply {
            target.setImageDrawable(this)
            registerAnimationCallback(pauseToPlayCallback)
            start()
        }
    }

    enum class State {
        PLAYING, PAUSED, COMPLETED;
    }
}