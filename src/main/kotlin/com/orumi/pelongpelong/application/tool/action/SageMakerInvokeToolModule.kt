package com.orumi.pelongpelong.application.tool.action

import com.orumi.pelongpelong.application.tool.ToolHandler
import com.orumi.pelongpelong.application.tool.ToolModule
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.document.Document
import software.amazon.awssdk.services.bedrockruntime.model.Tool
import software.amazon.awssdk.services.bedrockruntime.model.ToolInputSchema
import software.amazon.awssdk.services.bedrockruntime.model.ToolSpecification

@Component
class SageMakerInvokeToolModule : ToolModule {
    // TODO 사용 예시라서 sagemaker endpoint 연결하면서 새로 구현
    override fun tool(): Tool {
        val schema = Document.mapBuilder()
            .putString("type", "object")
            .putDocument("properties",
                Document.mapBuilder()
                    .putDocument("endpointName", Document.mapBuilder().putString("type","string").build())
                    .putDocument("payload", Document.mapBuilder().putString("type","object").build())
                    .build()
            )
            .putList("required", listOf(Document.fromString("endpointName"), Document.fromString("payload")))
            .build()

        val spec = ToolSpecification.builder()
            .name("invoke_sagemaker")
            .description("Invoke a SageMaker endpoint and return the inference result.")
            .inputSchema(ToolInputSchema.fromJson(schema))
            .build()

        return Tool.fromToolSpec(spec)
    }

    override fun handler(): ToolHandler =
        object : ToolHandler {
            override val name: String = "invoke_sagemaker"
            override fun handle(input: Document): Document {
                // endpointName/payload 꺼내서 invokeEndpoint 호출 → 결과를 Document로 리턴
                // (여기서 payload 직렬화/역직렬화는 Jackson을 써도 되고, Document를 JSON string으로 바꿔도 됨)
                TODO("call SageMaker and return Document")
            }
        }
}
