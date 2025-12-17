package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.application.port.out.FoodRepository
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import com.orumi.pelongpelong.domain.food.Food
import org.springframework.stereotype.Component

@Component
class FoodPersistenceAdapter(
    private val jpaRepository: FoodJpaRepository
) : FoodRepository {

    override fun save(food: Food): Food {
        val saved = jpaRepository.save(FoodMapper.toRow(food))
        return FoodMapper.toDomain(saved)
    }

    override fun saveAll(foods: List<Food>): List<Food> {
        val rows = foods.map { FoodMapper.toRow(it) }
        val savedRows = jpaRepository.saveAll(rows)
        return savedRows.map { FoodMapper.toDomain(it) }
    }

    override fun findAll(): List<Food> = jpaRepository.findAll().map(FoodMapper::toDomain)

    override fun findById(id: Int): Food {
        val row = jpaRepository.findById(id).orElseThrow{
            PelongException(
                ErrorType.NOT_FOUND,
                "Id [$id] has no restaurant data"
            )
        }
        return FoodMapper.toDomain(row)
    }
}
