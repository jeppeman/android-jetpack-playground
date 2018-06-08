package com.jeppeman.jetpackplayground.common.presentation.extensions

import android.content.Context
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun findViewWithTransitionName_shouldTraverseViewTreeAndFindViewIfPresent() {
        val transitionNameTotoro = "Totoro"
        val root = FrameLayout(context).apply {
            addView(
                    FrameLayout(context).apply {
                        addView(
                                FrameLayout(context).apply {
                                    listOf(
                                            TextView(context),
                                            ImageView(context).apply { transitionName = transitionNameTotoro },
                                            Button(context)
                                    ).forEach { v -> addView(v) }
                                }
                        )
                    }
            )
        }

        assertThat(root.findViewWithTransitionName(transitionNameTotoro)).isInstanceOf(ImageView::class.java)
    }
}