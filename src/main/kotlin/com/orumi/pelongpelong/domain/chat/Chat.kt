package com.orumi.pelongpelong.domain.chat

data class Chat(
    val pk: String,
    val sk: String,
    val role: String, // user/assistant
    val content: String,
    val inputTokenUsage: Int,
    val outputTokenUsage: Int,
)
