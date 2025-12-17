package com.orumi.pelongpelong.common.exception

class PelongException(
        val errorType: ErrorType,
        override val message: String = errorType.message
) : RuntimeException(message)
