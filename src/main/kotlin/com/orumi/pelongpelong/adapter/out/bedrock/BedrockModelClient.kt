package com.orumi.pelongpelong.adapter.out.bedrock

import com.fasterxml.jackson.databind.ObjectMapper
import com.orumi.pelongpelong.application.port.out.BedrockPort
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.infrastructure.config.BedrockProperties
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest
import software.amazon.awssdk.core.SdkBytes
import java.nio.charset.StandardCharsets

private val logger = KotlinLogging.logger {}
@Component
class BedrockModelClient(
    private val bedrockRuntimeClient: BedrockRuntimeClient,
    private val bedrockProperties: BedrockProperties,
    private val objectMapper: ObjectMapper
) : BedrockPort {
    override fun invokeText(
        prompt: String,
        modelId: String?,
        temperature: Double?,
        maxTokens: Int?
    ): String {
        /*TODO: model별 req / res 처리가 달라짐.
           추상화해서 OCP 원칙 지키기
           현재 hexagonal을 사용중이니 port / adapter 분리 필요
           사용 모델은 nova / claude 두개를 일단 구현.
         */
        val resolvedModelId = modelId ?: bedrockProperties.modelId
        val body = when {
            resolvedModelId.contains("titan") -> {
                buildTitanRequestBody(prompt, temperature, maxTokens)
            }
            resolvedModelId.contains("nova")  -> {
                buildNovaRequestBody(prompt, temperature, maxTokens)
            }
            else -> {
                throw PelongException(ErrorType.BAD_REQUEST,"Unsupported modelId: $resolvedModelId")
            }
        }

        val request = InvokeModelRequest.builder()
            .modelId(resolvedModelId)
            .body(SdkBytes.fromByteArray(body))
            .contentType("application/json")
            .accept("application/json")
            .build()

        try {
            val response = bedrockRuntimeClient.invokeModel(request)
            val responseByteArray = response.body().asByteArray()
            val responseJson = objectMapper.readTree(responseByteArray)
            logger.debug { responseJson }
            logger.debug{ String(responseByteArray, StandardCharsets.UTF_8) }
//            return responseJson.at("/outputText").asText()
            return responseJson.at("/output/message/content/0/text").asText("")


        } catch (e: BedrockRuntimeException) {
            logger.error(e) { "Bedrock invoke failed: statusCode=${e.statusCode()}, modelId=$resolvedModelId" }
            throw RuntimeException("Bedrock invoke failed: ${e.message}", e)
        }
    }

    private fun buildTitanRequestBody(
        prompt: String,
        temperature: Double?,
        maxTokens: Int?
    ): ByteArray {
        val payload = mutableMapOf<String, Any>(
            "inputText" to prompt
        )
        temperature?.let { payload["temperature"] = it }
        maxTokens?.let { payload["maxTokens"] = it }

        val json = objectMapper.writeValueAsString(payload)
        return json.toByteArray(StandardCharsets.UTF_8)
    }

    private fun buildNovaRequestBody(
        prompt: String,
        temperature: Double?,
        maxTokens: Int?
    ): ByteArray {
        val payload = mutableMapOf<String, Any>(
//            "inputText" to prompt
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to listOf(mapOf("text" to prompt))
                )
        ))
        temperature?.let { payload["temperature"] = it }
        maxTokens?.let { payload["maxTokens"] = it }

        val json = objectMapper.writeValueAsString(payload)
        return json.toByteArray(StandardCharsets.UTF_8)
    }
}
