package com.jeppeman.jetpackplayground.video.data.repository

import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.domain.Result
import com.jeppeman.jetpackplayground.video.data.entity.VideoCategoryEntity
import com.jeppeman.jetpackplayground.video.data.entity.VideoEntity
import com.jeppeman.jetpackplayground.video.data.entity.mapper.VideoEntityMapper
import com.jeppeman.jetpackplayground.video.data.entity.reponse.VideoResponseEntity
import com.jeppeman.jetpackplayground.video.data.net.VideoApi
import com.nhaarman.mockitokotlin2.whenever
import dagger.Lazy
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

@RunWith(MockitoJUnitRunner::class)
class VideoRepositoryImplTest {
    @Mock
    private lateinit var mockVideoApi: VideoApi
    @Mock
    private lateinit var mockVideoEntityMapper: VideoEntityMapper

    private lateinit var videoRepositoryImpl: VideoRepositoryImpl

    private val videoResponseEntity = VideoResponseEntity(
            categories = listOf(
                    VideoCategoryEntity(
                            name = "Fake videos",
                            videos = listOf(
                                    VideoEntity(
                                            title = "Fun title :)",
                                            subtitle = "Fun subtitle :)",
                                            sources = listOf("https://fun-video.com"),
                                            thumb = "https://fun-thumb.com"
                                    )
                            )
                    )
            )
    )

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    @Before
    fun setUp() {
        val testDispatcher = object : CoroutineDispatcher(), Delay {
            override fun dispatch(context: CoroutineContext, block: Runnable) {
                block.run()
            }

            override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
                continuation.resume(Unit)
            }

        }

        Dispatchers.setMain(testDispatcher)
        videoRepositoryImpl = VideoRepositoryImpl(Lazy { mockVideoApi }, mockVideoEntityMapper, Dispatchers.Main)
    }

    @Test
    fun getVideos_shouldMapToDomainAndPropagate() = runBlocking {
        whenever(mockVideoApi.getVideos()).thenReturn(videoResponseEntity)

        val videos = videoRepositoryImpl.getVideos()

        assertThat((videos as Result.Success).data).isNotEmpty()
    }
}