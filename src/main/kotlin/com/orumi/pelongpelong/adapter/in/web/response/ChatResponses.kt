package com.orumi.pelongpelong.adapter.`in`.web.response

import com.orumi.pelongpelong.domain.chat.*

data class ChatResponse(
  val sessionId: String,
  val userInputText: String,
  val bedrockResponse: BedrockResponse

) {
  companion object {
    //mock data 입니다.
    fun ofMock(
      sessionId: String,
    ) = ChatResponse(
      sessionId = sessionId,
      userInputText = "성산 일출봉 가려고하는데 어때?",
      bedrockResponse = BedrockResponse(
        status = LocationStatus(
          locationName = "성산",
          locationStatus = 5,
          timeTable = listOf(
            TimeTable("10:00", 5),
            TimeTable("11:00", 5),
            TimeTable("12:00", 2),
          ),
          Coordinate(123.toDouble(),456.toDouble())
        ),
        recommendation = Recommendation(
          locationName = "한라산",
          story = "한라산은 제주도의 중심에 위치한 대한민국에서 가장 높은 산으로, 아름다운 자연경관과 다양한 등산로로 유명합니다.",
          Coordinate(123.toDouble(),456.toDouble())
        ),
        around = listOf(
          Around("고등어 식당", "고등어가 맛있어요.",Coordinate(123.toDouble(),456.toDouble())),
          Around("흑돼지 식당", "흑돼지가 맛있어요", Coordinate(123.toDouble(),456.toDouble())),
        ),
        coupons = listOf(
          Coupon("고등어 식당 쿠폰", "1234567890"),
          Coupon("흑돼지 식당 쿠폰", "0987654321"),
        )
      )
    )
  }
}
