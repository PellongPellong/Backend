package com.orumi.pelongpelong.adapter.out.bedrock.healper

import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration

class InferenceConfig {
  companion object {
    // TODO: 헛소리 방지하고 싶어서 이렇게 설정 해두었습니다. 나중에 조정 하시죠
    const val MAX_TOKENS = 1024
    const val TEMPERATURE  = 0.3f
    const val TOP_P= 0.5f
    const val TOP_K= 0.5 // top_k는 additional model request에서 설정

    fun inferenceConfig() = InferenceConfiguration.builder()
      .temperature(TEMPERATURE)
      .topP(TOP_P)
      .build()

  }
}