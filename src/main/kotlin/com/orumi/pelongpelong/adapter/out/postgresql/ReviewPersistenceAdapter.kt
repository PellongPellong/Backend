package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.application.port.out.ReviewPort
import org.springframework.stereotype.Component

@Component
class ReviewPersistenceAdapter(
    private val jpaRepository: ReviewJpaRepository
) : ReviewPort {
    override fun save(review: ReviewEntity): ReviewEntity {
        return jpaRepository.save(review)
    }
}
