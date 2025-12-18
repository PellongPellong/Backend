package com.orumi.pelongpelong.application.port.`in`.command

import java.util.UUID

data class ChatCommand(
  val sessionId: String,// 식별자 PK
  val message: String,
  var bedrockResponseText: String? = null
) {
  companion object {
    fun of(sessionId: String?, message: String) = ChatCommand(sessionId ?: UUID.randomUUID().toString(), message)
  }
}
