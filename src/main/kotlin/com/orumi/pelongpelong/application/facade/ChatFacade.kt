package com.orumi.pelongpelong.application.facade

import com.orumi.pelongpelong.adapter.`in`.web.response.ChatResponse
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommand
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommandUseCase
import com.orumi.pelongpelong.application.port.`in`.query.ChatQueryUseCase
import com.orumi.pelongpelong.application.port.out.BedrockPort
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.domain.chat.Chat
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
    logger.error { chat.pk }

    val chatHistory = this.getChatConverseHistory(chatCommand.sessionId).toMutableList()
    var historyMessage = ""
    if(chatHistory.size > 1) {
      historyMessage += "---previous conversation history START\n"
      chatHistory.filter { it.sk != chat.sk }.map{
        historyMessage += "userInputMessage: ${it.userInputText} \n"
        historyMessage += "LLM responseMessage: ${it.bedrockResponseText} \n"
      }
      historyMessage += "---previous conversation history END\n"
    }


    //2.call bedrock api
    val responseText = bedrockPort.converse(historyMessage + chatCommand.message)

    //3. update dynamoDB with response
    // 비동기 처리 필요
    chat.bedrockResponseText = responseText
    chat = chatCommandUseCase.update(chat)

    logger.error { chat.pk }
    return chat.toResponse()

  }

  fun getChatConverseHistory(sessionId: String): List<Chat>{
    if(sessionId.isEmpty()){
      throw PelongException(ErrorType.BAD_REQUEST, "채팅 내역을찾을 수 없습니다.")
    } else {
      return chatQueryUseCase.getList(sessionId)

    }
  }

  fun chatUpdateTest(sessionId: String){
    val chat = getChatConverseHistory(sessionId).firstOrNull()
    chat?.bedrockResponseText = "Updated Response Text"

    chat?.let{
      chatCommandUseCase.update(it)
    }

  }

}
