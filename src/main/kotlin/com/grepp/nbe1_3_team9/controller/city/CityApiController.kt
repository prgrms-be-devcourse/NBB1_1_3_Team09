package com.grepp.nbe1_3_team9.controller.city

import com.grepp.nbe1_3_team9.controller.city.dto.CityResponse
import com.grepp.nbe1_3_team9.domain.service.city.CityApiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/cities")
class CityApiController(private val cityService: CityApiService) {

    // 도시 검색
    @GetMapping("/search")
    fun searchCities(@RequestParam(name = "input") input: String): ResponseEntity<List<CityResponse>> {
        val results = cityService.searchCity(input)
        return ResponseEntity.ok(results)
    }

    // 나라별 도시 검색
    @GetMapping("/country")
    fun getCitiesByCountry(@RequestParam input: String): ResponseEntity<List<CityResponse>> {
        val cities = cityService.getCitiesByCountry(input)
        return ResponseEntity.ok(cities)
    }
}
