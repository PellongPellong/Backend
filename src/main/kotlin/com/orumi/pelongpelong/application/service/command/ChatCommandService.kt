package com.orumi.pelongpelong.application.service.command

import com.orumi.pelongpelong.adapter.out.dynamodb.ChatItem
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommandUseCase
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommand
import com.orumi.pelongpelong.application.port.out.ChatRepository
import com.orumi.pelongpelong.domain.chat.Chat
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class ChatCommandService(
    private val chatRepository: ChatRepository,
) : ChatCommandUseCase {

    override fun save(command: ChatCommand): Chat {
        // todo: bedrock에 사용자 입력으로 요청 보내기
        // todo: bedrock에 응답 받기
        // todo: chat 테이블에 user와 assistant 대화값 넣기

        val timestamp = Instant.now().toString()

        // DB 저장 (user)
        val chat = Chat(
                pk = command.sessionId,
                sk = timestamp,
                role = "user",
                content = command.message,
                inputTokenUsage = 0, // todo: bedrock의 응답에서 찾기
                outputTokenUsage = 0, // todo: bedrock의 응답에서 찾기
                userInputText = command.message,
                bedrockResponseText= command.bedrockResponseText
        )
        chatRepository.save(ChatItem.fromDomain(chat))
        return chat
    }

  override fun update(chat: Chat): Chat {
    chatRepository.update(ChatItem.fromDomain(chat))
    return chat
  }

}
