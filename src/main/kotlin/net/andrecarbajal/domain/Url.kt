package net.andrecarbajal.domain

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor

@Entity(name = "Url")
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
data class Url(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,

    @JoinColumn(name = "original_url") var originalUrl: String,

    @JoinColumn(name = "url_code") val urlCode: String
)