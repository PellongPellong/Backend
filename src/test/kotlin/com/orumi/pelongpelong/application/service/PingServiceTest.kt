package com.orumi.pelongpelong.application.service

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class PingServiceTest : FunSpec({

    val service = PingService()

    test("ping은 pong 메시지를 반환한다") {
        val result = service.ping()

        result.message shouldBe "pong"
    }
})
