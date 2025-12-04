package com.orumi.pelongpelong.adapter.`in`.web

import com.orumi.pelongpelong.application.port.`in`.PingResult
import com.orumi.pelongpelong.application.port.`in`.PingUseCase
import io.kotest.core.spec.style.FunSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class PingControllerMockTest : FunSpec({

    val pingUseCase = mockk<PingUseCase>()
    val mockMvc = MockMvcBuilders
        .standaloneSetup(PingController(pingUseCase))
        .setControllerAdvice(ApiExceptionHandler())
        .build()

    afterTest { clearAllMocks() }

    test("GET /ping 호출 시 pingUseCase를 한번 호출하고 pong을 반환한다") {
        every { pingUseCase.ping() } returns PingResult("pong")

        mockMvc.get("/ping")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.message") { value("pong") }
            }

        verify(exactly = 1) { pingUseCase.ping() }
    }
})
