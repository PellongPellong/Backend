package com.orumi.pelongpelong.application.service.query

import com.orumi.pelongpelong.application.port.`in`.query.FoodQueryUseCase
import com.orumi.pelongpelong.application.port.out.FoodRepository
import com.orumi.pelongpelong.domain.food.Food
import org.springframework.stereotype.Service

@Service
class FoodQueryService(
    private val foodRepository: FoodRepository,
) : FoodQueryUseCase {
    override fun list(): List<Food> = foodRepository.findAll()

    override fun get(id: Int): Food = foodRepository.findById(id)
}
