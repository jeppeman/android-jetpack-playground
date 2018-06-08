package com.jeppeman.jetpackplayground.video.presentation.widget

import android.content.Context
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.jeppeman.jetpackplayground.video.R

class ToggleSoundButton(context: Context, attributeSet: AttributeSet) : AppCompatImageButton(context, attributeSet) {
    private val onToOff: AnimatedVectorDrawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_volume_on_to_off) as? AnimatedVectorDrawable
    private val offToOn: AnimatedVectorDrawable? =
            ContextCompat.getDrawable(context, R.drawable.ic_volume_off_to_on) as? AnimatedVectorDrawable

    var isAnimating = false
        private set
    var soundState = SoundState.ON
        set(value) {
            if (field != value) {
                field = value
                setIcon()
            }
        }

    init {
        setIcon()
    }

    private fun runOffToOn() {
        offToOn?.apply {
            isAnimating = true
            setImageDrawable(this)
            registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    isAnimating = false
                    setImageDrawable(onToOff)
                    unregisterAnimationCallback(this)
                    stop()
                    reset()
                }
            })
            start()
        }
    }

    private fun runOnToOff() {
        onToOff?.apply {
            isAnimating = true
            setImageDrawable(this)
            registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    isAnimating = false
                    setImageDrawable(offToOn)
                    unregisterAnimationCallback(this)
                    stop()
                    reset()
                }
            })
            start()
        }
    }

    private fun setIcon() {
        if (soundState == SoundState.ON) {
            runOffToOn()
        } else {
            runOnToOff()
        }
    }

    enum class SoundState {
        ON, OFF
    }
}

fun Boolean.asSoundState() = if (this) {
    ToggleSoundButton.SoundState.ON
} else {
    ToggleSoundButton.SoundState.OFF
}