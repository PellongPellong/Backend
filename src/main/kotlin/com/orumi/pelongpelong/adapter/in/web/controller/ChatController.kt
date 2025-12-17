package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.request.ChatRequest
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.adapter.`in`.web.response.ChatResponse
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommandUseCase
import com.orumi.pelongpelong.application.port.`in`.command.CreateChatCommand
import com.orumi.pelongpelong.application.port.`in`.query.ChatQueryUseCase
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

@Tag(name = "Chat", description = "Bedrock LLM 대화 히스토리 API")
@RestController
@RequestMapping("/chats")
class ChatController(
        private val chatQueryUseCase : ChatQueryUseCase,
        private val chatCommandUseCase: ChatCommandUseCase,
) {

    @PostMapping
    fun create(@RequestBody request: ChatRequest): ApiResult<ChatResponse> {
        val chat = chatCommandUseCase.create(CreateChatCommand.of(
                request.sessionId,
                request.message
        ))
        return ApiResponse.created(ChatResponse.of(chat.pk))
    }

    @GetMapping("/{sessionId}")
    fun get(@PathVariable sessionId: String): ApiResult<List<ChatResponse>> {
        val chats = chatQueryUseCase.get(sessionId)

        if (chats.isEmpty()) {
            throw PelongException(
                    ErrorType.NOT_FOUND,
                    "Session [$sessionId] has no chat history"
            )
        }

        val response = chats.map { ChatResponse.of(it.pk) }

        return ApiResponse.get(response)
    }
}