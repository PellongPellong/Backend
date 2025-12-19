package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.application.port.`in`.query.FoodQueryUseCase
import com.orumi.pelongpelong.domain.chat.Around
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class FoodToolModule(private val foodQueryUseCase: FoodQueryUseCase) : ToolModule {
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

                println("recommendLocation: $recommendLocation")

                val foods = foodQueryUseCase.getTop3(recommendLocation)
                // TODO 리뷰 받아와야 함.

                fun reasonFor(index: Int): String = when (index) {
                    0 -> "고등어가 맛있어요, 별점도 높아요"
                    1 -> "제주에 왔으면 한번 먹어야죠"
                    2 -> "아이스아메리카노 맛집"
                    else -> "근처에서 인기 있는 맛집이에요"
                }

                val aroundDocs = if (foods.isEmpty()) {
                    listOf(
                        Document.mapBuilder()
                            .putString("name", "")
                            .putString("reason", "근처에서 추천할 만한 맛집 데이터를 찾지 못했어요. 다른 지역명으로 다시 시도해 주세요.")
                            .build()
                    )
                } else {
                    foods.take(3).mapIndexed { idx, food ->
                        Document.mapBuilder()
                            .putString("name", food.name)
                            .putString("reason", reasonFor(idx))
                            .build()
                    }
                }

                return Document.mapBuilder()
                    .putList("Around", aroundDocs)
                    .build()
                /**
                 * 주소 컬럼에서 ~~읍을 쿼리해서 평점 높은 순으로 최대 3개 가져오기
                 * 근데 이유는 어떻게 가져오지? -> 리뷰까지 가져와서 bedrock한테 요약해달라하자
                 */
                // todo 여기서 툴 호출, 아마도 rdb. 우선순위 2
                // ToolResultContentBlock.fromJson()에 넣을 JSON
//                val resultDoc: Document = Document.mapBuilder()
//                  .putList("Around",
//                    listOf(Around("고등어 식당", "고등어가 맛있어요, 별점도 높아요"), Around("흑돼지 식당", "제주에 왔으면 한번 먹어야죠"), Around("김녕카페", "아이스아메리카노 맛집") ).map {
//                        Document.mapBuilder()
//                          .putString("name", it.name)
//                          .putString("reason", it.reason)
//                          .build()
//                    }
//                  )
//                    .build()
            }
        }
}
