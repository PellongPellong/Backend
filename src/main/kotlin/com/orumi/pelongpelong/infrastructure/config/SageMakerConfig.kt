package com.orumi.pelongpelong.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient
import java.time.Duration

@ConfigurationProperties(prefix = "aws.bedrock") // 전체 공통 리전으로 바꿔야함.
data class SageMakerProperties(
    val region: String,
    val timeoutMs: Long,
)

@Configuration
@EnableConfigurationProperties(SageMakerProperties::class)
class SageMakerConfig {
    @Bean
    fun sageMakerRuntimeClient(properties: SageMakerProperties): SageMakerRuntimeClient {
        val overrideConfiguration = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMillis(properties.timeoutMs)) // 환경변수로 수정 예정
            .apiCallAttemptTimeout(Duration.ofMillis(properties.timeoutMs)) // 환경변수로 수정 예정
            .build()
        return SageMakerRuntimeClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(properties.region))
            .overrideConfiguration(overrideConfiguration)
            .build()
    }
}
