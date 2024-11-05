package com.grepp.nbe1_3_team9.domain.service.city

import com.grepp.nbe1_3_team9.controller.city.dto.CityResponse
import com.grepp.nbe1_3_team9.controller.location.dto.api.GooglePlacesAutocompleteResponse
import com.grepp.nbe1_3_team9.controller.location.dto.api.GooglePlacesNearbyResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CityApiService {

    @Value("\${google.api.key}")
    private lateinit var apiKey: String

    private val restTemplate = RestTemplate()

    // 도시 검색
    fun searchCity(query: String): List<CityResponse> {
        val url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$query&types=(cities)&key=$apiKey"
        val response: ResponseEntity<GooglePlacesAutocompleteResponse> = restTemplate.getForEntity(url, GooglePlacesAutocompleteResponse::class.java)

        return response.body?.predictions?.map { prediction ->
            CityResponse(prediction.place_id, prediction.description)
        } ?: emptyList()
    }

    // 나라별 도시 검색
    fun getCitiesByCountry(country: String): List<CityResponse> {
        val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=cities+in+$country&key=$apiKey"
        val response: ResponseEntity<GooglePlacesNearbyResponse> = restTemplate.getForEntity(url, GooglePlacesNearbyResponse::class.java)

        return response.body?.results?.map { result ->
            CityResponse(result.place_id, result.name)
        } ?: emptyList()
    }
}
