package com.orumi.pelongpelong.application.service

import com.orumi.pelongpelong.application.port.`in`.CreateTodoCommand
import com.orumi.pelongpelong.application.port.out.TodoRepository
import com.orumi.pelongpelong.domain.todo.Todo
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class TodoServiceTest : FunSpec({

    val repository = mockk<TodoRepository>()
    val service = TodoService(repository)

    test("create는 저장소에 Todo를 저장하고 반환한다") {
        every { repository.save(any()) } just runs

        val created = service.create(CreateTodoCommand("task1"))

        created shouldBe Todo("task1")
        verify(exactly = 1) { repository.save(Todo("task1")) }
    }

    test("get은 이름으로 Todo를 조회한다") {
        every { repository.findByName("task1") } returns Todo("task1")

        service.get("task1") shouldBe Todo("task1")
    }

    test("list는 모든 Todo를 반환한다") {
        every { repository.findAll() } returns listOf(Todo("a"), Todo("b"))

        service.list() shouldContainExactly listOf(Todo("a"), Todo("b"))
    }

    test("delete는 이름으로 삭제를 위임한다") {
        every { repository.deleteByName("task1") } just runs

        service.delete("task1")

        verify(exactly = 1) { repository.deleteByName("task1") }
    }
})
