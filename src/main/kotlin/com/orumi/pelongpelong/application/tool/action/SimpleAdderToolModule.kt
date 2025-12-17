package com.orumi.pelongpelong.application.tool.action

import com.orumi.pelongpelong.application.tool.ToolHandler
import com.orumi.pelongpelong.application.tool.ToolModule
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class SimpleAdderToolModule : ToolModule {
    override fun tool(): Tool {
        val propertiesDoc = Document.mapBuilder()
          .putDocument("a",Document.mapBuilder()
              .putString("type", "integer")
              .putString("description", "First operand")
              .build())
          .putDocument("b",Document.mapBuilder()
              .putString("type", "integer")
              .putString("description", "Second operand")
              .build())
          .build()

        val schema = Document.mapBuilder()
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

        val spec = ToolSpecification.builder()
          .name("simple_adder")
          .description("tool for adding two integers")
          .inputSchema(ToolInputSchema.fromJson(schema))
          .build()

        return Tool.fromToolSpec(spec)
    }

    override fun handler(): ToolHandler =
        object : ToolHandler {
            override val name: String = "simple_adder"
            override fun handle(input: Document): Document {
                // input은 {"a":..., "b":...} 형태라고 가정
                val a = input.asMap()["a"]?.asNumber()?.toInt() ?: 0
                val b = input.asMap()["b"]?.asNumber()?.toInt() ?: 0
                val sum = a + b

                // ToolResultContentBlock.fromJson()에 넣을 JSON
                val resultDoc: Document = Document.mapBuilder()
                    .putNumber("sum", sum)   // Int 그대로 넣어도 됩니다.
                    .build()
                return resultDoc
            }
        }
}
