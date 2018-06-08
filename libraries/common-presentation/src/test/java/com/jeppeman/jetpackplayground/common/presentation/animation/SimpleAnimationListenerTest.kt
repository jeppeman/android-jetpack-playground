package com.jeppeman.jetpackplayground.common.presentation.animation

import android.view.animation.Animation
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SimpleAnimationListenerTest {
    @Spy
    private lateinit var onStart: (Animation?) -> Unit
    @Spy
    private lateinit var onRepeat: (Animation?) -> Unit
    @Spy
    private lateinit var onEnd: (Animation?) -> Unit
    private lateinit var simpleAnimationListener: SimpleAnimationListener

    @Before
    fun setUp() {
        simpleAnimationListener = SimpleAnimationListener(
                onStart = onStart,
                onRepeat = onRepeat,
                onEnd = onEnd
        )
    }

    @Test
    fun onStart_ShouldCallProvidedOnStart() {
        simpleAnimationListener.onAnimationStart(null)

        verify(onStart)(any())
    }

    @Test
    fun onRepeat_ShouldCallProvidedOnRepeat() {
        simpleAnimationListener.onAnimationRepeat(null)

        verify(onRepeat)(any())
    }

    @Test
    fun onEnd_ShouldCallProvidedOnEnd() {
        simpleAnimationListener.onAnimationEnd(null)

        verify(onEnd)(any())
    }
}