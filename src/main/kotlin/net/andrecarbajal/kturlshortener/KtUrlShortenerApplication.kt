package net.andrecarbajal.kturlshortener

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KtUrlShortenerApplication

fun main(args: Array<String>) {
    runApplication<KtUrlShortenerApplication>(*args)
}
