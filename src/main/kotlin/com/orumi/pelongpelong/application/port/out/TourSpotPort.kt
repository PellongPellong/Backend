package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.postgresql.TourSpotEntity

interface TourSpotPort {
    fun findByName(address: String): TourSpotEntity

    fun findByNameContainingOrAddressContaining(name: String): List<TourSpotEntity>

    fun findTop1ByPlace(place: String): TourSpotEntity
}
