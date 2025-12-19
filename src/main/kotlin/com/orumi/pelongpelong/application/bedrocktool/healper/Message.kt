package com.orumi.pelongpelong.application.bedrocktool.healper

import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole
import software.amazon.awssdk.services.bedrockruntime.model.Message

class Message {
  companion object {
    fun of(prompt: String) =  Message.builder()
      .content(ContentBlock.fromText(prompt))
      .role(ConversationRole.USER)
      .build()
  }

}