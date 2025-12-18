package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.dynamodb.ChatItem
import com.orumi.pelongpelong.domain.chat.Chat

interface ChatRepository {
    fun save(chat: ChatItem)
    fun findAll(): List<ChatItem>
    fun findByPk(pk: String): List<ChatItem>
}
