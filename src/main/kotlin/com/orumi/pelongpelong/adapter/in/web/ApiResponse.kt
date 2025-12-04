package com.orumi.pelongpelong.adapter.`in`.web

data class ApiResponse<T>(
        val success: Boolean,
        val data: T? = null,
        val error: ApiError? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> =
                ApiResponse(success = true, data = data)

        fun failure(error: ApiError): ApiResponse<Nothing> =
                ApiResponse(success = false, error = error)
    }
}
