package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.application.port.out.BedrockPort
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class BedrockTestRequest(
    val prompt: String,
    val modelId: String? = null,
    val temperature: Float? = null,
    val maxTokens: Int? = null,
)

data class BedrockTestResponse(
    val text: String,
)

@Tag(name = "Bedrock", description = "AWS Bedrock 테스트 API")
@RestController
@RequestMapping("/bedrock")
class BedrockController(
    private val bedrockPort: BedrockPort,
) {
    @Operation(summary = "Bedrock 텍스트 converse 호출 테스트")
    @PostMapping("/converse")
    fun converse(@RequestBody request: BedrockTestRequest): ApiResult<BedrockTestResponse> {
        val text = bedrockPort.converse(
            prompt = request.prompt,
            modelId = request.modelId,
            temperature = request.temperature,
            maxTokens = request.maxTokens
        )
        return ApiResponse.get(BedrockTestResponse(text))
    }
}
