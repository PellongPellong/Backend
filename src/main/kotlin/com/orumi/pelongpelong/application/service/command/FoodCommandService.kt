package com.orumi.pelongpelong.application.service.command

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.orumi.pelongpelong.adapter.out.postgresql.FoodMapper
import com.orumi.pelongpelong.adapter.out.postgresql.ReviewMapper
import com.orumi.pelongpelong.application.port.`in`.command.CreateFoodCommand
import com.orumi.pelongpelong.application.port.`in`.command.FoodCommandUseCase
import com.orumi.pelongpelong.application.port.out.FoodRepository
import com.orumi.pelongpelong.application.port.out.ReviewRepository
import com.orumi.pelongpelong.domain.food.Food
import com.orumi.pelongpelong.domain.food.Review
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FoodCommandService(
    private val foodRepository: FoodRepository,
    private val reviewRepository: ReviewRepository
) : FoodCommandUseCase {

    @Transactional
    override fun create(command: CreateFoodCommand): Food {
        val food = Food(
            foodId = command.foodId,
            name = command.name,
            address = command.address,
            latitude = command.latitude,
            longitude = command.longitude,
            categoryLarge = command.categoryLarge,
            categoryMiddle = command.categoryMiddle,
            categorySmall = command.categorySmall,
            rating = command.rating,
            reviews = emptyList()
        )

        val savedFood = foodRepository.save(
            FoodMapper.toEntity(food)
        )

        return FoodMapper.toDomain(savedFood)
    }

    @Transactional
    override fun createAll() {
        // json load
        val mapper = jacksonObjectMapper()

        val inputStream = Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("data/jeju-places-with-reviews.json")
            ?: throw IllegalArgumentException("json file not found")

        val root: JsonNode = mapper.readTree(inputStream)

        val placesNode = root["places"]
            ?: throw IllegalArgumentException("places key not found")

        placesNode.map { node ->
            if (node["category_name"].asText().startsWith("음식점")) {
                // get food
                val food = foodRepository.findByFoodId(node["id"].asText())
                val reviewsNode = node["reviews"]

                reviewsNode
                    .sortedByDescending { it["star"].asDouble() } // ⭐ 별점 내림차순
                    .take(5)                                      // 상위 20개
                    .map { reviewNode ->
                        val reviewContent = reviewNode["review"].asText()
                        println("reviewContent: $reviewContent")

                        food.addReview(
                            ReviewMapper.toEntity(
                                Review(review = reviewContent)
                            )
                        )
                    }

                foodRepository.save(food)
            }
        }
    }
}
