package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.domain.food.Review

object ReviewMapper {
    fun toDomain(entity: ReviewEntity): Review =
        Review(
            id = entity.id,
            review = entity.review,
        )

    fun toDomainList(entities: List<ReviewEntity>): List<Review> =
        entities.map { toDomain(it) }

    fun toEntity(domain: Review): ReviewEntity =
        ReviewEntity(
            id = domain.id,
            review = domain.review,
        )

    fun toEntityList(domains: List<Review>): List<ReviewEntity> =
        domains.map { toEntity(it) }
}
