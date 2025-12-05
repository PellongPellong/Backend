package com.orumi.pelongpelong.application.port.`in`.command

data class CreateChatCommand(
        val sessionId: String,
        val content: String,
)
