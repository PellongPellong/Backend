package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.application.port.`in`.query.FoodQueryUseCase
import com.orumi.pelongpelong.application.port.`in`.query.TourSpotQueryUseCase
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class FoodToolModule(
  private val foodQueryUseCase: FoodQueryUseCase,
  private val tourSpotQueryUseCase: TourSpotQueryUseCase
) : ToolModule {
  override fun tool(): Tool {
    val propertiesDoc = Document.mapBuilder()
      .putDocument(
        "recommend_location", Document.mapBuilder()
          .putString("type", "string")
          .putString("description", "key for recommend location around restaurant")
          .build()
      )
      .build()

    val schema = Document.mapBuilder()
      .putString("type", "object")
      .putDocument("properties", propertiesDoc)
      .putList(
        "required",
        listOf(
          Document.fromString("recommend_location"),
        )
      )
      .putBoolean("additionalProperties", false)
      .build()

    val spec = ToolSpecification.builder()
      .name("food_recommendation_tool")
      .description("tool for restaurant recommendation around reccomend_location")
      .inputSchema(ToolInputSchema.fromJson(schema))
      .build()

    return Tool.fromToolSpec(spec)
  }

  override fun handler(): ToolHandler =
    object : ToolHandler {
      override val name: String = "food_recommendation_tool"
      override fun handle(input: Document): Document {

        val recommendLocation = input.asMap()["recommend_location"]?.asString()
          ?: throw IllegalArgumentException("recommend_location is required")

        val base = tourSpotQueryUseCase.findByNameContainingOrAddressContaining(recommendLocation).first()
        val foods = foodQueryUseCase.findTop3ByAddressContainingOrderByRatingDesc(base.place)

        val resultDoc: Document = Document.mapBuilder().putList(
          "Around",
          foods.map { food ->
            Document.mapBuilder()
              .putString("name", food.name)
              .putList("reviews", food.reviews.map {
                Document.mapBuilder()
                  .putString("review", it.review)
                  .build()
              })
              .putDocument("coordinates", Document.mapBuilder()
                .putNumber("lat", food.latitude)
                .putNumber("lng", food.longitude)
                .build()
              )
              .build()
          }).build()

        return resultDoc
      }
    }
}
