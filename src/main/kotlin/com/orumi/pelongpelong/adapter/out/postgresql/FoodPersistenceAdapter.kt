package com.orumi.pelongpelong.adapter.out.postgresql

import com.orumi.pelongpelong.application.port.out.FoodRepository
import com.orumi.pelongpelong.common.exception.ErrorType
import com.orumi.pelongpelong.common.exception.PelongException
import org.springframework.stereotype.Component

@Component
class FoodPersistenceAdapter(
    private val jpaRepository: FoodJpaRepository
) : FoodRepository {

    override fun save(food: FoodEntity): FoodEntity {
        return jpaRepository.save(food)
    }

    override fun saveAll(foods: List<FoodEntity>) {
        jpaRepository.saveAll(foods)
    }

    override fun findAll(): List<FoodEntity> = jpaRepository.findAll()

    override fun findById(id: Int): FoodEntity {
        return jpaRepository.findById(id).orElseThrow{
            PelongException(
                ErrorType.NOT_FOUND,
                "Id [$id] has no restaurant data"
            )
        }
    }

    override fun findTop3ByAddress(address: String): List<FoodEntity> {
        return jpaRepository.findTop3ByAddressContainingOrderByRatingDesc(address)
    }

    override fun findByFoodId(foodId: String): FoodEntity {
        return jpaRepository.findByFoodId(foodId)
    }
}
