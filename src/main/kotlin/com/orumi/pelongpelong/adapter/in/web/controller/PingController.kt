package com.orumi.pelongpelong.adapter.`in`.web.controller

import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import com.orumi.pelongpelong.application.port.`in`.PingResult
import com.orumi.pelongpelong.application.port.`in`.PingUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class PingResponse(val message: String)

@Tag(name = "Ping", description = "헬스체크용 API")
@RestController
@RequestMapping("/ping")
class PingController(
    private val pingUseCase: PingUseCase,
) {
    @Operation(
            summary = "핑 체크",
            description = "서버가 살아있는지 확인하는 간단한 API입니다."
    )
    @GetMapping
    fun ping(request: HttpServletRequest): ApiResult<PingResponse> {
//        val sessionId = request.getAttribute(SessionIdFilter.COOKIE_ATTR) as String
//        println("sessionId:$sessionId");

        val response = pingUseCase.ping().toResponse()

        return ApiResponse.get(response)
    }
}

private fun PingResult.toResponse(): PingResponse = PingResponse(message = message)
