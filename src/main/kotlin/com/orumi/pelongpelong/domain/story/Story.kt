package com.orumi.pelongpelong.domain.story

data class Story (
    val id: Long? = null,
    val storyId: Int,
    val placeName: String?,
    val lat: Double?,
    val lon: Double?,
    val score: Int?,
    val story: String?,
    val category1: String?,
    val category2: String?,
    val category3: String?,
)
