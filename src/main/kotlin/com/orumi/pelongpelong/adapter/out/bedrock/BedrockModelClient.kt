package com.orumi.pelongpelong.adapter.out.bedrock

import com.fasterxml.jackson.databind.ObjectMapper
import com.orumi.pelongpelong.application.port.out.BedrockPort
import com.orumi.pelongpelong.infrastructure.config.BedrockProperties
import mu.KotlinLogging
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole
import software.amazon.awssdk.services.bedrockruntime.model.ConverseResponse
import software.amazon.awssdk.services.bedrockruntime.model.Message
import software.amazon.awssdk.services.bedrockruntime.model.SpecificToolChoice
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolChoice
import software.amazon.awssdk.services.bedrockruntime.model.ToolConfiguration
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultBlock
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultContentBlock
import software.amazon.awssdk.services.bedrockruntime.model.ToolResultStatus
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

private val logger = KotlinLogging.logger {}

@Component
class BedrockModelClient(
  private val bedrockRuntimeClient: BedrockRuntimeClient,
  private val bedrockProperties: BedrockProperties,
  private val objectMapper: ObjectMapper
) : BedrockPort {
  override fun converse(
    prompt: String,
    modelId: String?,
    temperature: Double?,
    maxTokens: Int?
  ): String {

    logger.info("converse in ")
    val resolvedModelId = modelId ?: bedrockProperties.modelId
    val message = Message.builder()
      .content(ContentBlock.fromText(prompt))
      .role("user")
      .build()


    val propertiesDoc = Document.mapBuilder()
      .putDocument(
        "a",
        Document.mapBuilder()
          .putString("type", "integer")
          .putString("description", "First operand")
          .build()
      )
      .putDocument(
        "b",
        Document.mapBuilder()
          .putString("type", "integer")
          .putString("description", "Second operand")
          .build()
      )
      .build()

    val schemaDocument = Document.mapBuilder()
      .putString("type", "object")
      .putDocument("properties", propertiesDoc)
      .putList(
        "required",
        listOf(
          Document.fromString("a"),
          Document.fromString("b"),
        )
      )
      .putBoolean("additionalProperties", false)
      .build()

    val toolInputSchema = ToolInputSchema.fromJson(schemaDocument)
    val toolSpec = ToolSpecification.builder()
      .name("simple_adder")
      .description("tool for adding two integers")
      .inputSchema(toolInputSchema)
      .build()

    // 4) Tool 객체 생성
    val tool = Tool.fromToolSpec(toolSpec)

    // 5) toolChoice: 이 예제에서는 특정 툴을 반드시 쓰도록 강제 (SpecificToolChoice)
    val toolChoice = ToolChoice.fromTool(
      SpecificToolChoice.builder()
        .name("simple_adder")
        .build()
    )

    // 6) 최종 ToolConfiguration
    val toolConfig = ToolConfiguration.builder()
      .tools(tool)
//      .toolChoice(toolChoice) // 옵션: auto 로 두려면 생략하거나 AnyToolChoice 사용
      .build()

    try {
      var stopResponse = StopResponse.TOOL_USE
//      while(stopResponse.needContinue) {
      val firstResponse = bedrockRuntimeClient.converse {
        it.modelId(resolvedModelId)
          .messages(message)
          .toolConfig(toolConfig)
      }
      val (toolUseId, adderInput) = extractAdderCall(firstResponse)

      val sum = simpleAdder(adderInput.a, adderInput.b)

      val toolResultMessage = buildSimpleAdderToolResultMessage(toolUseId, sum)
      val history = listOf(
        message,                 // 처음 유저 질문
        firstResponse.output().message(),  // toolUse가 들어 있는 assistant 메시지
        toolResultMessage
      )
      val secondResponse = bedrockRuntimeClient.converse {
        it.modelId(resolvedModelId)
          .messages(history).toolConfig(toolConfig)
      }

//        logger.info { converseResposne }
//        logger.info { converseResposne.toString() }
//        stopResponse = StopResponse.of(converseResposne.stopReason())
//      }
      return secondResponse.output().message().content().first().text()
    } catch (e: BedrockRuntimeException) {
      logger.error(e) { "Bedrock invoke failed: statusCode=${e.statusCode()}, modelId=$resolvedModelId" }
      throw RuntimeException("Bedrock invoke failed: ${e.message}", e)
    } catch (e: Exception) {
      logger.error(e) { "Bedrock invoke failed: ${e.message}" }
      throw RuntimeException("Bedrock invoke failed: ${e.message}", e)
    }
  }

  fun simpleAdder(a: Int, b: Int): Int {

    return a + b
  }

  data class SimpleAdderInput(
    val a: Int,
    val b: Int
  )

  fun extractAdderCall(response: ConverseResponse): Pair<String, SimpleAdderInput> {
    val message = response.output().message()
    val mapper = ObjectMapper()

    for (block in message.content()) {
      val toolUse = block.toolUse()
      if (toolUse != null && toolUse.name() == "simple_adder") {
        val inputJson = toolUse.input().toString() // Document → JSON string
//        val input = mapper.readValue(inputJson, SimpleAdderInput::class.java)
        return toolUse.toolUseId() to SimpleAdderInput(
          a = mapper.readTree(inputJson).get("a").asInt(),
          b = mapper.readTree(inputJson).get("b").asInt()
        )
      }
    }
    throw IllegalStateException("simpleAdder toolUse not found")
  }

  fun buildSimpleAdderToolResultMessage(
    toolUseId: String,
    sum: Int
  ): Message {
    // 1) 툴 결과 JSON ({"sum": 3})
    val resultDoc: Document = Document.mapBuilder()
      .putNumber("sum", sum)   // Int 그대로 넣어도 됩니다.
      .build()

    // 2) ToolResultContentBlock – json 타입으로 래핑
    val resultContentBlock: ToolResultContentBlock =
      ToolResultContentBlock.fromJson(resultDoc)

    // 3) ToolResultBlock – 어떤 toolUse에 대한 결과인지 연결
    val toolResultBlock: ToolResultBlock = ToolResultBlock.builder()
      .toolUseId(toolUseId)
      .content(resultContentBlock)          // vararg 가능
      .status(ToolResultStatus.SUCCESS)     // 선택 (성공/에러 구분용)
      .build()

    // 4) ContentBlock – toolResult 필드에 넣기
    val contentBlock: ContentBlock = ContentBlock.builder()
      .toolResult(toolResultBlock)
      .build()

    // 5) Message – role은 "user" 이어야 함 (공식 문서 기준)
    return Message.builder()
      .role(ConversationRole.USER)
      .content(contentBlock)
      .build()
  }

}
