package com.orumi.pelongpelong.application.port.`in`

import com.orumi.pelongpelong.domain.todo.Todo

interface CreateTodoUseCase {
    fun create(command: CreateTodoCommand): Todo
}

interface GetTodoUseCase {
    fun get(name: String): Todo?
}

interface ListTodoUseCase {
    fun list(): List<Todo>
}

interface DeleteTodoUseCase {
    fun delete(name: String)
}
