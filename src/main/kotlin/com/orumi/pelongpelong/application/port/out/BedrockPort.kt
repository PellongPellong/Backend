package com.orumi.pelongpelong.application.port.out

interface BedrockPort {
    fun converse(
        prompt: String,
        modelId: String? = null,
    ): String
}
