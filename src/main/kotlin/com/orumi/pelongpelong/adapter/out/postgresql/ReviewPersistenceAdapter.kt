package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.application.port.out.ReviewRepository
import org.springframework.stereotype.Component

@Component
class ReviewPersistenceAdapter(
    private val jpaRepository: ReviewJpaRepository
) : ReviewRepository {
    override fun save(review: ReviewEntity): ReviewEntity {
        return jpaRepository.save(review)
    }
}
