package com.orumi.pelongpelong.infrastructure.config.security

import com.orumi.pelongpelong.infrastructure.config.security.filter.SessionIdFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter

@Configuration
class SecurityConfig(
        private val sessionIdFilter: SessionIdFilter
) {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .authorizeHttpRequests {
        it.anyRequest().permitAll()
//                it.requestMatchers("/ping", "/actuator/health").permitAll()
//                    .anyRequest().authenticated()
      }
      .httpBasic {}
    http.addFilterBefore(sessionIdFilter, AnonymousAuthenticationFilter::class.java)
    return http.build()
  }
}
