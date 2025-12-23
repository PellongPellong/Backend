package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import mu.KotlinLogging
import com.orumi.pelongpelong.application.port.`in`.query.StoryQueryUseCase
import com.orumi.pelongpelong.application.port.`in`.query.TourSpotQueryUseCase
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification
import kotlin.math.*

private val logger = KotlinLogging.logger {}

@Component
class RecommendationToolModule(
  //todo: db 조회 관련 service 또는 facade로 변경해야함
  private val storyQueryUseCase: StoryQueryUseCase,
  private val tourSpotQueryUseCase: TourSpotQueryUseCase
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
        logger.error { "errorlog for search,  Recommendation Tool In" }

        println("baseLocationName: $baseLocationName")

        val base = tourSpotQueryUseCase.findByNameContainingOrAddressContaining(baseLocationName).first()

        // todo 없는 경우 처리

        println("tourSpot: $base")

        // 1. 목적지 위경도 고정
        val baseLat = base.latitude
        val baseLon = base.longitude
        val km = 5.0

        // 2. 반경 5km 이내 후보 조회
        val box = boundingBox(baseLat, baseLon, km)

        val rough = storyQueryUseCase.findByLatBetweenAndLonBetweenOrderByScoreDesc( // score 내림차순으로 했을 때 가장 첫번째
          box.minLat, box.maxLat, box.minLng, box.maxLng
        )

        val within5km = rough.filter {
          haversineKm(baseLat, baseLon, it.lat, it.lon) <= km
        }

        val recommendation = within5km.firstOrNull()

        logger.error { "baseLocationName: $baseLocationName, recommendation: $recommendation " }


        val resultDoc: Document = when (recommendation) {
          null -> {
            Document.mapBuilder()
              .putNull(
                "Recommendation"
              )
              .build()

          }

          else -> {
            Document.mapBuilder()
              .putDocument(
                "Recommendation", Document.mapBuilder()
                  .putString("locationName", recommendation.placeName)
                  .putString("story", recommendation.story)
                  .build()
              )
              .build()

          }
        }

        return resultDoc
      }
    }

  private data class BoundingBox(val minLat: Double, val maxLat: Double, val minLng: Double, val maxLng: Double)

  //기준점 기반 최대,최소 위ㅣ경도 계산
  private fun boundingBox(lat: Double, lon: Double, radiusKm: Double): BoundingBox {
    val r = 6371.0
    val latRad = Math.toRadians(lat)

    val latDelta = Math.toDegrees(radiusKm / r)
    val lonDelta = Math.toDegrees(radiusKm / (r * cos(latRad)))

    return BoundingBox(
      minLat = lat - latDelta,
      maxLat = lat + latDelta,
      minLng = lon - lonDelta,
      maxLng = lon + lonDelta
    )
  }

  private fun haversineKm(lat1: Double, lon1: Double, lat2: Double?, lon2: Double?): Double {
    if (lat2 == null || lon2 == null) {
      return 0.0
    }
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2).pow(2.0) +
            cos(Math.toRadians(lat1)) *
            cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2.0)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
  }
}
