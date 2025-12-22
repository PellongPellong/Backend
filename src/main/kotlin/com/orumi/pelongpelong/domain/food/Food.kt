package com.orumi.pelongpelong.domain.food

data class Food (
    val id: Int? = null,
    val foodId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val categoryLarge: String?,
    val categoryMiddle: String?,
    val categorySmall: String?,
    val rating: Double,
    val reviews: List<Review>
)
