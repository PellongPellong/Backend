package com.orumi.pelongpelong.adapter.out.bedrock

import com.orumi.pelongpelong.application.port.out.BedrockPort
import com.orumi.pelongpelong.application.bedrocktool.ToolFactory
import com.orumi.pelongpelong.application.bedrocktool.healper.AdditionalModelRequestFields
import com.orumi.pelongpelong.application.bedrocktool.healper.InferenceConfig
import com.orumi.pelongpelong.application.bedrocktool.healper.SystemPrompt
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.infrastructure.config.BedrockProperties
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole
import software.amazon.awssdk.services.bedrockruntime.model.GuardrailConfiguration
import software.amazon.awssdk.services.bedrockruntime.model.Message
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient
import software.amazon.awssdk.core.document.Document

private val logger = KotlinLogging.logger {}

@Component
class BedrockModelClient(
  private val bedrockRuntimeClient: BedrockRuntimeClient,
  private val bedrockProperties: BedrockProperties,
  private val toolFactory: ToolFactory,
  private val sageMakerRuntimeClient: SageMakerRuntimeClient
) : BedrockPort {
  override fun converse(
    prompt: String,
    modelId: String?,
  ): String {

    logger.info("converse in ")
    val resolvedModelId = modelId ?: bedrockProperties.modelId

    val chanining = BedrockToolChainingService(
      bedrockRuntimeClient = bedrockRuntimeClient,
      modelIdProvider = { resolvedModelId },
      toolConfigProvider = { toolFactory.toolConfiguration() },
      toolRegistry = toolFactory.toolRegistry(),
      bedrockProperties = bedrockProperties,
    )

    return try {
      chanining.converseWithTools(prompt)
    } catch (e: BedrockRuntimeException) {
      logger.error(e) { "Bedrock converse failed: statusCode=${e.statusCode()}, modelId=$resolvedModelId" }
      throw PelongException(ErrorType.INTERNAL_SERVER_ERROR, "Bedrock converse failed: ${e.message}")
    } catch (e: Exception) {
      logger.error(e) { "Bedrock converse failed: ${e.message}" }
      throw PelongException(ErrorType.INTERNAL_SERVER_ERROR, "Bedrock converse failed: ${e.message}")
    }
  }

  override fun invoke(prompt: String, modelId: String?): String {
    val resolvedModelId = modelId ?: bedrockProperties.modelId

    val message = Message.builder()
      .role(ConversationRole.USER)
      .content(ContentBlock.fromText(""" 유저가 입력한 관광지의 읍/면/동 이름을 대답한다.
        | - 반드시 읍/면/동 이름만 대답한다.
        | - 다른 설명이나 부가적인 내용은 절대 포함하지 않는다.
        | 예: 입력 - 성산일출봉, 출력 - 성산읍
      """.trimMargin() + prompt))
      .build()

    return try {
      val response = bedrockRuntimeClient.converse { req ->
        req.modelId(resolvedModelId)
          .messages(message)
          .system(SystemPrompt.getSystemPromptBlock())
          .additionalModelRequestFields(
            Document.fromMap(
              mapOf(
                "inferenceConfig" to AdditionalModelRequestFields.additionalModelRequestFields()
              )
            )
          )
          .inferenceConfig(InferenceConfig.inferenceConfig())

        val guardrailId = bedrockProperties.guardrailId.takeIf { it.isNotBlank() }
        val guardrailVersion = bedrockProperties.guardrailVersion.takeIf { it.isNotBlank() }
        if (guardrailId != null && guardrailVersion != null) {
          req.guardrailConfig(
            GuardrailConfiguration.builder()
              .guardrailIdentifier(guardrailId)
              .guardrailVersion(guardrailVersion)
              .trace("enabled")
              .build()
          )
        }
      }

      response.output().message().content()
        .firstOrNull { it.type() == ContentBlock.Type.TEXT }
        ?.text()
        ?: ""
    } catch (e: BedrockRuntimeException) {
      logger.error(e) { "Bedrock invoke failed: statusCode=${e.statusCode()}, modelId=$resolvedModelId" }
      throw PelongException(ErrorType.INTERNAL_SERVER_ERROR, "Bedrock invoke failed: ${e.message}")
    } catch (e: Exception) {
      logger.error(e) { "Bedrock invoke failed: ${e.message}" }
      throw PelongException(ErrorType.INTERNAL_SERVER_ERROR, "Bedrock invoke failed: ${e.message}")
    }


  }
}
