package com.orumi.pelongpelong.adapter.`in`.web

import com.orumi.pelongpelong.adapter.`in`.web.controller.TodoController
import com.orumi.pelongpelong.application.port.`in`.CreateTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.DeleteTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.GetTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.ListTodoUseCase
import com.orumi.pelongpelong.common.exception.ApiExceptionHandler
import com.orumi.pelongpelong.domain.todo.Todo
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class TodoControllerTest : FunSpec({

    val createUseCase = mockk<CreateTodoUseCase>()
    val getUseCase = mockk<GetTodoUseCase>()
    val listUseCase = mockk<ListTodoUseCase>()
    val deleteUseCase = mockk<DeleteTodoUseCase>()

    val controller = TodoController(createUseCase, getUseCase, listUseCase, deleteUseCase)
    val mockMvc = MockMvcBuilders
        .standaloneSetup(controller)
        .setControllerAdvice(ApiExceptionHandler())
        .build()

    test("POST /todos 는 Todo를 생성하고 201을 반환한다") {
        every { createUseCase.create(any()) } returns Todo("task1")

        mockMvc.post("/todos") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"name": "task1"}"""
        }.andExpect {
            status { isCreated() }
            jsonPath("$.data.name") { value("task1") }
        }
    }

    test("GET /todos/{name} 는 Todo를 반환한다") {
        every { getUseCase.get("task1") } returns Todo("task1")

        mockMvc.get("/todos/task1")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.name") { value("task1") }
            }
    }

    test("GET /todos/{name} 에서 없으면 404 반환한다") {
        every { getUseCase.get("missing") } returns null

        mockMvc.get("/todos/missing")
            .andExpect {
                status { isNotFound() }
                jsonPath("$.error.errorType") { value("NOT_FOUND") }
            }
    }

    test("GET /todos 는 목록을 반환한다") {
        every { listUseCase.list() } returns listOf(Todo("a"), Todo("b"))

        mockMvc.get("/todos")
            .andExpect {
                status { isOk() }
                jsonPath("$.data[0].name") { value("a") }
                jsonPath("$.data[1].name") { value("b") }
            }
    }

    test("DELETE /todos/{name} 는 삭제 후 204를 반환한다") {
        every { deleteUseCase.delete("task1") } returns Unit

        mockMvc.delete("/todos/task1")
            .andExpect {
                status { isNoContent() }
            }

        verify(exactly = 1) { deleteUseCase.delete("task1") }
    }
})
