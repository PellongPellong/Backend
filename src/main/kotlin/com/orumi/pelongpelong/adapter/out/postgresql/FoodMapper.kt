package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.domain.food.Food

object FoodMapper {
    fun toRow(domain: Food): FoodRow =
        FoodRow(
            id = domain.id,
            foodId = domain.foodId,
            name = domain.name,
            address = domain.address,
            latitude = domain.latitude,
            longitude = domain.longitude,
            categoryLarge = domain.categoryLarge,
            categoryMiddle = domain.categoryMiddle,
            categorySmall = domain.categorySmall,
            rating = domain.rating
        )

    fun toDomain(row: FoodRow): Food =
        Food(
            id = row.id,
            foodId = row.foodId,
            name = row.name,
            address = row.address,
            latitude = row.latitude,
            longitude = row.longitude,
            categoryLarge = row.categoryLarge,
            categoryMiddle = row.categoryMiddle,
            categorySmall = row.categorySmall,
            rating = row.rating
        )
}
