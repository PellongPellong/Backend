package com.orumi.pelongpelong.adapter.`in`.web.response

data class ChatResponse(
  val sessionId: String,
  val status: LocationStatus,
  val recommendation: Recommendation,
  val around: List<Around>,
  val coupons: List<Coupon>
) {
  companion object {
    //mock data 입니다.
    fun of(
      sessionId: String,
    ) = ChatResponse(
      sessionId = sessionId,
      status = LocationStatus(
        locationName = "성산",
        locationStatus = 5,
        timeTable = listOf(
          TimeTable("10:00", 5),
          TimeTable("11:00", 5),
          TimeTable("12:00", 2),
        )
      ),
      recommendation = Recommendation(
        locationName = "한라산",
        story = "한라산은 제주도의 중심에 위치한 대한민국에서 가장 높은 산으로, 아름다운 자연경관과 다양한 등산로로 유명합니다."
      ),
      around = listOf(
        Around("고등어 식당", "고등어가 맛있어요."),
        Around("흑돼지 식당", "흑돼지가 맛있어요"),
      ),
      coupons = listOf(
        Coupon("고등어 식당 쿠폰", "1234567890"),
        Coupon("흑돼지 식당 쿠폰", "0987654321"),
      )
    )
  }
}

data class LocationStatus(val locationName: String, val locationStatus: Int, val timeTable: List<TimeTable>)
data class Recommendation(val locationName: String, val story: String)
data class Around(val name: String, val reason: String)
data class Coupon(val name: String, val barcode: String)
data class TimeTable(val time: String, val congestion: Int)