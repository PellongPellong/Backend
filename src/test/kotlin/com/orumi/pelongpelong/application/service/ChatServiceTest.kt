package com.orumi.pelongpelong.application.service

import com.orumi.pelongpelong.application.port.`in`.CreateChatCommand
import com.orumi.pelongpelong.application.port.out.ChatDynamoDbPort
import com.orumi.pelongpelong.domain.chat.Chat
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.time.Instant

class ChatServiceTest : FunSpec({

    val chatDynamoDbPort = mockk<ChatDynamoDbPort>()
    val chatService = ChatService(chatDynamoDbPort)

    afterTest {
        clearAllMocks()
    }

    test("list()는 ChatRepository에서 모든 채팅을 가져와 sk 내림차순으로 정렬한다") {
        // given
        val chat1 = Chat(
                pk = "SESSION#s1",
                sk = "TIME#2024-01-01T10:00:00Z",
                role = "user",
                content = "old",
                inputTokenUsage = 1,
                outputTokenUsage = 1
        )
        val chat2 = Chat(
                pk = "SESSION#s1",
                sk = "TIME#2024-01-01T11:00:00Z",
                role = "assistant",
                content = "new",
                inputTokenUsage = 2,
                outputTokenUsage = 2
        )

        // Repo는 정렬되지 않은 상태로 내려준다고 가정
        every { chatDynamoDbPort.findAll() } returns listOf(chat1, chat2)

        // when
        val result = chatService.list()

        // then
        result.shouldHaveSize(2)
        // sk 기준 내림차순인지 확인
        result.map { it.sk } shouldBe listOf(
                "TIME#2024-01-01T11:00:00Z",
                "TIME#2024-01-01T10:00:00Z"
        )

        verify(exactly = 1) { chatDynamoDbPort.findAll() }
    }

    test("create()는 SESSION#prefix, TIME#prefix로 Chat을 생성해서 저장하고 반환한다 (role=user)") {
        // given
        val command = CreateChatCommand(
                sessionId = "session-123",
                content = "hello bedrock"
        )

        val savedSlot = slot<Chat>()

        every { chatDynamoDbPort.save(capture(savedSlot)) } answers { savedSlot.captured }

        // when
        val result = chatService.create(command)

        // then
        verify(exactly = 1) { chatDynamoDbPort.save(any()) }

        // 저장한 값 검증
        val saved = savedSlot.captured
        saved.pk shouldBe "SESSION#${command.sessionId}"
        saved.role shouldBe "user"
        saved.content shouldBe command.content
        saved.inputTokenUsage shouldBe 0
        saved.outputTokenUsage shouldBe 0
        saved.sk.startsWith("TIME#") shouldBe true

        // 서비스 반환값도 동일한지
        result shouldBe saved
    }

    test("get(sessionId)는 SESSION#sessionId로 조회해서 sk 내림차순으로 정렬된 리스트를 반환한다") {
        // given
        val sessionId = "session-abc"
        val pk = "SESSION#$sessionId"

        val oldChat = Chat(
                pk = pk,
                sk = "TIME#${Instant.parse("2024-01-01T09:00:00Z")}",
                role = "user",
                content = "old message",
                inputTokenUsage = 1,
                outputTokenUsage = 1
        )
        val newChat = Chat(
                pk = pk,
                sk = "TIME#${Instant.parse("2024-01-01T10:00:00Z")}",
                role = "assistant",
                content = "new message",
                inputTokenUsage = 2,
                outputTokenUsage = 2
        )

        // 정렬되지 않은 순서로 내려온다고 가정
        every { chatDynamoDbPort.findByPk(pk) } returns listOf(oldChat, newChat)

        // when
        val result = chatService.get(sessionId)

        // then
        result.shouldHaveSize(2)
        result.map { it.pk }.toSet() shouldBe setOf(pk)

        // sk 기준 내림차순
        result.map { it.sk } shouldBe listOf(
                newChat.sk,
                oldChat.sk
        )

        verify(exactly = 1) { chatDynamoDbPort.findByPk(pk) }
    }
})
