package com.grepp.nbe1_3_team9.domain.service.weather

import com.grepp.nbe1_3_team9.controller.weather.dto.GeocodeRes
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
@Transactional(readOnly = true)
class WeatherService(
    private val restTemplate: RestTemplate
) {

    @Value("\${weather.api.key}")
    private lateinit var apiKey: String

    // 도시 이름으로 날씨 예보 조회
    fun getForecastByCityName(city: String): String {
        // 1. Geocoding API (위도와 경도를 받아옴)
        val geoCodingUrl = UriComponentsBuilder.fromHttpUrl("http://api.openweathermap.org/geo/1.0/direct")
            .queryParam("q", city)
            .queryParam("limit", 1)
            .queryParam("appid", apiKey)
            .toUriString()

        val geocodeResponses = restTemplate.getForObject(geoCodingUrl, Array<GeocodeRes>::class.java)
        if (geocodeResponses.isNullOrEmpty()) {
            return "City not found"
        }

        val (lat, lon) = geocodeResponses[0]

        // 2. 5 Day / 3 Hour Forecast API
        val forecastUrl = UriComponentsBuilder.fromHttpUrl("https://api.openweathermap.org/data/2.5/forecast")
            .queryParam("lat", lat)
            .queryParam("lon", lon)
            .queryParam("appid", apiKey)
            .queryParam("units", "metric")
            .toUriString()

        return restTemplate.getForObject(forecastUrl, String::class.java) ?: "Forecast data not found"
    }
}