package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.application.port.out.StoryPort
import org.springframework.stereotype.Component

@Component
class StoryPersistenceAdapter(
    private val jpaRepository: StoryJpaRepository
): StoryPort {
    override fun findByLatBetweenAndLonBetweenOrderByScoreDesc(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<StoryEntity> {
        return jpaRepository.findByLatBetweenAndLonBetweenOrderByScoreDesc(minLat, maxLat, minLon, maxLon)
    }
}
