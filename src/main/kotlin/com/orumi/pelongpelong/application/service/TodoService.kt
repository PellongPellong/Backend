package com.orumi.pelongpelong.application.service

import com.orumi.pelongpelong.application.port.`in`.CreateTodoCommand
import com.orumi.pelongpelong.application.port.`in`.CreateTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.DeleteTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.GetTodoUseCase
import com.orumi.pelongpelong.application.port.`in`.ListTodoUseCase
import com.orumi.pelongpelong.application.port.out.TodoRepository
import com.orumi.pelongpelong.domain.todo.Todo
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) : CreateTodoUseCase, GetTodoUseCase, ListTodoUseCase, DeleteTodoUseCase {

    override fun create(command: CreateTodoCommand): Todo {
        val todo = Todo(name = command.name)
        todoRepository.save(todo)
        return todo
    }

    override fun get(name: String): Todo? = todoRepository.findByName(name)

    override fun list(): List<Todo> = todoRepository.findAll()

    override fun delete(name: String) {
        todoRepository.deleteByName(name)
    }
}
