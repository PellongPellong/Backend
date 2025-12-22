package com.orumi.pelongpelong.adapter.out.postgresql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TourSpotJpaRepository : JpaRepository<TourSpotEntity, Long> {
    fun findByName(address: String): TourSpotEntity
}
