package com.orumi.pelongpelong.adapter.out.postgresql

import jakarta.persistence.*

@Entity
@Table(name = "food", schema = "public")
class FoodEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Int? = null,

    @Column(name = "food_id", nullable = false)
    var foodId: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "address", nullable = false)
    var address: String,

    @Column(name = "latitude", nullable = false)
    var latitude: Double,

    @Column(name = "longitude", nullable = false)
    var longitude: Double,

    @Column(name = "category_large")
    var categoryLarge: String?,

    @Column(name = "category_middle")
    var categoryMiddle: String?,

    @Column(name = "category_small")
    var categorySmall: String?,

    @Column(name = "rating", nullable = false)
    var rating: Double,
)
