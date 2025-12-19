package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.domain.chat.Around
import com.orumi.pelongpelong.domain.chat.Coupon
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class CouponToolModule : ToolModule {
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
                val recommmendLocation = input.asMap()["recommend_location"]?.asString()?: throw IllegalArgumentException("recommend_location is required")

              //tool 호출 부분
                // ToolResultContentBlock.fromJson()에 넣을 JSON
                val resultDoc: Document = Document.mapBuilder()
                  .putList("Coupon",
                    listOf(
                      Coupon("고등어 식당", "12391287498"),
                              Coupon("아메리카노 쿠폰", "9999999998")
                    ).map {
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
}
