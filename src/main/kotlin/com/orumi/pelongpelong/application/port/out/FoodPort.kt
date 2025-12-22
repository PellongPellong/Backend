package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.postgresql.FoodEntity

interface FoodPort {
    fun save(food: FoodEntity): FoodEntity

    fun saveAll(foods: List<FoodEntity>)

    fun findAll(): List<FoodEntity>

    fun findById(id: Int): FoodEntity

    fun findTop3ByAddressContainingOrderByRatingDesc(address: String): List<FoodEntity>

    fun findByFoodId(foodId: String): FoodEntity

    fun findTop5ByNameContainingOrAddressContainingOrderByRatingDesc(topic: String, address: String): List<FoodEntity>


}

