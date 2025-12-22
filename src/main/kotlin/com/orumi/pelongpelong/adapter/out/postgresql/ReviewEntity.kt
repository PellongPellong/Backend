package com.orumi.pelongpelong.adapter.out.postgresql

import jakarta.persistence.*

@Entity
@Table(name = "review", schema = "public")
class ReviewEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var review: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "food_id",
        referencedColumnName = "food_id",
        nullable = false
    )
    var food: FoodEntity? = null,
)
