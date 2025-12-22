package com.orumi.pelongpelong.application.service.query

import com.orumi.pelongpelong.adapter.out.postgresql.StoryMapper
import com.orumi.pelongpelong.application.port.`in`.query.StoryQueryUseCase
import com.orumi.pelongpelong.application.port.out.StoryPort
import com.orumi.pelongpelong.domain.story.Story
import org.springframework.stereotype.Service

@Service
class StoryQueryService(
    private val storyPort: StoryPort
): StoryQueryUseCase {
    override fun findByLatBetweenAndLonBetweenOrderByScoreDesc(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<Story> {
        return storyPort.findByLatBetweenAndLonBetweenOrderByScoreDesc(minLat, maxLat, minLon, maxLon).map {
            StoryMapper.toDomain(it)
        }
    }
}
