package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.domain.chat.Chat

interface ChatRepository {
    fun save(chat: Chat)
    fun findAll(): List<Chat>
    fun findByPk(pk: String): List<Chat>
}
