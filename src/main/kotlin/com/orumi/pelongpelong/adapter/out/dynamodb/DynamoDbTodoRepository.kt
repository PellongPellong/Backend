package com.orumi.pelongpelong.adapter.out.dynamodb

import com.orumi.pelongpelong.application.port.out.TodoRepository
import com.orumi.pelongpelong.domain.todo.Todo
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

@Component
class DynamoDbTodoRepository(
    dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) : TodoRepository {

    private val table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(TodoItem::class.java))

    override fun save(todo: Todo) {
        table.putItem(TodoItem.fromDomain(todo))
    }

    override fun findByName(name: String): Todo? =
        table.getItem(keyOf(name))?.toDomain()

    override fun findAll(): List<Todo> {
        // PK-only 테이블이라 Scan 사용; 데이터가 많아지면 키 스캔/GSI 고려
        return table.scan()
            .items()
            .map { it.toDomain() }
    }

    override fun deleteByName(name: String) {
        table.deleteItem(keyOf(name))
    }

    companion object {
        private const val TABLE_NAME = "Todo"
    }

    private fun keyOf(name: String): Key = Key.builder().partitionValue(name).build()
}
