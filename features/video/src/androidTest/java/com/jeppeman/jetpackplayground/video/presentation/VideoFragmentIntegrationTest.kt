package com.jeppeman.jetpackplayground.video.presentation

import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.presentation.detail.VideoDetailFragmentArgs
import com.jeppeman.jetpackplayground.video.presentation.espresso.isVisibleToUser
import com.jeppeman.jetpackplayground.video.test.TestActivity
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.fragment_video_list.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class VideoFragmentIntegrationTest {

    @Test
    fun clickVideoElement_shouldNavigateToVideoDetail() {
        val latch = CountDownLatch(2)
        ActivityScenario.launch(TestActivity::class.java).onActivity { activity ->
            val destinations = listOf(R.id.videoListFragment, R.id.videoDetailFragment)
            activity.videoList
                    ?.findNavController()
                    ?.addOnDestinationChangedListener { _, destination, arguments ->
                        assertThat(destination.id).isIn(destinations)
                        if (destination.id == R.id.videoDetailFragment) {
                            assertThat(
                                    VideoDetailFragmentArgs.fromBundle(arguments!!)
                                            .videoDetailParameter.videoModel.title
                            ).isEqualTo("Big Buck Bunny")
                        }
                        latch.countDown()
                    }
        }

        onView(withId(R.id.videoList))
                .check(matches(isVisibleToUser()))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withText("Big Buck Bunny"))
                .check(matches(isVisibleToUser()))
                .perform(click())
        latch.await(5, TimeUnit.SECONDS)
    }
}

@LargeTest
@RunWith(AndroidJUnit4::class)
class VideoFragmentE2ETest {

    @Test
    fun clickVideoElement_shouldMoveFromVideoListAndDisplayVideoDetail() {
        ActivityScenario.launch(TestActivity::class.java)

        onView(withId(R.id.videoList))
                .check(matches(isVisibleToUser()))
                .perform(scrollToPosition<RecyclerView.ViewHolder>(0))
        onView(withText("Big Buck Bunny"))
                .check(matches(isVisibleToUser()))
                .perform(click())
        onView(withId(R.id.videoList)).check(doesNotExist())
        onView(withId(R.id.mainAction)).check(matches(isVisibleToUser()))
    }
}
