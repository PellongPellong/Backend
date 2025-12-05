package com.orumi.pelongpelong.application.port.`in`

data class CreateChatCommand(
        val sessionId: String,
        val content: String,
)
