package com.orumi.pelongpelong.adapter.out.postgresql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FoodJpaRepository : JpaRepository<FoodEntity, Int> {
    fun findTop3ByAddressContainingOrderByRatingDesc(
        address: String
    ): List<FoodEntity>

    fun findByFoodId(foodId: String): FoodEntity
}
