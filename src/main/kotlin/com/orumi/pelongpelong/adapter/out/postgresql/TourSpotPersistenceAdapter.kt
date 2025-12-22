package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.application.port.out.TourSpotPort
import org.springframework.stereotype.Component

@Component
class TourSpotPersistenceAdapter(
    private val jpaRepository: TourSpotJpaRepository
): TourSpotPort {
    override fun findByName(address: String): TourSpotEntity {
        return jpaRepository.findByName(address)
    }

    override fun findByNameContainingOrAddressContaining(name: String): List<TourSpotEntity>{
        return jpaRepository.findByNameContainingOrAddressContaining(name, name)
    }
}
