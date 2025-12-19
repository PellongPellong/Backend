package com.orumi.pelongpelong.domain.chat

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.orumi.pelongpelong.adapter.`in`.web.response.ChatResponse

class Chat(
  val pk: String,
  val sk: String,
  val role: String, // user/assistant
  val content: String,
  val inputTokenUsage: Int,
  val outputTokenUsage: Int,
  val userInputText: String?,
  var bedrockResponseText: String?,
) {
  fun toResponse(): ChatResponse {
    val objectMapper = jacksonObjectMapper()
    return ChatResponse(
      sessionId = pk,
      userInputText = userInputText ?: "",
      bedrockResponse = objectMapper.readValue(bedrockResponseText ?: "")

    )
  }
}

data class BedrockResponse(
  val status: LocationStatus,
  val recommendation: Recommendation,
  val around: List<Around>,
  val coupons: List<Coupon>
)

data class LocationStatus(val locationName: String, val locationStatus: Int, val timeTable: List<TimeTable>, val coordinate: Coordinate)
data class Recommendation(val locationName: String, val story: String, val coordinate: Coordinate)
data class Around(val name: String, val reason: String, val coordinate: Coordinate)
data class Coupon(val name: String, val barcode: String)
data class TimeTable(val time: String, val congestion: Int)
data class Coordinate(val lat: Double, val lng: Double)