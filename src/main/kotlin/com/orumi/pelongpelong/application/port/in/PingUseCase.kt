package com.orumi.pelongpelong.application.port.`in`

data class PingResult(val message: String)

interface PingUseCase {
    fun ping(): PingResult
}
