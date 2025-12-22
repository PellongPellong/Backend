package com.orumi.pelongpelong.adapter.out.postgresql

import jakarta.persistence.*

@Entity
@Table(name = "tour_spot", schema = "public")
class TourSpotEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "name", nullable = false)
    var name: String?,

    @Column(name = "lot_address")
    var address: String?,

    @Column(name = "latitude")
    var latitude: Double?,

    @Column(name = "longitude")
    var longitude: Double?,

    @Column(name = "place")
    var place: String?,

    )
