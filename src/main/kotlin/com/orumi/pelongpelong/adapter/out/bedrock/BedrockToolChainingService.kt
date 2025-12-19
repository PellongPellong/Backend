package com.orumi.pelongpelong.adapter.out.bedrock

import com.orumi.pelongpelong.application.bedrocktool.ToolExecution
import com.orumi.pelongpelong.application.bedrocktool.ToolRegistry
import com.orumi.pelongpelong.application.bedrocktool.healper.AdditionalModelRequestFields
import com.orumi.pelongpelong.application.bedrocktool.healper.InferenceConfig
import com.orumi.pelongpelong.application.bedrocktool.healper.SystemPrompt
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.*

class BedrockToolChainingService(
  private val bedrockRuntimeClient: BedrockRuntimeClient,
  private val modelIdProvider: () -> String,
  private val toolConfigProvider: () -> ToolConfiguration,
  private val toolRegistry: ToolRegistry,
//    private val systemPromptProvider: () -> String? = { null },
) {

  /**
   * END_TURN까지 돌며 최종 텍스트 반환
   */
  fun converseWithTools(
    prompt: String,
    maxTokens: Int? = null
  ): String {
    val history = mutableListOf<Message>()

    // todo 시스템 프롬프트 추가할 때
    // val systemBlocks: List<SystemContentBlock> =
    //     systemPromptProvider()
    //         ?.let { sp -> listOf(SystemContentBlock.fromText(sp)) }
    //         ?: emptyList()

    // 최초 유저 메시지
    history += Message.builder()
      .role(ConversationRole.USER)
      .content(ContentBlock.fromText(prompt))
      .build()

    while (true) {
      // bedrock 호출
      val response = bedrockRuntimeClient.converse { it ->
        it.modelId(modelIdProvider())
          // .system(systemBlocks) todo 시스템 프롬프트 추가
          .messages(history)
          .toolConfig(toolConfigProvider())
          .system(SystemPrompt.getSystemPromptBlock())
          .additionalModelRequestFields(
            Document.fromMap(
              mapOf(
                "inferenceConfig" to AdditionalModelRequestFields.additionalModelRequestFields()
              )
            )
          )
          .inferenceConfig(InferenceConfig.inferenceConfig())
      }

      val assistantMsg = response.output().message()
      history += assistantMsg

      // END_TURN이면 최종 답변 텍스트 반환
      if (response.stopReason() == StopReason.END_TURN) {
        return assistantMsg.content()
          .firstOrNull { it.type() == ContentBlock.Type.TEXT }
          ?.text()
          ?: "" // 모델이 텍스트 안 준 케이스
      }

      // toolUse가 있으면 실행하고 toolResult를 history에 추가한 뒤 루프 계속
      val toolUses = extractToolUses(assistantMsg)
      if (toolUses.isNotEmpty()) {
        // llm이 사용하라는 tool 실행
        val executions = toolRegistry.executeAll(toolUses)
        // ToolExecution 실행 결과들
        val toolResultMsg = buildToolResultMessage(executions)
        history += toolResultMsg
        continue
      }

      // END_TURN도 아니고 toolUse도 없으면: 더 진행 불가 → 안전하게 텍스트 리턴(또는 예외)
      // 다음 액션을 결정할 근거가 없어서 더 진행을 임의로 나아갈 수 없음.
      return assistantMsg.content()
        .firstOrNull { it.type() == ContentBlock.Type.TEXT }
        ?.text()
        ?: ""
    }
  }

  /**
   * assistant 메시지(content)에서 toolUse들을 전부 추출
   */
  private fun extractToolUses(message: Message): List<ToolUseBlock> {
    val result = mutableListOf<ToolUseBlock>()
    for (cb in message.content()) {
      if (cb.type() == ContentBlock.Type.TOOL_USE) {
        result += cb.toolUse()
      }
    }
    return result
  }

  /**
   * toolResult 메시지 하나(여러 toolResult 블록 포함 가능) 만들기
   * - role: "user"
   * - content: toolResult blocks
   */
  private fun buildToolResultMessage(executions: List<ToolExecution>): Message {
    val blocks = executions.map { exec ->
      ContentBlock.fromToolResult(
        ToolResultBlock.builder()
          .toolUseId(exec.toolUseId)
          .content(
            listOf(
              ToolResultContentBlock.fromJson(exec.outputJson)
            )
          )
          .status(ToolResultStatus.SUCCESS)
          .build()
      )
    }

    return Message.builder()
      .role("user")
      .content(blocks)
      .build()
  }
}

