package com.grepp.nbe1_3_team9.controller.weather

import com.grepp.nbe1_3_team9.domain.service.weather.WeatherService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class WeatherController(
    private val weatherService: WeatherService
) {

    @GetMapping("/forecast")
    fun getForecast(@RequestParam city: String): String {
        return weatherService.getForecastByCityName(city)
    }
}