package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.domain.story.TourSpot

object TourSpotMapper {
    fun toDomain(entity: TourSpotEntity): TourSpot =
        TourSpot(
            id = entity.id,
            name = entity.name,
            address = entity.address,
            latitude = entity.latitude,
            longitude = entity.longitude,
            place = entity.place,
        )
}
