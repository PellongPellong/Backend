package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.domain.food.Review

object ReviewMapper {
    fun toEntity(domain: Review): ReviewEntity =
        ReviewEntity(
            id = domain.id,
            food = FoodMapper.toEntity(domain.food),
            review = domain.review,
        )

    fun toDomain(entity: ReviewEntity): Review =
        Review(
            id = entity.id,
            food = FoodMapper.toDomain(entity.food),
            review = entity.review,
        )
}
