package com.orumi.pelongpelong.adapter.`in`.web

import com.orumi.pelongpelong.application.port.`in`.PingResult
import com.orumi.pelongpelong.application.port.`in`.PingUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class PingResponse(val message: String)

@RestController
@RequestMapping("/ping")
class PingController(
    private val pingUseCase: PingUseCase,
) {
    @GetMapping
    fun ping(): PingResponse = pingUseCase.ping().toResponse()
}

private fun PingResult.toResponse(): PingResponse = PingResponse(message = message)
