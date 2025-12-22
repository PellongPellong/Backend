package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class RecommendationToolModule : ToolModule {
  override fun tool(): Tool {
    val propertiesDoc = Document.mapBuilder()
      .putDocument(
        "base_location", Document.mapBuilder()
          .putString("type", "string")
          .putString("description", "base location for recommendation")
          .build()
      )
      .build()

    val schema = Document.mapBuilder()
      .putString("type", "object")
      .putDocument("properties", propertiesDoc)
      .putList(
        "required",
        listOf(
          Document.fromString("base_location"),
        )
      )
      .putBoolean("additionalProperties", false)
      .build()

    val spec = ToolSpecification.builder()
      .name("recommendation_tool")
      .description("tool for search location recommendation")
      .inputSchema(ToolInputSchema.fromJson(schema))
      .build()

    return Tool.fromToolSpec(spec)
  }

  override fun handler(): ToolHandler =
    object : ToolHandler {
      override val name: String = "recommendation_tool"
      override fun handle(input: Document): Document {
        val baseLocation =
          input.asMap()["base_location"]?.asString() ?: throw IllegalArgumentException("base_location is required")

        //여기서 실제 tool(mehtod) 호출
        // ToolResultContentBlock.fromJson()에 넣을 JSON
        val resultDoc: Document = Document.mapBuilder()
          .putString("location_name", "김녕")
          .putString("story", "김녕은 조서시대에 뭐시기가 있었던 곳으로 유명합니다.")
          .build()
        return resultDoc
      }
    }
}
