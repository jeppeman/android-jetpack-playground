package com.jeppeman.jetpackplayground.video.presentation.shadows

import androidx.constraintlayout.motion.widget.MotionLayout
import com.jeppeman.jetpackplayground.video.presentation.util.invokeMethod
import com.jeppeman.jetpackplayground.video.presentation.util.setProperty
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadow.api.Shadow.directlyOn
import org.robolectric.shadows.ShadowViewGroup

@Implements(MotionLayout::class)
class ShadowMotionLayout : ShadowViewGroup(), MotionLayout.TransitionListener {
    @RealObject
    private lateinit var realMotionLayout: MotionLayout
    private var onCompleteCalled = false
    private var transitionListener: MotionLayout.TransitionListener? = null

    @Implementation
    override fun invalidate() {
        super.invalidate()
        realMotionLayout.setProperty("mTransitionInstantly", true)
        realMotionLayout.invokeMethod("evaluate", true)
    }

    @Implementation
    fun setTransitionListener(listener: MotionLayout.TransitionListener) {
        onCompleteCalled = false
        transitionListener = listener
        directlyOn(realMotionLayout, MotionLayout::class.java).setTransitionListener(this)
    }

    override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
        transitionListener?.onTransitionChange(layout, startId, endId, progress)
    }

    override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
        if (!onCompleteCalled) {
            onCompleteCalled = true
            transitionListener?.onTransitionCompleted(layout, currentId)
        }
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
    }
}