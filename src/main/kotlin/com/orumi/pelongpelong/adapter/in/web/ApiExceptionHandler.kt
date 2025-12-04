package com.orumi.pelongpelong.adapter.`in`.web

import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

data class ApiError(
        val errorType: String,
        val errorMessage: String,
)


private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(PelongException::class)
    fun handlePelongException(ex: PelongException): ResponseEntity<ApiResponse<Nothing>> {
        val error = ApiError(
                errorType = ex.errorType.name,
                errorMessage = ex.message
        )

        return ResponseEntity(
                ApiResponse.failure(error),
                ex.errorType.status
        )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ResponseEntity<ApiResponse<Nothing>> {
        val errorType = ErrorType.NOT_FOUND
        val error = ApiError(
                errorType = errorType.name,
                errorMessage = ex.message ?: errorType.message
        )

        return ResponseEntity(
                ApiResponse.failure(error),
                errorType.status
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        val errorType = ErrorType.INTERNAL_SERVER_ERROR
        val error = ApiError(
                errorType = errorType.name,
                errorMessage = errorType.message
        )

        logger.debug { errorType.name + " - " + ex.message }

        return ResponseEntity(
                ApiResponse.failure(error),
                errorType.status
        )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errorType = ErrorType.BAD_REQUEST
        val fieldErrors = ex.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }
        val error = ApiError(
                errorType = errorType.name,
                errorMessage = fieldErrors.joinToString(", ")
        )

        return ResponseEntity(
                ApiResponse.failure(error),
                errorType.status
        )
    }
}
