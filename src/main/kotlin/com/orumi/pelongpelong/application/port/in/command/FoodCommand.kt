package com.orumi.pelongpelong.application.port.`in`.command

data class CreateFoodCommand(
        val foodId: String,
        val name: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val categoryLarge: String,
        val categoryMiddle: String,
        val categorySmall: String,
        val rating: Double,
)
