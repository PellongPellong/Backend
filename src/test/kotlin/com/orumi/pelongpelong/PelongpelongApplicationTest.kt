package com.orumi.pelongpelong

import com.orumi.pelongpelong.application.port.`in`.PingUseCase
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldNotBe
import org.springframework.context.ApplicationContext
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PelongpelongApplicationTest(
    private val applicationContext: ApplicationContext,
) : FunSpec({

    test("스프링 컨텍스트가 로드되고 PingUseCase 빈을 찾는다") {
        applicationContext.getBean(PingUseCase::class.java) shouldNotBe null
    }
}) {
    override fun extensions() = listOf(SpringExtension)
}
