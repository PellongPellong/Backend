package com.orumi.pelongpelong.application.service

import com.orumi.pelongpelong.application.port.`in`.*
import com.orumi.pelongpelong.application.port.out.ChatRepository
import com.orumi.pelongpelong.domain.chat.Chat
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatService(
    private val chatRepository: ChatRepository,
) : ListChatUseCase, CreateChatUseCase, GetChatUseCase {

    override fun list(): List<Chat> = chatRepository.findAll()
            .sortedByDescending { it.sk }

    override fun create(command: CreateChatCommand): Chat {
        // todo: bedrock에 사용자 입력으로 요청 보내기
        // todo: bedrock에 응답 받기
        // todo: chat 테이블에 user와 assistant 대화값 넣기

        val timestamp = Instant.now().toString()

        // DB 저장 (user)
        val chat = Chat(
                pk = "SESSION#${command.sessionId}",
                sk = "TIME#${timestamp}",
                role = "user",
                content = command.content,
                inputTokenUsage = 0, // todo: bedrock의 응답에서 찾기
                outputTokenUsage = 0 // todo: bedrock의 응답에서 찾기
        )
        chatRepository.save(chat)
        // DB 저장 (assistant)
//        val chat = Chat(
//                pk = "SESSION#${command.sessionId}",
//                sk = "TIME#${timestamp}",
//                role = "assistant",
//                content = command.content, // todo: bedrock의 응답에서 assistant 대답 찾아서 넣기
//                inputTokenUsage = 0, // todo: bedrock의 응답에서 찾기
//                outputTokenUsage = 0 // todo: bedrock의 응답에서 찾기
//        )
//        chatRepository.save(chat)
        // todo: assistant 응답으로 반환
        return chat
    }

    override fun get(sessionId: String): List<Chat> = chatRepository.findByPk("SESSION#${sessionId}")
            .sortedByDescending { it.sk }
}
