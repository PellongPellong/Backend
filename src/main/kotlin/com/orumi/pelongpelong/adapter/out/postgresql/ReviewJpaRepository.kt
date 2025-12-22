package com.orumi.pelongpelong.adapter.out.postgresql

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewJpaRepository : JpaRepository<ReviewEntity, Long>
