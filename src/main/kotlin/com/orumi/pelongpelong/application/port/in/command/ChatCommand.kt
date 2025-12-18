package com.orumi.pelongpelong.application.port.`in`.command

import java.util.UUID

data class CreateChatCommand(
        val sessionId: String ,// 식별자 PK
        val message: String,
        var bedrockResponseText: String? = null
        ){
        companion object{
                fun of(sessionId: String?, message: String) = CreateChatCommand(sessionId ?: UUID.randomUUID().toString(), message)
        }
}
