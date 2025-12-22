package com.orumi.pelongpelong.application.port.`in`.query

import com.orumi.pelongpelong.domain.food.Food

interface FoodQueryUseCase {
    fun list(): List<Food>

    fun get(id: Int): Food

    fun findTop3ByAddressContainingOrderByRatingDesc(address: String): List<Food>

    fun findTop5ByNameContainingOrAddressContainingOrderByRatingDesc(topic: String): List<Food>

}
