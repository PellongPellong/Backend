package com.orumi.pelongpelong.application.port.`in`.query

import com.orumi.pelongpelong.adapter.out.dynamodb.ChatItem
import com.orumi.pelongpelong.domain.chat.Chat

interface ChatQueryUseCase {
    fun list(): List<Chat>
    fun getList(sessionId: String): List<Chat>
}
