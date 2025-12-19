package com.orumi.pelongpelong.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient
import java.time.Duration

@ConfigurationProperties(prefix = "aws.bedrock")
data class BedrockProperties(
    val region: String,
    val modelId: String,
    val timeoutMs: Long,
    val guardrailId: String,
    val guardrailVersion: String
)

@Configuration
@EnableConfigurationProperties(BedrockProperties::class)
class BedrockConfig {

    @Bean
    fun bedrockRuntimeClient(properties: BedrockProperties): BedrockRuntimeClient {
        val overrideConfiguration = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMillis(properties.timeoutMs))
            .apiCallAttemptTimeout(Duration.ofMillis(properties.timeoutMs))
            .build()

        return BedrockRuntimeClient.builder()
            .credentialsProvider(DefaultCredentialsProvider.create())
            .region(Region.of(properties.region))
            .overrideConfiguration(overrideConfiguration)
            .build()
    }
}
