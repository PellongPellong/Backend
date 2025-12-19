package com.orumi.pelongpelong.adapter.out.bedrock

import com.orumi.pelongpelong.application.port.out.BedrockPort
import com.orumi.pelongpelong.application.bedrocktool.ToolFactory
import com.orumi.pelongpelong.application.tool.ToolFactory
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.infrastructure.config.BedrockProperties
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException

private val logger = KotlinLogging.logger {}

@Component
class BedrockModelClient(
  private val bedrockRuntimeClient: BedrockRuntimeClient,
  private val bedrockProperties: BedrockProperties,
  private val toolFactory: ToolFactory,
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
      bedrockProperties = bedrockProperties
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
}
