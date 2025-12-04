package com.orumi.pelongpelong.common.exception

import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResponse
import com.orumi.pelongpelong.adapter.`in`.web.response.ApiResult
import mu.KotlinLogging
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(PelongException::class)
    fun handlePelongException(ex: PelongException): ApiResult<Nothing> {
        return ApiResponse.failure(errorMessage = ex.message, status = ex.errorType.status)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(ex: NoResourceFoundException): ApiResult<Nothing> {
        val errorType = ErrorType.NOT_FOUND
        val errorMessage = ex.message ?: errorType.message

        return ApiResponse.failure(errorMessage, status = errorType.status)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ApiResult<Nothing> {
        val errorType = ErrorType.INTERNAL_SERVER_ERROR
        val errorMessage = errorType.message

        logger.debug { errorType.name + " - " + ex.message }

        return ApiResponse.failure(errorMessage, status = errorType.status)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ApiResult<Nothing> {
        val errorType = ErrorType.BAD_REQUEST
        val fieldErrors = ex.bindingResult.fieldErrors.map {
            "${it.field}: ${it.defaultMessage}"
        }
        val errorMessage = fieldErrors.joinToString(", ")

        return ApiResponse.failure(errorMessage, status = errorType.status)
    }
}
