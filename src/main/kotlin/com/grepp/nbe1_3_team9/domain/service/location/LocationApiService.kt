package com.grepp.nbe1_3_team9.domain.service.location

import com.grepp.nbe1_3_team9.controller.location.dto.PlaceDetailResponse
import com.grepp.nbe1_3_team9.controller.location.dto.PlaceRecommendResponse
import com.grepp.nbe1_3_team9.controller.location.dto.PlaceResponse
import com.grepp.nbe1_3_team9.controller.location.dto.api.GeocodingApiResponse
import com.grepp.nbe1_3_team9.controller.location.dto.api.GooglePlacesAutocompleteResponse
import com.grepp.nbe1_3_team9.controller.location.dto.api.GooglePlacesNearbyResponse
import com.grepp.nbe1_3_team9.controller.location.dto.api.PlaceDetailApiResponse
import com.grepp.nbe1_3_team9.domain.service.event.EventService
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate


@Service
class LocationApiService(
    private val restTemplate: RestTemplate,
    //private val eventService: EventService,
    @Value("\${google.api.key}")
    private val apiKey: String
) {

    // 장소 자동 검색
    fun getAutocompletePlaces(eventId: Long, input: String): List<PlaceResponse> {
//        val eventDto = eventService.getEventById(eventId)
//        val cityName = eventDto.city

        val location = getCoordinatesFromCityName("Seoul")

        val url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=$input&location=$location&radius=5000&types=establishment&key=$apiKey"

        val response: ResponseEntity<GooglePlacesAutocompleteResponse> = restTemplate.getForEntity(url, GooglePlacesAutocompleteResponse::class.java)

        return response.body?.predictions?.map { prediction ->
            PlaceResponse(prediction.place_id, prediction.description, null)
        } ?: emptyList()
    }

    // 장소 추천
    //@Cacheable(value = ["recommendedPlaces"], key = "#eventId + '-' + #type")
    fun getRecommendedPlaces(eventId: Long, type: String?): List<PlaceRecommendResponse> {
        //        val eventDto = eventService.getEventById(eventId)
//        val cityName = eventDto.city

        val location = getCoordinatesFromCityName("Seoul")
        val placeType = type ?: "establishment"
        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$location&radius=5000&type=$placeType&key=$apiKey"

        val response: ResponseEntity<GooglePlacesNearbyResponse> = restTemplate.getForEntity(url, GooglePlacesNearbyResponse::class.java)
        return response.body?.results?.map { result ->
            PlaceRecommendResponse(
                result.place_id,
                result.name,
                result.geometry.location.lat,
                result.geometry.location.lng,
                result.rating ?: 0.0,
                result.user_ratings_total,
                result.vicinity,
                getPhotoUrl_PlaceRecommend(result.photos)
            )
        } ?: emptyList()
    }

    // id로 장소 상세 정보 조회
    //@Cacheable(value = ["placeDetails"], key = "#placeId")
    fun getPlaceDetail(placeId: String): PlaceDetailResponse {
        val url = "https://maps.googleapis.com/maps/api/place/details/json?placeid=$placeId&key=$apiKey"

        val response: ResponseEntity<PlaceDetailApiResponse> = restTemplate.getForEntity(url, PlaceDetailApiResponse::class.java)

        val result = response.body?.result ?: throw RuntimeException("Place not found")

        return PlaceDetailResponse(
            result.place_id,
            result.name,
            result.geometry.location.lat,
            result.geometry.location.lng,
            result.formatted_address,
            result.formatted_phone_number,
            getPhotoUrl_PlaceDetail(result.photos),
            result.rating,
            result.url,
            getWeekdayText(result),
            result.website,
            isOpenNow(result)
        )
    }

    private fun getPhotoUrl_PlaceRecommend(photos: List<GooglePlacesNearbyResponse.Photo>?): String {
        return if (photos != null && photos.isNotEmpty()) {
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=${photos[0].photo_reference}&key=$apiKey"
        } else "default_photo_url"
    }

    private fun getPhotoUrl_PlaceDetail(photos: List<PlaceDetailApiResponse.Photo>?): String {
        return if (photos != null && photos.isNotEmpty()) {
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=${photos[0].photo_reference}&key=$apiKey"
        } else "default_photo_url"
    }

    fun getCoordinatesFromCityName(cityName: String): String {
        val geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=$cityName&key=$apiKey"

        val geocodingResponse: ResponseEntity<GeocodingApiResponse> = restTemplate.getForEntity(geocodingUrl, GeocodingApiResponse::class.java)

        val geocodingResult = geocodingResponse.body?.results?.first() ?: throw RuntimeException("Geocoding result not found")
        val latitude = geocodingResult.geometry.location.lat
        val longitude = geocodingResult.geometry.location.lng

        return "$latitude,$longitude"
    }

    private fun getWeekdayText(result: PlaceDetailApiResponse.Result): String {
        return result.current_opening_hours?.weekday_text?.joinToString(", ") ?: "운영 시간 정보가 없습니다."
    }

    private fun isOpenNow(result: PlaceDetailApiResponse.Result): Boolean {
        return result.current_opening_hours?.open_now ?: false // current_opening_hours가 null이면 false 반환
    }

}
