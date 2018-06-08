package com.jeppeman.jetpackplayground.video.data.entity.mapper

import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.video.domain.model.Video
import org.junit.Test

class VideoEntityMapperTest {

    private val fakeApiBaseUrl = "http://something.com/"

    private val videoEntityMapper = VideoEntityMapper(fakeApiBaseUrl)

    @Test
    fun mapToDomain_ShouldMaintainDataConsistency() {
        val entity = com.jeppeman.jetpackplayground.video.data.entity.VideoEntity(
                title = "Cool title",
                subtitle = "Cool subtitle",
                sources = listOf("https://cool-movie.com"),
                thumb = "https://cool-movie-thumb.com"
        )

        val domainObject = videoEntityMapper.toDomain(entity)

        domainObject.apply {
            assertThat(entity.title).isEqualTo(title)
            assertThat("${entity.subtitle}\n\n${entity.subtitle}\n\n${entity.subtitle}").isEqualTo(subtitle)
            assertThat("$fakeApiBaseUrl${entity.thumb}").isEqualTo(thumb)
            assertThat(entity.sources?.first()).isEqualTo(source)
        }
    }

    @Test
    fun mapToEntity_ShouldMaintainDataConsistency() {
        val domainObject = Video(
                title = "Cool title",
                subtitle = "Cool subtitle",
                source = "https://cool-movie.com",
                thumb = "https://cool-movie-thumb.com"
        )

        val entity = videoEntityMapper.toEntity(domainObject)

        entity.apply {
            assertThat(domainObject.title).isEqualTo(title)
            assertThat(domainObject.subtitle).isEqualTo(subtitle)
            assertThat(domainObject.thumb).isEqualTo(thumb)
            assertThat(domainObject.source).isEqualTo(sources?.first())
        }
    }
}