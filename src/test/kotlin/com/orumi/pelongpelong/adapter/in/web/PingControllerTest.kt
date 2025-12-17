package com.orumi.pelongpelong.adapter.`in`.web

import io.kotest.extensions.spring.SpringExtension
import io.kotest.core.spec.style.FunSpec
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class PingControllerTest(
    private val mockMvc: MockMvc,
) : FunSpec({

    test("GET /ping returns pong payload") {
        mockMvc.get("/ping")
            .andExpect {
                status { isOk() }
                jsonPath("$.data.message") { value("pong") }
            }
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
