package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.domain.todo.Todo

interface TodoRepository {
    fun save(todo: Todo)
    fun findByName(name: String): Todo?
    fun findAll(): List<Todo>
    fun deleteByName(name: String)
}
