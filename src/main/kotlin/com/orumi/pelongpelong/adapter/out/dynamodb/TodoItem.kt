package com.orumi.pelongpelong.adapter.out.dynamodb

import com.orumi.pelongpelong.domain.todo.Todo
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute

@DynamoDbBean
class TodoItem {
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("Name")
    var name: String? = null

    fun toDomain(): Todo = Todo(name = name ?: "")

    companion object {
        fun fromDomain(todo: Todo): TodoItem = TodoItem().apply {
            this.name = todo.name
        }
    }
}
