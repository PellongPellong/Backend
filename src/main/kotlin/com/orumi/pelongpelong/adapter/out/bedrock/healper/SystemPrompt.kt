package com.orumi.pelongpelong.adapter.out.bedrock.healper

import software.amazon.awssdk.services.bedrockruntime.model.SystemContentBlock

class SystemPrompt {
  companion object {

    const val SYSTEM_PROMPT = """
    당신은 여행객의 쾌적한 여행을 위해서 혼잡한 장소를 피할 수 있도록 도와주는 여행 가이드입니다.
    고객에겐 항상 공손한 대화체를 사용하세요. 약어나 은어는 피하고 정중한 표현을 사용하세요.
    제공되는 tools 를 활용하여 사용자가 현재 방문하려고 하는 관광지의 혼잡도를 확인하고, 
    혼잡 할 경우 대안을 제시해 주는 안내를 해 주세요.
    안내된 여행지 근처의 카페, 식당도 함께 추천 해 주세요 
  """

    fun getSystemPromptBlock() = SystemContentBlock
      .builder()
      .text(SYSTEM_PROMPT.trimIndent())
      .build()
  }
}