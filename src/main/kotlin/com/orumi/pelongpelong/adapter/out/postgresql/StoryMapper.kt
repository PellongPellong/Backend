package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.domain.story.Story

object StoryMapper {
    fun toDomain(entity: StoryEntity): Story =
        Story(
            id = entity.id,
            storyId = entity.storyId,
            placeName = entity.placeName,
            lat = entity.lat,
            lon = entity.lon,
            score = entity.score,
            story = entity.story,
            category1 = entity.category1,
            category2 = entity.category2,
            category3 = entity.category3,
        )
}
