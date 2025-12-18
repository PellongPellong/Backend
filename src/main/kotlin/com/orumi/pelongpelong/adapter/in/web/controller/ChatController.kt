package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.request.ChatRequest
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.adapter.`in`.web.response.ChatResponse
import com.orumi.pelongpelong.application.facade.ChatFacade
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommand
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Chat", description = "Bedrock LLM 대화 히스토리 API")
@RestController
@RequestMapping("/api/chats")
class ChatController(
  private val chatFacade: ChatFacade,
) {

  @PostMapping
  fun chat(@RequestBody request: ChatRequest): ApiResult<ChatResponse> {
    val chatCommand = ChatCommand.of(
      request.sessionId,
      request.message,
    )
    val chatResponse = chatFacade.converse(chatCommand)

    return ApiResponse.created(chatResponse)
  }

  @GetMapping("/{sessionId}")
  fun get(@PathVariable sessionId: String): ApiResult<List<ChatResponse>> {
    val chats = chatFacade.getChatConverseHistory(sessionId)
    val response = chats.map { it.toResponse() }

    return ApiResponse.get(response)
  }
}