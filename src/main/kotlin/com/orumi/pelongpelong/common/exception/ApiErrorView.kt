package com.orumi.pelongpelong.common.exception

data class Error(val errorType: String, val errorMessage: String)

data class ApiErrorView(
        val error: Error
) {
    companion object {
        fun from(messageType: MessageType): ApiErrorView =
                ApiErrorView(
                        error = Error(
                                errorType = messageType.name,
                                errorMessage = messageType.message
                        )
                )
    }
}
