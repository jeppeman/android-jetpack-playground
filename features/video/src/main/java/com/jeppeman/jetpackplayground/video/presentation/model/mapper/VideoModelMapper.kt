package com.jeppeman.jetpackplayground.video.presentation.model.mapper

import com.jeppeman.jetpackplayground.common.presentation.ModelMapper
import com.jeppeman.jetpackplayground.video.domain.model.Video
import com.jeppeman.jetpackplayground.video.presentation.list.items.item.VideoListItemViewModel
import com.jeppeman.jetpackplayground.video.presentation.list.VideoListViewModel
import com.jeppeman.jetpackplayground.video.presentation.model.VideoModel

class VideoModelMapper : ModelMapper<VideoModel, Video> {
    override fun toModel(from: Video): VideoModel {
        return VideoModel(
                title = from.title,
                subtitle = from.subtitle,
                thumb = from.thumb,
                source = from.source
        )
    }

    override fun toDomain(from: VideoModel): Video {
        return Video(
                title = from.title,
                subtitle = from.subtitle,
                thumb = from.thumb,
                source = from.source
        )
    }
}