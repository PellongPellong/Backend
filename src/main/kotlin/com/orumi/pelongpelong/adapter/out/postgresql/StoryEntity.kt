package com.orumi.pelongpelong.adapter.out.postgresql

import jakarta.persistence.*

@Entity
@Table(name = "stories", schema = "public")
class StoryEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "story_id", nullable = false)
    var storyId: Int,

    @Column(name = "place_name", nullable = false)
    var placeName: String?,

    @Column(name = "lat")
    var lat: Double?,

    @Column(name = "lon")
    var lon: Double?,

    @Column(name = "score")
    var score: Int?,

    @Column(name = "story")
    var story: String?,

    @Column(name = "category_1")
    var category1: String?,

    @Column(name = "category_2")
    var category2: String?,

    @Column(name = "category_3")
    var category3: String?,
)
