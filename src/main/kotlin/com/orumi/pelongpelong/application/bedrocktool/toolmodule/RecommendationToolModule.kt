package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.application.port.out.TourSpotPort
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification
import kotlin.math.cos

private val logger= KotlinLogging.logger {}
@Component
class RecommendationToolModule(
  //todo: db 조회 관련 service 또는 facade로 변경해야함
  private val tourSpotPort: TourSpotPort,
) : ToolModule {
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
      .description("tool for search location recommendation use this tool if base_location's congestion is high")
      .inputSchema(ToolInputSchema.fromJson(schema))
      .build()

    return Tool.fromToolSpec(spec)
  }

  override fun handler(): ToolHandler =
    object : ToolHandler {
      override val name: String = "recommendation_tool"
      override fun handle(input: Document): Document {
        val baseLocationName =
          input.asMap()["base_location"]?.asString() ?: throw IllegalArgumentException("base_location is required")
        logger.info { "----------------------:: base location for recommend : $baseLocationName" }

        val base= tourSpotPort.findByNameContainingOrAddressContaining(baseLocationName).firstOrNull()
        if(base == null){
          throw PelongException(ErrorType.NOT_FOUND,"base_location not found in DB: $baseLocationName")
        }






        //여기서 실제 tool(mehtod) 호출
        // ToolResultContentBlock.fromJson()에 넣을 JSON
        val resultDoc: Document = Document.mapBuilder()
          .putString("location_name", "김녕")
          .putString("story", "김녕은 조서시대에 뭐시기가 있었던 곳으로 유명합니다.")
          .putDocument("coordinates", Document.mapBuilder()
            .putNumber("lat", 33.5296)
            .putNumber("lng", 126.8880)
            .build()
          )
          .build()
        return resultDoc
      }
    }

  //기준점 기반 최대,최소 위ㅣ경도 계산
  fun bboxAround(lat: Double, lng: Double, radiusKm: Double = 5.0): BoundingBox {
    val deltaLat = radiusKm / 111.32
    val deltaLng = radiusKm / (111.32 * cos(Math.toRadians(lat)))
    return BoundingBox(
      minLat = lat - deltaLat,
      maxLat = lat + deltaLat,
      minLng = lng - deltaLng,
      maxLng = lng + deltaLng,
    )
  }
}
data class BoundingBox(val minLat: Double, val maxLat: Double, val minLng: Double, val maxLng: Double)
