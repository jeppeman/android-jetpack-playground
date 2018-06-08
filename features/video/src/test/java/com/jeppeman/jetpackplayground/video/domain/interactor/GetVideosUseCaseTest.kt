package com.jeppeman.jetpackplayground.video.domain.interactor

import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.domain.Result
import com.jeppeman.jetpackplayground.domain.asResult
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.domain.repository.VideoRepository
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetVideosUseCaseTest {
    @Mock
    private lateinit var mockVideoRepository: VideoRepository
    private lateinit var useCase: GetVideosUseCase

    private val videos = listOf(
            Video(
                    title = "Fun title :)",
                    subtitle = "Fun subtitle :)",
                    source = "https://fun-video.com",
                    thumb = "https://fun-thumb.com"
            )
    )

    @Before
    fun setUp() {
        useCase = GetVideosUseCase(mockVideoRepository)
    }

    @Test
    fun getVideos_shouldPropagate() = runBlocking {
        whenever(mockVideoRepository.getVideos()).thenReturn(videos.asResult())

        val videos = useCase.execute()

        assertThat((videos as Result.Success).data).isNotEmpty()
    }
}