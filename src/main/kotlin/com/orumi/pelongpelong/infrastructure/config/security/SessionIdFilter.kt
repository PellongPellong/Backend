package com.orumi.pelongpelong.infrastructure.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*

@Component
@Order(1)
class SessionIdFilter : OncePerRequestFilter() {
    companion object {
        const val COOKIE_ATTR = "SESSION_ID"
    }

    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        var sessionId = extractSessionIdFromCookie(request)
        println("1. sessionId = $sessionId")

        // 쿠키 없으면 새로 생성
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString()
            addSessionCookie(response, sessionId)
        }

        request.setAttribute(COOKIE_ATTR, sessionId)

        filterChain.doFilter(request, response)
    }

    private fun extractSessionIdFromCookie(request: HttpServletRequest): String? {
        return request.cookies
                ?.firstOrNull { it.name == COOKIE_ATTR }
                ?.value
    }

    private fun addSessionCookie(response: HttpServletResponse, sessionId: String) {
        val cookie = Cookie(COOKIE_ATTR, sessionId).apply {
            path = "/"
            maxAge = 60 * 60 * 24 * 30
            isHttpOnly = true
            secure = false
        }
        response.addCookie(cookie)
    }
}
