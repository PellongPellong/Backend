package com.orumi.pelongpelong.application.port.out

interface BedrockPort {
    fun converse(
        prompt: String,
        modelId: String? = null,
        temperature: Float? = null,
        maxTokens: Int? = null
    ): String
}
