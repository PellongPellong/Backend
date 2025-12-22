package com.orumi.pelongpelong.application.port.`in`.query

import com.orumi.pelongpelong.domain.story.Story

interface StoryQueryUseCase {
    fun findByLatBetweenAndLonBetweenOrderByScoreDesc(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<Story>
}
