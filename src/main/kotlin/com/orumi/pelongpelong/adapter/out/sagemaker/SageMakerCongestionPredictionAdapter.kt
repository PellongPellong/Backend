package com.orumi.pelongpelong.adapter.out.sagemaker

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.orumi.pelongpelong.application.port.out.CongestionPredictionPort
import com.orumi.pelongpelong.application.port.out.CongestionPredictionRequest
import com.orumi.pelongpelong.application.port.out.CongestionPredictionResult
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient

@Component
class SageMakerCongestionPredictionAdapter(
    private val client: SageMakerRuntimeClient,
    private val objectMapper: ObjectMapper,
    @Value("\${aws.sagemaker.endpointName:}") private val endpointNameFromConfig: String,
) : CongestionPredictionPort {

    override fun predict(request: CongestionPredictionRequest): CongestionPredictionResult {
        val endpointName = endpointNameFromConfig.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Missing SageMaker endpoint name. Set 'aws.sagemaker.endpointName' (env: AWS_SAGEMAKER_ENDPOINT_NAME).")

        val payload = SageMakerCongestionRequest(
            itemId = request.itemId,
            month = request.month,
            day = request.day,
            hour = request.hour,
            weekday = request.weekday,
            featRain = request.featRain,
            featHoliday = request.featHoliday,
            featVisitor = request.featVisitor,
        )

        val bodyJson = objectMapper.writeValueAsString(payload)
        val response = client.invokeEndpoint { b ->
            b.endpointName(endpointName)
            b.contentType("application/json")
            b.accept("application/json")
            b.body(SdkBytes.fromUtf8String(bodyJson))
        }

        val bytes = response.body().asByteArray()
        if (bytes.isEmpty()) {
            throw IllegalStateException("SageMaker endpoint returned an empty body.")
        }

        val responsePayload = objectMapper.readValue(bytes, SageMakerCongestionResponse::class.java)
        return CongestionPredictionResult(
            itemId = responsePayload.itemId,
            predictedCongestion = responsePayload.predictedCongestion,
            status = responsePayload.status,
        )
    }
}

private data class SageMakerCongestionRequest(
    @JsonProperty("item_id") val itemId: String,
    @JsonProperty("month") val month: Int,
    @JsonProperty("day") val day: Int,
    @JsonProperty("hour") val hour: Int,
    @JsonProperty("weekday") val weekday: Int,
    @JsonProperty("feat_rain") val featRain: Int,
    @JsonProperty("feat_holiday") val featHoliday: Int,
    @JsonProperty("feat_visitor") val featVisitor: Long,
)

private data class SageMakerCongestionResponse(
    @JsonProperty("item_id") val itemId: String,
    @JsonProperty("predicted_congestion") val predictedCongestion: Double,
    @JsonProperty("status") val status: String,
)
