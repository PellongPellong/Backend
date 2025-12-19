package com.orumi.pelongpelong.application.port.`in`.command

import com.orumi.pelongpelong.domain.chat.Chat

interface ChatCommandUseCase {
    fun save(command: ChatCommand): Chat
    fun update(chat:Chat): Chat
}
