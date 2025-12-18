package com.orumi.pelongpelong.application.port.`in`.command

import com.orumi.pelongpelong.domain.chat.Chat

interface ChatCommandUseCase {
    fun save(command: CreateChatCommand): Chat
}
