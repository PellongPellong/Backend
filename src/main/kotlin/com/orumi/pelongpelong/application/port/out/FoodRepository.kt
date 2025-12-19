package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.postgresql.FoodEntity

interface FoodRepository {
    fun save(food: FoodEntity): FoodEntity

    fun saveAll(foods: List<FoodEntity>)

    fun findAll(): List<FoodEntity>

    fun findById(id: Int): FoodEntity

    fun findTop3ByAddress(address: String): List<FoodEntity>

    fun findByFoodId(foodId: String): FoodEntity
}

