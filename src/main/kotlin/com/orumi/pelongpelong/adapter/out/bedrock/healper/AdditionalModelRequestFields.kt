package com.orumi.pelongpelong.adapter.out.bedrock.healper

import software.amazon.awssdk.core.document.Document

class AdditionalModelRequestFields {
  companion object {
    const val TOP_K = 30 // top_k는 모델별로 설정값이 다름. nova는 128이최대 (gemini답변인니 진짜인지 확인 필요)

    fun additionalModelRequestFields() = mapOf(
      "top_k" to Document.fromNumber(TOP_K)
    )
  }


}