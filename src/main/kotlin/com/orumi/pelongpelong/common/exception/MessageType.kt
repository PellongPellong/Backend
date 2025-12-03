package com.orumi.pelongpelong.common.exception

import org.springframework.http.HttpStatus

enum class MessageType(
        val message: String,
        val status: HttpStatus
) {
    NOT_FOUND(
            "리소스를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    )
}
