package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.application.port.`in`.command.ChatCommandUseCase
import com.orumi.pelongpelong.application.port.`in`.command.CreateChatCommand
import com.orumi.pelongpelong.application.port.`in`.query.ChatQueryUseCase
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.domain.chat.Chat
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

data class CreateChatRequest(
        val sessionId: String,
        val content: String,
)

data class ChatResponse(
        val pk: String,
        val sk: String,
        val role: String,
        val content: String,
        val inputTokenUsage: Int,
        val outputTokenUsage: Int,
)

@Tag(name = "Chat", description = "Bedrock LLM 대화 히스토리 API")
@RestController
@RequestMapping("/chats")
class ChatController(
        private val chatQueryUseCase : ChatQueryUseCase,
        private val chatCommandUseCase: ChatCommandUseCase,
) {

    @PostMapping
    fun create(@RequestBody request: CreateChatRequest): ApiResult<ChatResponse> {
        val created = chatCommandUseCase.create(CreateChatCommand(
                request.sessionId,
                request.content
        ))
        return ApiResponse.created(created.toResponse())
    }

    @GetMapping
    fun list(): ApiResult<List<ChatResponse>> {
        val chats = chatQueryUseCase.list().map { it.toResponse() }
        return ApiResponse.get(chats)
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

        val response = chats.map { it.toResponse() }

        return ApiResponse.get(response)
    }
}

private fun Chat.toResponse(): ChatResponse = ChatResponse(
        pk = pk,
        sk = sk,
        role = role,
        content = content,
        inputTokenUsage = inputTokenUsage,
        outputTokenUsage = outputTokenUsage
)
