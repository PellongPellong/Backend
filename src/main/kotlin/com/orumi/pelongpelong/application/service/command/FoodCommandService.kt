package com.orumi.pelongpelong.application.service.command

import com.orumi.pelongpelong.application.port.`in`.command.CreateFoodCommand
import com.orumi.pelongpelong.application.port.`in`.command.FoodCommandUseCase
import com.orumi.pelongpelong.application.port.out.FoodRepository
import com.orumi.pelongpelong.domain.food.Food
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodCommandService(
    private val foodRepository: FoodRepository,
) : FoodCommandUseCase {

    @Transactional
    override fun create(command: CreateFoodCommand): Food {
        val food = foodRepository.save(Food(
            foodId = command.foodId,
            name = command.name,
            address = command.address,
            latitude = command.latitude,
            longitude = command.longitude,
            categoryLarge = command.categoryLarge,
            categoryMiddle = command.categoryMiddle,
            categorySmall = command.categorySmall,
            rating = command.rating,
        ))
        return food
    }

//    @Transactional
//    override fun createAll() {
//        // json load
//        val mapper = jacksonObjectMapper()
//
//        val inputStream = Thread.currentThread()
//            .contextClassLoader
//            .getResourceAsStream("data/[file_name].json")
//            ?: throw IllegalArgumentException("json file not found")
//
//        val root: JsonNode = mapper.readTree(inputStream)
//
//        val placesNode = root["places"]
//            ?: throw IllegalArgumentException("places key not found")
//
//        var count = 0
//
//        val foodsToSave = mutableListOf<Food>()
//
//        placesNode.map { node ->
//            if (node["category_name"].asText().startsWith("음식점")) {
//                count++
//                // 대중소 분류 추출
//                val foodId = node["id"].asText()
//                val placeName = node["name"].asText()
//                val address = node["address"].asText()
//                val longitude = node["x"].asDouble()
//                val latitude = node["y"].asDouble()
//                val categoryPath = node["category_name"].asText()
//                val parts = parseCategoryUsingPlaceName(categoryPath, placeName)
//                val rating = node["average_rating"].asDouble()
//
//                val c1 = parts.getOrNull(0)
//                val c2 = parts.getOrNull(1)
//                val c3 = parts.getOrNull(2)
//
//                val food = Food(
//                    foodId = foodId,
//                    name = placeName,
//                    address = address,
//                    latitude = latitude,
//                    longitude = longitude,
//                    categoryLarge = c1,
//                    categoryMiddle = c2,
//                    categorySmall = c3,
//                    rating = rating
//                )
//                foodsToSave += food
//            }
//        }
//        foodRepository.saveAll(foodsToSave)
//    }
//
//    private fun parseCategoryUsingPlaceName(
//        categoryPath: String,
//        placeName: String
//    ): List<String> {
//
//        // 1. '>' 기준 분리
//        val parts = categoryPath
//            .split(">")
//            .map { it.trim() }
//            .filter { it.isNotEmpty() }
//
//        if (parts.isEmpty()) return emptyList()
//
//        // 2. 가게명 기반 브랜드 후보
//        val brandCandidate = placeName
//            .trim()
//            .split("\\s+".toRegex())
//            .firstOrNull()
//            ?: ""
//
//        // 3. 마지막 토큰 제거 여부 판단
//        val removedLast = if (
//            parts.last() == brandCandidate
//        ) {
//            parts.dropLast(1)
//        } else {
//            parts
//        }
//
//        // 4. 첫 번째 토큰 제거 (ex: 음식점)
//        return removedLast.drop(1)
//    }
}
