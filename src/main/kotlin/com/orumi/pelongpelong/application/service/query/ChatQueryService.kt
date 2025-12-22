package com.orumi.pelongpelong.application.service.query

import com.orumi.pelongpelong.application.port.`in`.query.ChatQueryUseCase
import com.orumi.pelongpelong.application.port.out.ChatDynamoDbPort
import com.orumi.pelongpelong.domain.chat.Chat
import org.springframework.stereotype.Service

@Service
class ChatQueryService(
  private val chatDynamoDbPort: ChatDynamoDbPort,
) : ChatQueryUseCase {

    override fun list(): List<Chat> = chatDynamoDbPort.findAll()
            .sortedByDescending { it.sk }.map{it.toDomain()}

    override fun getList(sessionId: String): List<Chat> = chatDynamoDbPort.findByPk(sessionId)
            .sortedBy{ it.sk }.map{it.toDomain()}
}
