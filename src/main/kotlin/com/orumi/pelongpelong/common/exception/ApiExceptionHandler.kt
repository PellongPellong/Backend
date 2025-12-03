package com.orumi.pelongpelong.common.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    // 커스텀 예외 처리
    @ExceptionHandler(PelongException::class)
    fun handlePelongException(ex: PelongException): ResponseEntity<ApiErrorView> {
        val body = ApiErrorView.from(ex.messageType)
        return ResponseEntity(body, ex.messageType.status)
    }
}
