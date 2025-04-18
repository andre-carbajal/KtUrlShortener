package net.andrecarbajal.kturlshortener.domain

import java.time.LocalDateTime

data class Url(
    var originalUrl: String? = null,
    var urlCode: String? = null,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    @Transient
    var createdAtFormatted: String? = null,
    var visits: Long = 0
)