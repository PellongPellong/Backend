package com.orumi.pelongpelong.application.bedrocktool.toolmodule

import com.fasterxml.jackson.databind.ObjectMapper
import com.orumi.pelongpelong.application.bedrocktool.ToolHandler
import com.orumi.pelongpelong.application.bedrocktool.ToolModule
import com.orumi.pelongpelong.application.port.out.CongestionPredictionPort
import com.orumi.pelongpelong.application.port.out.CongestionPredictionRequest
import com.orumi.pelongpelong.application.port.out.TourSpotPort
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.domain.chat.Coordinate
import com.orumi.pelongpelong.domain.chat.LocationStatus
import com.orumi.pelongpelong.domain.chat.TimeTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeout
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification
import java.time.LocalDateTime

private val logger = KotlinLogging.logger {}

@Component
class CongestionToolModule(
  private val objectMapper: ObjectMapper,
  private val congestionPredictionPort: CongestionPredictionPort,
  private val tourSpotPort: TourSpotPort
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

        logger.error { "errorlog for search,  Congestion Tool In" }

        val baseLocationName =
          input.asMap()["base_location"]?.asString() ?: throw IllegalArgumentException("base_location not specified")
        val base = tourSpotPort.findByNameContainingOrAddressContaining(baseLocationName).firstOrNull()
        val baseLocation =
          base?.place ?: throw PelongException(ErrorType.NOT_FOUND, "해당 명소를 찾을 수 없습니다: $baseLocationName")

        logger.error { "baseLocationName : $baseLocationName, baseLocation: $baseLocation" }

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

        val indexed = getPredictionAsync(requestList)
        val filled: List<Int> = buildList(indexed.size) {
          var lastGood = 0
          for (r in indexed) {
            val v = r.congestion ?: lastGood
            add(v)
            if (r.congestion != null) lastGood = r.congestion
          }
        }
//        val responseList = requestList.map {
//          congestionPredictionPort.predict(it)
//        }
        val locationStatus = LocationStatus(
//          locationName = baseLocation,
          locationName = baseLocationName,
          locationStatus = when (filled[0]) {
            in 0..25 -> 1
            in 26..50 -> 2
            in 51..75 -> 3
            in 75..100 -> 4
            else -> 5
          },
          timeTable = filled.mapIndexed { index, result ->
            TimeTable(
              time = "${requestList[index].hour}:00",
              congestion = when (result) {
                in 0..25 -> 1
                in 26..50 -> 2
                in 51..75 -> 3
                in 75..100 -> 4
                else -> 5
              }
            )
          },
          coordinate = Coordinate(lat = base.latitude, lng = base.longitude)
        )

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
              )
              .putDocument(
                "coordinates", Document.mapBuilder()
                  .putNumber("lat", locationStatus.coordinate!!.lat!!.toDouble())
                  .putNumber("lng", locationStatus.coordinate!!.lng!!.toDouble())
                  .build()
              )
              .build()
          )
          .build()
        return resultDoc
      }
    }

  fun getPredictionAsync(requestList: List<CongestionPredictionRequest>): List<IndexedResult> = runBlocking {
    val semaphore = Semaphore(permits = 4)

    supervisorScope<List<IndexedResult>> {
      requestList.mapIndexed { idx, req ->
        async(Dispatchers.IO) {
          semaphore.withPermit {
            val v = predictWithRetry(req, 1, timeoutMs = 1000L)
            IndexedResult(idx, v?.predictedCongestion?.toInt())
          }
        }
      }.awaitAll()
    }.sortedBy { it.index }
  }

  private suspend fun predictWithRetry(
    req: CongestionPredictionRequest,
    retries: Int,
    timeoutMs: Long,
  ): com.orumi.pelongpelong.application.port.out.CongestionPredictionResult? {
    var last: Exception? = null
    for (attempt in 0..retries) {
      val result = runCatching {
        withTimeout(timeoutMs) {
          congestionPredictionPort.predict(req)
        }
      }.getOrElse { e ->
        last = e as? Exception ?: Exception(e)
        null
      }

      if (result != null) return result

      if (attempt < retries) {
        delay(if (attempt == 0) 150L else 300L) // 백오프
      }
    }
    // 관대 모드: 여기서 throw 안 하고 null 반환(상위에서 직전 성공값으로 채움)
    return null
  }
}

data class IndexedResult(val index: Int, val congestion: Int?)
