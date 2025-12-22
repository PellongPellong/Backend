package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.dynamodb.ChatItem

interface ChatDynamoDbPort {
    fun save(chat: ChatItem)
    fun update(chat: ChatItem)
    fun findAll(): List<ChatItem>
    fun findByPk(pk: String): List<ChatItem>
}
