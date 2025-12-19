package com.orumi.pelongpelong.application.bedrocktool

import software.amazon.awssdk.services.bedrockruntime.model.Tool

interface ToolModule {
    /** Bedrock에 알려줄 Tool(from ToolSpecification) */
    fun tool(): Tool

    /** 서버에서 실제 실행할 핸들러 */
    fun handler(): ToolHandler
}
