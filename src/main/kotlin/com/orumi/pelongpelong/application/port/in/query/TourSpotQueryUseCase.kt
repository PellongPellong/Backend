package com.orumi.pelongpelong.application.port.`in`.query

import com.orumi.pelongpelong.domain.story.TourSpot

interface TourSpotQueryUseCase {
    fun findByName(address: String): TourSpot

    fun findByNameContainingOrAddressContaining(name: String): List<TourSpot>
}
