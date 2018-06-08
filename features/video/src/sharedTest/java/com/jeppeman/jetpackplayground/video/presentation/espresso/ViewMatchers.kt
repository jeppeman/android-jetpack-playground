package com.jeppeman.jetpackplayground.video.presentation.espresso

import android.view.View
import android.widget.ProgressBar
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.jeppeman.jetpackplayground.video.presentation.widget.ArcView
import com.jeppeman.jetpackplayground.video.presentation.widget.PlayButton
import com.jeppeman.jetpackplayground.video.presentation.widget.PlayButtonAnimationBehavior
import com.jeppeman.jetpackplayground.video.presentation.widget.PlayButtonFab
import com.jeppeman.jetpackplayground.video.presentation.widget.ToggleSoundButton
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class IsVisibleToUserMatcher : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("is _actually_ displayed on the screen to the user")
    }

    override fun matchesSafely(item: View?): Boolean {
        return isDisplayed().matches(item)
                && item?.alpha ?: 0f > 0
                && item?.scaleX ?: 0f > 0
                && item?.scaleY ?: 0f > 0
                && (item !is ArcView
                || item.loading
                || item.noseLength > 0
                || item.tailLength > 0
                || item.sweepAngle > 0)
    }
}

class AnimationStateMatcher(private val state: PlayButtonAnimationBehavior.State)
    : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("is matching animation state $state")
    }

    override fun matchesSafely(item: View?): Boolean {
        return (item is PlayButton && item.state == state)
                || (item is PlayButtonFab && item.state == state)
    }
}


class SoundStateMatcher(private val soundState: ToggleSoundButton.SoundState)
    : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("has sound state $soundState")
    }

    override fun matchesSafely(item: View?): Boolean {
        return item is ToggleSoundButton && item.soundState == soundState
    }
}

class IsLoadingMatcher : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("is loading")
    }

    override fun matchesSafely(item: View?): Boolean {
        return (item is ProgressBar && item.isAnimating)
                || (item is ArcView && item.loading)
    }
}

class TitleMatcher(private val title: String?) : TypeSafeMatcher<View>() {
    override fun describeTo(description: Description?) {
        description?.appendText("has title $title")
    }

    override fun matchesSafely(item: View?): Boolean {
        return item is CollapsingToolbarLayout && item.title == title
    }
}

fun isVisibleToUser() = IsVisibleToUserMatcher()
fun hasAnimationState(state: PlayButtonAnimationBehavior.State) = AnimationStateMatcher(state)
fun hasSoundState(state: ToggleSoundButton.SoundState) = SoundStateMatcher(state)
fun isLoading() = IsLoadingMatcher()
fun hasTitle(title: String?) = TitleMatcher(title)