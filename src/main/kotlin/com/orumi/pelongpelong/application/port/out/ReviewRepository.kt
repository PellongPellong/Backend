package com.orumi.pelongpelong.application.port.out

import com.orumi.pelongpelong.adapter.out.postgresql.ReviewEntity

interface ReviewRepository {
    fun save(review: ReviewEntity): ReviewEntity
}

