package com.orumi.pelongpelong.application.service.query

import com.orumi.pelongpelong.application.port.`in`.query.ChatQueryUseCase
import com.orumi.pelongpelong.application.port.out.ChatRepository
import com.orumi.pelongpelong.domain.chat.Chat
import org.springframework.stereotype.Service

@Service
class ChatQueryService(
    private val chatRepository: ChatRepository,
) : ChatQueryUseCase {

    override fun list(): List<Chat> = chatRepository.findAll()
            .sortedByDescending { it.sk }

    override fun get(sessionId: String): List<Chat> = chatRepository.findByPk("SESSION#${sessionId}")
            .sortedByDescending { it.sk }
}
