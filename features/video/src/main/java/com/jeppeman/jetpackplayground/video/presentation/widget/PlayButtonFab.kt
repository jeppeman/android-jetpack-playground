package com.jeppeman.jetpackplayground.video.presentation.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Wrapper for [FloatingActionButton], this enables animating the background tint with
 * [MotionLayout]
 */
class PlayButtonFab(context: Context, attributeSet: AttributeSet) : FloatingActionButton(context, attributeSet) {
    private val behavior = PlayButtonAnimationBehavior(this)

    var backgroundTint: Int = Color.TRANSPARENT
        set(value) {
            field = value
            backgroundTintList = ColorStateList.valueOf(field)
        }

    val state get() = behavior.state

    fun runPauseToPlay() {
        behavior.runPauseToPlay()
    }

    fun runPlayToPause() {
        behavior.runPlayToPause()
    }

    fun markCompleted() = behavior.markCompleted()
}