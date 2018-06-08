package com.jeppeman.jetpackplayground.video.presentation.shadows

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build.VERSION_CODES.N
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.annotation.RealObject
import org.robolectric.shadows.ShadowVectorDrawable

@Implements(AnimatedVectorDrawable::class, minSdk = N)
class ShadowAnimatedVectorDrawable : ShadowVectorDrawable() {
    @RealObject
    private lateinit var realDrawable: AnimatedVectorDrawable
    private val animationListeners = mutableListOf<Animatable2.AnimationCallback>()

    @Implementation
    fun start() {
        animationListeners.forEach { listener ->
            listener.onAnimationStart(realDrawable)
            listener.onAnimationEnd(realDrawable)
        }
    }

    @Implementation
    fun registerAnimationCallback(callback: Animatable2.AnimationCallback) {
        animationListeners.add(callback)
    }
}