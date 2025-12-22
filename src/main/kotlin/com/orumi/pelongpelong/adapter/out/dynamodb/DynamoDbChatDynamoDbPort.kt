package com.orumi.pelongpelong.adapter.out.dynamodb

import com.orumi.pelongpelong.application.port.out.ChatDynamoDbPort
import org.springframework.stereotype.Component
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional

@Component
class DynamoDbChatDynamoDbPort(
  dynamoDbEnhancedClient: DynamoDbEnhancedClient,
) : ChatDynamoDbPort {
  override fun update(chat: ChatItem) {
    val pk = chat.pk?.takeIf { it.isNotBlank() }
      ?: throw IllegalArgumentException("ChatItem.pk is required for update")
    val sk = chat.sk?.takeIf { it.isNotBlank() }
      ?: throw IllegalArgumentException("ChatItem.sk is required for update")

    val request = UpdateItemEnhancedRequest.builder(ChatItem::class.java)
      .item(chat)
      .ignoreNulls(true)
      .build()

    table.updateItem(request)
  }

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

private fun keyOf(pk: String) = software.amazon.awssdk.enhanced.dynamodb.Key.builder().partitionValue(pk).build()
