package com.orumi.pelongpelong.application.port.`in`

import com.orumi.pelongpelong.domain.chat.Chat

interface CreateChatUseCase {
    fun create(command: CreateChatCommand): Chat
}

interface ListChatUseCase {
    fun list(): List<Chat>
}

interface GetChatUseCase {
    fun get(sessionId: String): List<Chat>
}
