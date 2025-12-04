package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.application.port.`in`.CreateTodoCommand
import com.orumi.pelongpelong.application.port.`in`.CreateTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.DeleteTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.GetTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.ListTodoUseCase
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.domain.todo.Todo
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class CreateTodoRequest(
    val name: String,
)

data class TodoResponse(
    val name: String,
)

@RestController
@RequestMapping("/todos")
class TodoController(
    private val createTodoUseCase: CreateTodoUseCase,
    private val getTodoUseCase: GetTodoUseCase,
    private val listTodoUseCase: ListTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
) {

    @PostMapping
    fun create(@RequestBody request: CreateTodoRequest): ApiResult<Todo> {
        val created = createTodoUseCase.create(CreateTodoCommand(request.name))
        return ApiResponse.get(created)
    }

    @GetMapping("/{name}")
    fun get(@PathVariable name: String): ApiResult<TodoResponse> {
        val todo = getTodoUseCase.get(name)
            ?: throw PelongException(ErrorType.NOT_FOUND, "$name is not stored")

        return ApiResponse.get(todo.toResponse())
    }

    @GetMapping
    fun list(): ApiResult<List<TodoResponse>> {
        val todos = listTodoUseCase.list().map { it.toResponse() }
        return ApiResponse.get(todos)
    }

    @DeleteMapping("/{name}")
    fun delete(@PathVariable name: String): ApiResult<String> {
        deleteTodoUseCase.delete(name)
        return ApiResponse.get("successfully deleted")
    }
}

//todo: Todo.toResponse vs TodoResponse 생성자 중에 뭐가 나은지 확인
private fun Todo.toResponse(): TodoResponse = TodoResponse(name = name)
