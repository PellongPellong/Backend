package com.orumi.pelongpelong.common.exception

import org.springframework.http.HttpStatus

enum class ErrorType(
        val status: HttpStatus,
        val message: String
) {
    NOT_FOUND(HttpStatus.NOT_FOUND,"리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 오류가 발생했습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 데이터입니다.")
}
