package com.orumi.pelongpelong.application.tool

import org.springframework.stereotype.Component
import software.amazon.awssdk.services.bedrockruntime.model.ToolConfiguration

@Component
class ToolFactory(
    private val modules: List<ToolModule>
) {
    fun toolConfiguration(): ToolConfiguration =
        ToolConfiguration.builder()
            .tools(modules.map { it.tool() })
            .build()

    fun toolRegistry(): ToolRegistry =
        ToolRegistry(
            handlers = modules
                .map { it.handler() }
                .associateBy { it.name } // name을 key로 Map 생성
        )
}
