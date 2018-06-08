package com.jeppeman.jetpackplayground.video.data.entity.mapper

import com.jeppeman.jetpackplayground.common.data.EntityMapper
import com.jeppeman.jetpackplayground.video.data.di.VideoApiBaseUrl
import com.jeppeman.jetpackplayground.video.data.entity.VideoEntity
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.platform.di.VideoScope
import javax.inject.Inject

@VideoScope
class VideoEntityMapper @Inject constructor(
        @VideoApiBaseUrl private val videoApiBaseUrl: String
) : EntityMapper<VideoEntity, Video> {
    override fun toEntity(from: Video): VideoEntity {
        return VideoEntity(
                title = from.title,
                subtitle = from.subtitle,
                thumb = from.thumb,
                sources = listOf(from.source)
        )
    }

    override fun toDomain(from: VideoEntity): Video {
        return Video(
                title = requireNotNull(from.title),
                subtitle = requireNotNull(from.subtitle).let { s -> "$s\n\n$s\n\n$s" },
                thumb = "$videoApiBaseUrl${from.thumb}",
                source = requireNotNull(from.sources?.firstOrNull())
        )
    }
}