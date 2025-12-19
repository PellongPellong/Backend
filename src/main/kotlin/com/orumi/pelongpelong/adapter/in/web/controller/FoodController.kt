package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.application.port.`in`.command.CreateFoodCommand
import com.orumi.pelongpelong.application.port.`in`.command.FoodCommandUseCase
import com.orumi.pelongpelong.application.port.`in`.query.FoodQueryUseCase
import com.orumi.pelongpelong.domain.food.Food
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

data class CreateFoodRequest(
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

data class FoodResponse(
    val id: Int?,
    val foodId: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val categoryLarge: String?,
    val categoryMiddle: String?,
    val categorySmall: String?,
    val rating: Double,
)

@Tag(name = "Food", description = "음식 API")
@RestController
@RequestMapping("/foods")
class FoodController(
    private val foodCommandUseCase : FoodCommandUseCase,
    private val foodQueryUseCase : FoodQueryUseCase,
) {
    @PostMapping
    fun create(@RequestBody request: CreateFoodRequest): ApiResult<FoodResponse> {
        val created = foodCommandUseCase.create(CreateFoodCommand(
            request.foodId,
            request.name,
            request.address,
            request.latitude,
            request.longitude,
            request.categoryLarge,
            request.categoryMiddle,
            request.categorySmall,
            request.rating
        ))
        return ApiResponse.created(created.toResponse())
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Int) : ApiResult<FoodResponse> {
        val food = foodQueryUseCase.get(id)
        return ApiResponse.get(food.toResponse())
    }

//    @PostMapping("/all")
//    fun create() {
//        foodCommandUseCase.createAll()
//    }
}

private fun Food.toResponse(): FoodResponse = FoodResponse(
    id = id,
    foodId = foodId,
    name = name,
    address = address,
    latitude = latitude,
    longitude = longitude,
    categoryLarge = categoryLarge,
    categoryMiddle = categoryMiddle,
    categorySmall = categorySmall,
    rating = rating
)
