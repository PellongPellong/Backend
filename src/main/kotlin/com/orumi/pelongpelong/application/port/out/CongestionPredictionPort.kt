package com.orumi.pelongpelong.application.port.out

/**
 * SageMaker inference endpoint (congestion prediction) outbound port.
 */
interface CongestionPredictionPort {
    fun predict(request: CongestionPredictionRequest): CongestionPredictionResult
}

data class CongestionPredictionRequest(
    val itemId: String,
    val month: Int,
    val day: Int,
    val hour: Int,
    val weekday: Int,
    val featRain: Int,
    val featHoliday: Int,
    val featVisitor: Long,
)

data class CongestionPredictionResult(
    val itemId: String,
    val predictedCongestion: Double,
    val status: String,
)
