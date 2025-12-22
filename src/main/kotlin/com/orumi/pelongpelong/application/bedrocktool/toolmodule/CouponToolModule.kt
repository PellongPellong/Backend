package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.application.port.`in`.query.FoodQueryUseCase
import com.orumi.pelongpelong.domain.chat.Coupon
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class CouponToolModule(
  private val foodQueryUseCase: FoodQueryUseCase
) : ToolModule {
    override fun tool(): Tool {
        val propertiesDoc = Document.mapBuilder()
          .putDocument("recommend_location",Document.mapBuilder()
              .putString("type", "string  ")
              .putString("description", "base location for coupon recommendation")
              .build())
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
          .name("coupon_tool")
          .description("tool for get coupons around recommend_location")
          .inputSchema(ToolInputSchema.fromJson(schema))
          .build()

        return Tool.fromToolSpec(spec)
    }

    override fun handler(): ToolHandler =
        object : ToolHandler {
            override val name: String = "coupon_tool"
            override fun handle(input: Document): Document {
                // input은 {"a":..., "b":...} 형태라고 가정
                val recommendLocation = input.asMap()["recommend_location"]?.asString()?: throw IllegalArgumentException("recommend_location is required")

              val coupons = getCoupons(recommendLocation)
                // ToolResultContentBlock.fromJson()에 넣을 JSON
                val resultDoc: Document = Document.mapBuilder()
                  .putList("Coupon",
                    coupons.map {
                      Document.mapBuilder()
                        .putString("name", it.name)
                        .putString("barcode", it.barcode)
                        .build()
                    }
                  )
                    .build()
                return resultDoc
            }
        }
  fun getCoupons(location: String): List<Coupon> {
    val foods = foodQueryUseCase.findTop5ByNameContainingOrAddressContainingOrderByRatingDesc(location)
    // 실제 쿠폰 조회 로직 구현
    return foods.map { Coupon(it.name, (1..10).joinToString(""){ kotlin.random.Random.nextInt(0,10).toString() }) }
  }
}
