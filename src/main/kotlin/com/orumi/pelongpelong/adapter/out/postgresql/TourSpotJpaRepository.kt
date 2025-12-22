package com.orumi.pelongpelong.adapter.out.postgresql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TourSpotJpaRepository : JpaRepository<TourSpotEntity, Long> {
    fun findByName(name: String): TourSpotEntity

    fun findByNameContainingOrAddressContaining(name: String, addr: String): List<TourSpotEntity>

    fun findTop1ByPlace(place: String): TourSpotEntity
}
