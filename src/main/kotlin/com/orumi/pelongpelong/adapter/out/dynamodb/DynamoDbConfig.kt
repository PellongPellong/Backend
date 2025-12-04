package com.orumi.pelongpelong.adapter.out.dynamodb

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder

@Configuration
class DynamoDbConfig {

    @Bean
    fun dynamoDbClient(
        @Value("\${aws.dynamodb.region}") region: String,
        @Value("\${aws.dynamodb.endpoint:}") endpoint: String?,
        credentialsProvider: AwsCredentialsProvider
    ): DynamoDbClient {
        val builder: DynamoDbClientBuilder = DynamoDbClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)

        if (!endpoint.isNullOrBlank()) {
            builder.endpointOverride(java.net.URI.create(endpoint))
        }

        return builder.build()
    }

    @Bean
    fun dynamoDbEnhancedClient(dynamoDbClient: DynamoDbClient): DynamoDbEnhancedClient =
        DynamoDbEnhancedClient.builder()
            .dynamoDbClient(dynamoDbClient)
            .build()

    /**
     * 기본은 DefaultCredentialsProvider 체인 사용. 명시 값이 있으면 StaticCredentialsProvider 사용.
     */
    @Bean
    fun awsCredentialsProvider(
        @Value("\${aws.credentials.accessKey:}") accessKey: String?,
        @Value("\${aws.credentials.secretKey:}") secretKey: String?,
    ): AwsCredentialsProvider {
        val hasStatic = !accessKey.isNullOrBlank() && !secretKey.isNullOrBlank()
        return if (hasStatic) {
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
        } else {
            AwsCredentialsProviderChain.builder()
                .addCredentialsProvider(DefaultCredentialsProvider.create())
                .build()
        }
    }
}
