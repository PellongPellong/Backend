package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.domain.food.Food

object FoodMapper {
    fun toEntity(domain: Food): FoodEntity {
        val entity = FoodEntity(
            id = domain.id,
            foodId = domain.foodId,
            name = domain.name,
            address = domain.address,
            latitude = domain.latitude,
            longitude = domain.longitude,
            categoryLarge = domain.categoryLarge,
            categoryMiddle = domain.categoryMiddle,
            categorySmall = domain.categorySmall,
            rating = domain.rating,
            reviews = mutableListOf()
        )

        domain.reviews.forEach { reviewDomain ->
            val reviewEntity = ReviewMapper.toEntity(reviewDomain)
            entity.addReview(reviewEntity)
        }

        return entity
    }

    fun toDomain(entity: FoodEntity): Food =
        Food(
            id = entity.id,
            foodId = entity.foodId,
            name = entity.name,
            address = entity.address,
            latitude = entity.latitude,
            longitude = entity.longitude,
            categoryLarge = entity.categoryLarge,
            categoryMiddle = entity.categoryMiddle,
            categorySmall = entity.categorySmall,
            rating = entity.rating,
            reviews = ReviewMapper.toDomainList(entity.reviews)
        )
}
