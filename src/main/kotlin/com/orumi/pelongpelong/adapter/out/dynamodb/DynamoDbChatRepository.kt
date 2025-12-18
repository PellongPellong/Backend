package com.orumi.pelongpelong.adapter.out.dynamodb

import com.orumi.pelongpelong.application.port.out.ChatRepository
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

@Component
class DynamoDbChatRepository(
  dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) : ChatRepository {

  private val table = dynamoDbEnhancedClient.table(TABLE_NAME, TableSchema.fromBean(ChatItem::class.java))

  companion object {
    private const val TABLE_NAME = "Chat"
  }

  override fun save(chat: ChatItem) {
    table.putItem(chat)
  }

  override fun findAll(): List<ChatItem> {
    return table.scan()
      .items().toList()
  }

  override fun findByPk(pk: String): List<ChatItem> {
    return table.query { q ->
      q.queryConditional(
        QueryConditional.keyEqualTo(keyOf(pk))
      )
    }
      .items().toList()
  }
}

private fun keyOf(pk: String): Key = Key.builder().partitionValue(pk).build()
