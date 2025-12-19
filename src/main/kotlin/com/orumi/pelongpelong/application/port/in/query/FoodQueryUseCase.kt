package com.orumi.pelongpelong.application.port.`in`.query

import com.orumi.pelongpelong.domain.food.Food

interface FoodQueryUseCase {
    fun list(): List<Food>

    fun get(id: Int): Food

    fun getTop3(address: String): List<Food>
}
