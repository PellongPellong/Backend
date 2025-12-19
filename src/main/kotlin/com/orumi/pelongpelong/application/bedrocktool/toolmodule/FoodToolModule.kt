package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.domain.chat.Around
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class FoodToolModule : ToolModule {
    override fun tool(): Tool {
        val propertiesDoc = Document.mapBuilder()
          .putDocument("recommend_location",Document.mapBuilder()
              .putString("type", "string")
              .putString("description", "key for recommend location around restaurant")
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

                val recommendLocation = input.asMap()["recommend_location"]?.asString() ?: throw IllegalArgumentException("recommend_location is required")

              // 여기서 툴 호출, 아마도 rdb
                // ToolResultContentBlock.fromJson()에 넣을 JSON
                val resultDoc: Document = Document.mapBuilder()
                  .putList("Around",
                    listOf(Around("고등어 식당", "고등어가 맛있어요, 별점도 높아요"), Around("흑돼지 식당", "제주에 왔으면 한번 먹어야죠"), Around("김녕카페", "아이스아메리카노 맛집") ).map {
                        Document.mapBuilder()
                          .putString("name", it.name)
                          .putString("reason", it.reason)
                          .build()
                    }
                  )
                    .build()
                return resultDoc
            }
        }
}
