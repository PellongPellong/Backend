package com.orumi.pelongpelong.adapter.out.bedrock

import software.amazon.awssdk.services.bedrockruntime.model.StopReason

enum class StopResponse(val value: String, val description: String, val needContinue: Boolean) {

  END_TURN("end_turn", "end of conversation", false),
  STOP_SEQUENCE("stop_sequence", "stop sequence encountered", false),
  MAX_TOKENS("max_tokens", "maximum tokens reached", false),
  TOOL_USE("tool_use", "request to use a tool", true),
  GUARDRAIL_INTERVENED("guardrail_intervened", "guardrail intervention occurred", false),
  CONTENT_FILTERED("content_filtered", "content was filtered", false),
  MODEL_CONTEXT_WINDOW_EXCEEDED("model_context_window_exceeded", "model context window exceeded", false),
  UNKNOWN("unknown", "unknown stop reason", false);

  companion object {
    fun of(stopReason: StopReason?): StopResponse =
      when (stopReason) {
        StopReason.END_TURN -> END_TURN
        StopReason.STOP_SEQUENCE -> STOP_SEQUENCE
        StopReason.MAX_TOKENS -> MAX_TOKENS
        StopReason.TOOL_USE -> TOOL_USE
        StopReason.GUARDRAIL_INTERVENED -> GUARDRAIL_INTERVENED
        StopReason.CONTENT_FILTERED -> CONTENT_FILTERED
//        StopReason.MODEL_CONTEXT_WINDOW_EXCEEDED -> MODEL_CONTEXT_WINDOW_EXCEEDED
        StopReason.UNKNOWN_TO_SDK_VERSION, null -> UNKNOWN
      }
  }

}
