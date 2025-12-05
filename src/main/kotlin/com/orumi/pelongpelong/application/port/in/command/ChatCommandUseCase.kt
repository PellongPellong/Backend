package com.orumi.pelongpelong.application.port.`in`.command

import com.orumi.pelongpelong.domain.chat.Chat

interface ChatCommandUseCase {
    fun create(command: CreateChatCommand): Chat
}
