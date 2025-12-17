package com.orumi.pelongpelong.adapter.`in`.web

import com.orumi.pelongpelong.adapter.`in`.web.controller.ChatController
import com.orumi.pelongpelong.application.port.`in`.CreateChatCommand
import com.orumi.pelongpelong.application.port.`in`.CreateChatUseCase
import com.orumi.pelongpelong.application.port.`in`.GetChatUseCase
import com.orumi.pelongpelong.application.port.`in`.ListChatUseCase
import com.orumi.pelongpelong.common.exception.ApiExceptionHandler
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.domain.chat.Chat
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class ChatControllerMockTest : FunSpec({

    val listChatUseCase = mockk<ListChatUseCase>()
    val createChatUseCase = mockk<CreateChatUseCase>()
    val getChatUseCase = mockk<GetChatUseCase>()

    val mockMvc: MockMvc = MockMvcBuilders
            .standaloneSetup(ChatController(listChatUseCase, createChatUseCase, getChatUseCase))
            .setControllerAdvice(ApiExceptionHandler()) // 프로젝트에 있는 예외 핸들러 사용
            .build()

    afterTest {
        clearAllMocks()
    }

    test("POST /chats 호출 시 createChatUseCase를 한 번 호출하고 201 + 생성된 ChatResponse를 반환한다") {
        // given
        val requestBody = """
            {
              "sessionId": "session-1",
              "content": "hello world"
            }
        """.trimIndent()

        val createdChat = Chat(
                pk = "session-1",
                sk = "2025-12-05T10:00:00",
                role = "USER",
                content = "hello world",
                inputTokenUsage = 10,
                outputTokenUsage = 20
        )

        every { createChatUseCase.create(any<CreateChatCommand>()) } returns createdChat

        // when & then
        mockMvc.post("/chats") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isCreated() }           // ApiResponse.created(...)
            jsonPath("$.data.pk") { value("session-1") }
            jsonPath("$.data.role") { value("USER") }
            jsonPath("$.data.content") { value("hello world") }
            jsonPath("$.data.inputTokenUsage") { value(10) }
            jsonPath("$.data.outputTokenUsage") { value(20) }
        }

        verify(exactly = 1) {
            createChatUseCase.create(
                    withArg {
                        // sessionId, content 잘 들어왔는지 검증
                        assert(it.sessionId == "session-1")
                        assert(it.content == "hello world")
                    }
            )
        }
    }

    test("GET /chats 호출 시 listChatUseCase를 호출하고 전체 대화 목록을 반환한다") {
        // given
        val chats = listOf(
                Chat(
                        pk = "session-1",
                        sk = "2025-12-05T10:00:00",
                        role = "USER",
                        content = "hi",
                        inputTokenUsage = 5,
                        outputTokenUsage = 7
                ),
                Chat(
                        pk = "session-1",
                        sk = "2025-12-05T10:01:00",
                        role = "ASSISTANT",
                        content = "hello :)",
                        inputTokenUsage = 5,
                        outputTokenUsage = 15
                )
        )

        every { listChatUseCase.list() } returns chats

        // when & then
        mockMvc.get("/chats")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data[0].pk") { value("session-1") }
                    jsonPath("$.data[0].role") { value("USER") }
                    jsonPath("$.data[1].role") { value("ASSISTANT") }
                }

        verify(exactly = 1) { listChatUseCase.list() }
    }

    test("GET /chats/{sessionId} 호출 시 해당 세션의 대화 목록을 반환한다") {
        // given
        val sessionId = "session-123"
        val chats = listOf(
                Chat(
                        pk = sessionId,
                        sk = "2025-12-05T10:00:00",
                        role = "USER",
                        content = "질문이 있어요",
                        inputTokenUsage = 8,
                        outputTokenUsage = 0
                )
        )

        every { getChatUseCase.get(sessionId) } returns chats

        // when & then
        mockMvc.get("/chats/$sessionId")
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data[0].pk") { value(sessionId) }
                    jsonPath("$.data[0].content") { value("질문이 있어요") }
                }

        verify(exactly = 1) { getChatUseCase.get(sessionId) }
    }

    test("GET /chats/{sessionId} 호출 시 대화가 없으면 PelongException을 던지고 404로 매핑된다") {
        // given
        val sessionId = "empty-session"
        every { getChatUseCase.get(sessionId) } returns emptyList()

        // 이 부분은 ApiExceptionHandler에서 PelongException + ErrorType.NOT_FOUND 를
        // 404로 변환해준다는 전제로 작성
        mockMvc.get("/chats/$sessionId")
                .andExpect {
                    status { isNotFound() }
                    jsonPath("$.error.errorType") { value(ErrorType.NOT_FOUND.name) }
                    jsonPath("$.error.errorMessage") {
                        value("Session [$sessionId] has no chat history")
                    }
                }

        verify(exactly = 1) { getChatUseCase.get(sessionId) }
    }
})
