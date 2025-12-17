package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.domain.food.Food

interface FoodRepository {
    fun save(food: Food): Food

    fun saveAll(foods: List<Food>): List<Food>

    fun findAll(): List<Food>

    fun findById(id: Int): Food
}

