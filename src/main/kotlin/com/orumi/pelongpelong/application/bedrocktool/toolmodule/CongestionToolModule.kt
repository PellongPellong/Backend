package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.domain.chat.TimeTable
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class CongestionToolModule : ToolModule {
  override fun tool(): Tool {
    val propertiesDoc = Document.mapBuilder()
      .putDocument(
        "base_location", Document.mapBuilder()
          .putString("type", "string")
          .putString("description", "base location for congestion information")
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
      .name("congestion_tool")
      .description("tool for find congestion information of base_location")
      .inputSchema(ToolInputSchema.fromJson(schema))
      .build()

    return Tool.fromToolSpec(spec)
  }

  override fun handler(): ToolHandler =
    object : ToolHandler {
      override val name: String = "congestion_tool"
      override fun handle(input: Document): Document {
        // input은 {"a":..., "b":...} 형태라고 가정
        val a =
          input.asMap()["base_location"]?.asString() ?: throw IllegalArgumentException("base_location not specified")

        // sagemaker 호출
        // ToolResultContentBlock.fromJson()에 넣을 JSON
        val resultDoc: Document = Document.mapBuilder()
          .putDocument(
            "LocationStatus",
            Document.mapBuilder()
              .putString("locationName", "성산")
              .putNumber("locationStatus", 5)
              .putList(
                "timeTable",
                listOf(
                  TimeTable("12:00", 3),
                  TimeTable("13:00", 5),
                  TimeTable("14:00", 2),
                  TimeTable("15:00", 4),
                  TimeTable("16:00", 1),
                  TimeTable("17:00", 3),
                  TimeTable("18:00", 5),
                  TimeTable("19:00", 2),
                  TimeTable("20:00", 4),
                  TimeTable("21:00", 1),
                  TimeTable("22:00", 3),
                  TimeTable("23:00", 5)
                ).map {
                  Document.mapBuilder()
                    .putString("time", it.time)
                    .putNumber("congestion", it.congestion)
                    .build()
                }
              ).build()
          )
          .putDocument("coordinates", Document.mapBuilder()
            .putNumber("lat", 33.4356)
            .putNumber("lng", 126.9057)
            .build()
          )
          .build()
        return resultDoc
      }
    }
}
