package com.orumi.pelongpelong.adapter.out.postgresql

import jakarta.persistence.*

@Entity
@Table(name = "review", schema = "public")
class ReviewEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "food_id",
        referencedColumnName = "food_id",
        nullable = false
    )
    var food: FoodEntity,

    @Column(name = "review", nullable = false)
    var review: String,
)
