package com.orumi.pelongpelong.application.service

import com.orumi.pelongpelong.application.port.`in`.PingResult
import com.orumi.pelongpelong.application.port.`in`.PingUseCase
import mu.KotlinLogging
import org.springframework.stereotype.Service

val logger = KotlinLogging.logger {}
@Service
class PingService : PingUseCase {

    override fun ping(): PingResult {
        logger.debug { "ping called" }
        return PingResult(message = "pong")
    }
}
