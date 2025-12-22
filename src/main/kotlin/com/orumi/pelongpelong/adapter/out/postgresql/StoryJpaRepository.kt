package com.orumi.pelongpelong.adapter.out.postgresql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StoryJpaRepository : JpaRepository<StoryEntity, Long> {
    fun findByLatBetweenAndLonBetweenOrderByScoreDesc(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<StoryEntity>
}
