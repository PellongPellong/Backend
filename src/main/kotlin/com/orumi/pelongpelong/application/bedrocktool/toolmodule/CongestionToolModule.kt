package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.fasterxml.jackson.databind.ObjectMapper
import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.application.port.out.CongestionPredictionPort
import com.orumi.pelongpelong.application.port.out.CongestionPredictionRequest
import com.orumi.pelongpelong.domain.chat.Coordinate
import com.orumi.pelongpelong.domain.chat.LocationStatus
import com.orumi.pelongpelong.domain.chat.TimeTable
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

@Component
class CongestionToolModule(
  private val objectMapper: ObjectMapper,
  private val congestionPredictionPort: CongestionPredictionPort
) : ToolModule {
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
        val baseLocation =
          input.asMap()["base_location"]?.asString() ?: throw IllegalArgumentException("base_location not specified")


        var today = LocalDateTime.now()
        val requestList = (0..11).map {
          today = today.plusHours(1)
          CongestionPredictionRequest(
            itemId = baseLocation,
            month = today.monthValue,
            day = today.dayOfMonth,
            hour = today.hour,
            weekday = today.dayOfWeek.value,
            featRain = 0,  //todo: 날씨 Api연동
            featHoliday = 0, //todo: 공휴일 api 연동
            featVisitor = FeatVisitor.valueOf(today.month.toString().padStart(2, '0')).avgVisitor,
          )
        }
        val responseList = requestList.map {
          congestionPredictionPort.predict(it)
        }
        val locationStatus = LocationStatus(
          locationName = baseLocation,
          locationStatus = responseList[0].predictedCongestion.toInt(),
          timeTable = responseList.mapIndexed { index, result ->
            TimeTable(
              time = "${requestList[index].hour}:00",
              congestion = result.predictedCongestion.toInt()
            )
          },
          coordinate = Coordinate(lat = 0.0, lng = 0.0)
        )

        // sagemaker 호출
        // ToolResultContentBlock.fromJson()에 넣을 JSON
//        val resultDoc: Document = Document.fromString(
//          objectMapper.writeValueAsString(locationStatus)
//        )

        val resultDoc: Document = Document.mapBuilder()
          .putDocument(
            "LocationStatus",
            Document.mapBuilder()
              .putString("locationName", locationStatus.locationName)
              .putNumber("locationStatus", locationStatus.locationStatus)
              .putList(
                "timeTable",
                locationStatus.timeTable
                  .map {
                    Document.mapBuilder()
                      .putString("time", it.time)
                      .putNumber("congestion", it.congestion)
                      .build()
                  }
              ).build()
          )
          .putDocument(
            "coordinates", Document.mapBuilder()
              .putNumber("lat", 33.4356)
              .putNumber("lng", 126.9057)
//              .putNumber("lat", locationStatus.coordinate.lat)
//              .putNumber("lng", locationStatus.coordinate.lng )
              .build()
          )
          .build()
        return resultDoc
      }
    }
}
