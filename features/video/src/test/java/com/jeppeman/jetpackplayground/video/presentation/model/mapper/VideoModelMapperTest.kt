package com.jeppeman.jetpackplayground.video.presentation.model.mapper

import com.google.common.truth.Truth.assertThat
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel
import org.junit.Test

class VideoModelMapperTest {
    private val videoModelMapper = VideoModelMapper()

    @Test
    fun mapToDomain_ShouldMaintainDataConsistency() {
        val entity = VideoModel(
                title = "Cool title",
                subtitle = "Cool subtitle",
                source = "https://cool-movie.com",
                thumb = "https://cool-movie-thumb.com"
        )

        val domainObject = videoModelMapper.toDomain(entity)

        domainObject.apply {
            assertThat(entity.title).isEqualTo(title)
            assertThat(entity.subtitle).isEqualTo(subtitle)
            assertThat(entity.thumb).isEqualTo(thumb)
            assertThat(entity.source).isEqualTo(source)
        }
    }

    @Test
    fun mapToModel_ShouldMaintainDataConsistency() {
        val domainObject = Video(
                title = "Cool title",
                subtitle = "Cool subtitle",
                source = "https://cool-movie.com",
                thumb = "https://cool-movie-thumb.com"
        )

        val entity = videoModelMapper.toModel(domainObject)

        entity.apply {
            assertThat(domainObject.title).isEqualTo(title)
            assertThat(domainObject.subtitle).isEqualTo(subtitle)
            assertThat(domainObject.thumb).isEqualTo(thumb)
            assertThat(domainObject.source).isEqualTo(source)
        }
    }
}