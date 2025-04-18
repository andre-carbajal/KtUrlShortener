package net.andrecarbajal.kturlshortener.controller

import net.andrecarbajal.kturlshortener.domain.Url
import net.andrecarbajal.kturlshortener.domain.UrlService
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*


@Controller
class UrController(private val urlService: UrlService) {

    @GetMapping("/api/urls")
    fun getAllUrls(): ResponseEntity<List<Url>> {
        return ResponseEntity.ok(urlService.getAllUrls())
    }

    @GetMapping("/api/urls/{urlCode}/stats")
    fun getUrlStats(@PathVariable urlCode: String): ResponseEntity<Url?> {
        return ResponseEntity.ok<Url?>(urlService.getUrlStats(urlCode))
    }


    @PutMapping("/api/urls/{urlCode}")
    @Transactional
    fun updateUrl(
        @RequestHeader("Authorization") auth: String,
        @PathVariable urlCode: String,
        @RequestBody data: Url
    ): ResponseEntity<Void> {
        return urlService.updateUrlCode(auth, urlCode, data)
    }

    @DeleteMapping("/api/urls/{urlCode}")
    @Transactional
    fun deleteUrl(@RequestHeader("Authorization") auth: String, @PathVariable urlCode: String): ResponseEntity<Void> {
        return urlService.deleteUrl(auth, urlCode)
    }

}