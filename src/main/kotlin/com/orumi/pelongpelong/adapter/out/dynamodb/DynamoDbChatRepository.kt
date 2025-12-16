package com.orumi.pelongpelong.adapter.out.dynamodb

import com.orumi.pelongpelong.application.port.out.ChatRepository
import com.orumi.pelongpelong.domain.chat.Chat
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

    override fun save(chat: Chat) {
        table.putItem(ChatItem.fromDomain(chat))
    }

    override fun findAll(): List<Chat> {
        return table.scan()
                .items()
                .map { it.toDomain() }
    }

    override fun findByPk(pk: String): List<Chat> {
        return table.query { q -> q.queryConditional(
          //todo: SESSION# prefix 필수인지 확인
                QueryConditional.keyEqualTo(keyOf("SESSION#${pk}"))
        )}
                .items()
                .map { it.toDomain() }
    }
}

private fun keyOf(pk: String): Key = Key.builder().partitionValue(pk).build()
