package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.postgresql.StoryEntity

interface StoryPort {
    fun findByLatBetweenAndLonBetweenOrderByScoreDesc(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<StoryEntity>
}
