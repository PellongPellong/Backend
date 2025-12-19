package com.orumi.pelongpelong.application.bedrocktool

import software.amazon.awssdk.services.bedrockruntime.model.*
import software.amazon.awssdk.core.document.Document

/**
 * Tool 실행 결과(툴 이름/ID는 toolResult 만들 때 필요)
 */
data class ToolExecution(
    val toolUseId: String,
    val toolName: String,
    val outputJson: Document
)

interface ToolHandler {
    val name: String
    fun handle(input: Document): Document // output: JSON string
}

/** 여러 툴을 등록해두고, toolUse가 오면 이름으로 찾아 실행 -> 이름을 찾아서 뭘 실행? */
class ToolRegistry(private val handlers: Map<String, ToolHandler>) {
    fun executeAll(toolUses: List<ToolUseBlock>): List<ToolExecution> {
        return toolUses.map { tu ->
            val handler = handlers[tu.name()]
                ?: throw IllegalArgumentException("No handler registered for tool: ${tu.name()}")

            // tool 실행 결과
            val outputJson = handler.handle(tu.input())
            ToolExecution(
                toolUseId = tu.toolUseId(),
                toolName = tu.name(),
                outputJson = outputJson
            )
        }
    }
}
