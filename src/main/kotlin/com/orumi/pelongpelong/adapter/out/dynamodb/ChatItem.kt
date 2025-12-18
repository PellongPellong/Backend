package com.orumi.pelongpelong.adapter.out.dynamodb

import com.orumi.pelongpelong.domain.chat.Chat
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey

@DynamoDbBean
class ChatItem {
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("PK")
    var pk: String? = null

    @get:DynamoDbSortKey
    @get:DynamoDbAttribute("SK")
    var sk: String? = null

    @get:DynamoDbAttribute("Role")
    var role: String? = null

    @get:DynamoDbAttribute("Content")
    var content: String? = null

    @get:DynamoDbAttribute("InputTokenUsage")
    var inputTokenUsage: Int? = null

    @get:DynamoDbAttribute("OutputTokenUsage")
    var outputTokenUsage: Int? = null

    @get:DynamoDbAttribute("UserInputText")
    var userInputText: String? = null

    @get:DynamoDbAttribute("BedrockResponseText")
    var bedrockResponseText: String? = null

    /** DynamoDB → Domain */
    fun toDomain(): Chat =
        Chat(
            pk = pk ?: "",
            sk = sk ?: "",
            role = role ?: "",
            content = content ?: "",
            inputTokenUsage = inputTokenUsage ?: 0,
            outputTokenUsage = outputTokenUsage ?: 0,
            userInputText = userInputText ?: "",
            bedrockResponseText = bedrockResponseText ?: ""
        )

    companion object {
        /** Domain → DynamoDB */
        fun fromDomain(chat: Chat): ChatItem = ChatItem().apply {
            this.pk = chat.pk
            this.pk = chat.pk
            this.sk = chat.sk
            this.role = chat.role
            this.content = chat.content
            this.inputTokenUsage = chat.inputTokenUsage
            this.outputTokenUsage = chat.outputTokenUsage
            this.userInputText = chat.userInputText
            this.bedrockResponseText = chat.bedrockResponseText
        }
    }
}
