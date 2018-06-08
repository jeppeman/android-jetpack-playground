package com.jeppeman.jetpackplayground.video.presentation.espresso

import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.Matcher

class SeekToAction(private val value: Int) : ViewAction {
    override fun getDescription() = "seek to $value"

    override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(SeekBar::class.java)
    }

    override fun perform(uiController: UiController?, view: View?) {
        (view as SeekBar).progress = value
    }
}

fun seekTo(value: Int) = SeekToAction(value)