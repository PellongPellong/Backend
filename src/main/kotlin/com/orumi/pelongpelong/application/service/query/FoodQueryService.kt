package com.orumi.pelongpelong.application.service.query

import com.orumi.pelongpelong.adapter.out.postgresql.FoodMapper
import com.orumi.pelongpelong.application.port.`in`.query.FoodQueryUseCase
import com.orumi.pelongpelong.application.port.out.FoodRepository
import com.orumi.pelongpelong.domain.food.Food
import org.springframework.stereotype.Service

@Service
class FoodQueryService(
    private val foodRepository: FoodRepository,
) : FoodQueryUseCase {
    override fun list(): List<Food> = foodRepository.findAll().map { FoodMapper.toDomain(it) }

    override fun get(id: Int): Food {
        val food = foodRepository.findById(id)
        return FoodMapper.toDomain(food)
    }

    override fun getTop3(address: String): List<Food> {
        return foodRepository.findTop3ByAddress(address).map { FoodMapper.toDomain(it) }
    }
}
