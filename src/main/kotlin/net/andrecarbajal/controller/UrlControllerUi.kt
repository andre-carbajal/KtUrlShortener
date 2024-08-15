package net.andrecarbajal.controller

import net.andrecarbajal.domain.UrlService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UrlControllerUi(private val urlService: UrlService) {

    @GetMapping("/")
    fun getAllUrls(model: Model): String {
        val urls = urlService.getAllUrls()
        model.addAttribute("urls", urls)
        model.addAttribute("baseUrl", urlService.baseUrl)
        return "index"
    }


}