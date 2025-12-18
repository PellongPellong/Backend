package com.orumi.pelongpelong.application.port.`in`.command

import com.orumi.pelongpelong.domain.food.Food

interface FoodCommandUseCase {
    fun create(command: CreateFoodCommand): Food

//    fun createAll()
}
