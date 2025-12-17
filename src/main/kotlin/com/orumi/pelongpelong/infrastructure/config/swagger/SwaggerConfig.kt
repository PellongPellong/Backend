package com.orumi.pelongpelong.infrastructure.config.swagger

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(
        info = Info(
                title = "제주숨곳 API",
                description = "PellongPellong 팀의 제주숨곳 REST API 문서",
                version = "v1"
        )
)
@Configuration
class SwaggerConfig
