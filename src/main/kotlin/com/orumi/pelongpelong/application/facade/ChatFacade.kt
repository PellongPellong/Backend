package com.orumi.pelongpelong.application.facade

import com.orumi.pelongpelong.adapter.`in`.web.request.ChatRequest
import com.orumi.pelongpelong.adapter.`in`.web.response.ChatResponse
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommand
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommandUseCase
import com.orumi.pelongpelong.application.port.`in`.query.ChatQueryUseCase
import com.orumi.pelongpelong.application.port.out.BedrockPort
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.domain.chat.Chat
import com.orumi.pelongpelong.infrastructure.config.security.SessionIdFilter
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}
@Service
class ChatFacade(
  val bedrockPort: BedrockPort,
  val chatQueryUseCase: ChatQueryUseCase,
  val chatCommandUseCase: ChatCommandUseCase
) {
  fun converse(chatCommand: ChatCommand): ChatResponse {
    //1.save to dynamoDB
    // 비동기 처리 필요
    var chat = chatCommandUseCase.save(chatCommand)

    //2.call bedrock api
    val responsneText = bedrockPort.converse(chatCommand.message)

    //3. update dynamoDB with response
    // 비동기 처리 필요
    chat.bedrockResponseText = responsneText
    chat = chatCommandUseCase.update(chat)

    return chat.toResponse()

  }

  fun getChatConverseHistory(sessionId: String): List<Chat>{
    if(sessionId.isEmpty()){
      throw PelongException(ErrorType.BAD_REQUEST, "채팅 내역을찾을 수 없습니다.")
    } else {
      return chatQueryUseCase.getList(sessionId)

    }
  }

}