package com.orumi.pelongpelong.application.service.query

import com.orumi.pelongpelong.adapter.out.postgresql.TourSpotMapper
import com.orumi.pelongpelong.application.port.`in`.query.TourSpotQueryUseCase
import com.orumi.pelongpelong.application.port.out.TourSpotPort
import com.orumi.pelongpelong.domain.story.TourSpot
import org.springframework.stereotype.Service

@Service
class TourSpotQueryService(
    private val tourSpotPort: TourSpotPort
): TourSpotQueryUseCase {
    override fun findByName(address: String): TourSpot {
        return TourSpotMapper.toDomain(tourSpotPort.findByName(address))
    }

    override fun findTop1ByPlace(place: String): TourSpot {
        return TourSpotMapper.toDomain(tourSpotPort.findTop1ByPlace(place))
    }
}
