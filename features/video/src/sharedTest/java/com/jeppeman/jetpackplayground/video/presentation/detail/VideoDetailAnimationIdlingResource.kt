package com.jeppeman.jetpackplayground.video.presentation.detail

import android.graphics.drawable.AnimatedVectorDrawable
import androidx.fragment.app.FragmentActivity
import androidx.test.espresso.IdlingResource
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import kotlinx.android.synthetic.main.fragment_video_detail.*

class VideoDetailAnimationIdlingResource : IdlingResource {
    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private var wasIdle = false

    override fun getName(): String = VideoDetailAnimationIdlingResource::class.java.name

    override fun isIdleNow(): Boolean {
        val fragment = (ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
                .firstOrNull() as FragmentActivity?)
                ?.supportFragmentManager
                ?.fragments
                ?.asSequence()
                ?.filterIsInstance(VideoDetailFragment::class.java)
                ?.firstOrNull() ?: return true

        val mainActionIsAnimating = (fragment.mainAction?.drawable as? AnimatedVectorDrawable)?.isRunning
        val fullscreenPlayIsAnimating = (fragment.mainAction?.drawable as? AnimatedVectorDrawable)?.isRunning
        val volumeButtonIsAnimating = (fragment.mainAction?.drawable as? AnimatedVectorDrawable)?.isRunning
        val arcViewAnimating = fragment.arc?.isAnimating
        val idle = mainActionIsAnimating != true
                && fullscreenPlayIsAnimating != true
                && volumeButtonIsAnimating != true
                && arcViewAnimating != true

        if (idle) {
            wasIdle = true
            resourceCallback?.onTransitionToIdle()
        }

        return idle
    }

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        resourceCallback = callback
    }
}