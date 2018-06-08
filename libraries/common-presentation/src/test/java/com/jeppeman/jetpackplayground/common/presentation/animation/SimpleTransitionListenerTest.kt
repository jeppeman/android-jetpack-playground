package com.jeppeman.jetpackplayground.common.presentation.animation

import androidx.constraintlayout.motion.widget.MotionLayout
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SimpleTransitionListenerTest {
    @Spy
    private lateinit var onChange: (MotionLayout?, Int, Int, Float) -> Unit
    @Spy
    private lateinit var onComplete: (MotionLayout?, Int) -> Unit
    private lateinit var simpleTransitionListener: SimpleTransitionListener

    @Before
    fun setUp() {
        simpleTransitionListener = SimpleTransitionListener(
                onChange = onChange,
                onComplete = onComplete
        )
    }

    @Test
    fun onChange_ShouldCallProvidedOnChange() {
        simpleTransitionListener.onTransitionChange(null, 0, 1, 0.5f)

        verify(onChange)(null, 0, 1, 0.5f)
    }

    @Test
    fun onComplete_ShouldCallProvidedOnComplete() {
        simpleTransitionListener.onTransitionCompleted(null, 1)

        verify(onComplete)(null, 1)
    }
}