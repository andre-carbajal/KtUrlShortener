package net.andrecarbajal.kturlshortener.controller

import jakarta.transaction.Transactional
import net.andrecarbajal.kturlshortener.domain.UrlService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UrlControllerUi(private val urlService: UrlService) {

    @GetMapping("/")
    fun getAllUrls(model: Model): String {
        val urls = urlService.getAllUrls()
        model.addAttribute("urls", urls)
        model.addAttribute("baseUrl", urlService.baseUrl)
        return "index"
    }

    @PostMapping("/ui/urls")
    @Transactional
    fun shortenUrl(
        @RequestParam("originalUrl") originalUrl: String,
        @RequestParam("urlCode") urlCode: String,
        @RequestParam("authInput") authInput: String,
        redirectAttributes: RedirectAttributes
    ): String {
        try {
            val shortUrl: String = urlService.shortenUrl(originalUrl, urlCode, authInput)
            redirectAttributes.addFlashAttribute("shortUrl", shortUrl)
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("errorMessage", e.message)
        }
        return "redirect:/"
    }
}