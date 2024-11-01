package com.grepp.nbe1_3_team9.controller.location

import com.grepp.nbe1_3_team9.controller.location.dto.PlaceDetailResponse
import com.grepp.nbe1_3_team9.controller.location.dto.PlaceRecommendResponse
import com.grepp.nbe1_3_team9.controller.location.dto.PlaceResponse
import com.grepp.nbe1_3_team9.domain.service.location.LocationApiService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/locations")
class LocationApiController(private val placeService: LocationApiService) {

    // 장소 자동 검색
    @GetMapping("/{eventId}/autocomplete")
    fun autocomplete(
        @PathVariable(name = "eventId") eventId: Long,
        @RequestParam(name = "input") input: String
    ): ResponseEntity<List<PlaceResponse>> {
        val places = placeService.getAutocompletePlaces(eventId, input)
        return ResponseEntity.ok(places)
    }

    // 추천 장소 검색
    @GetMapping("/{eventId}/recommend")
    fun getRecommendedPlaces(
        @PathVariable(name = "eventId") eventId: Long,
        @RequestParam(name = "type", defaultValue = "establishment") type: String = "establishment"
    ): ResponseEntity<List<PlaceRecommendResponse>> {
        val places = placeService.getRecommendedPlaces(eventId, type)
        return ResponseEntity.ok(places)
    }

    // 장소 상세 정보 조회
    @GetMapping("/{placeId}")
    fun getPlaceDetail(@PathVariable(name = "placeId") placeId: String): ResponseEntity<PlaceDetailResponse> {
        val placeDetail = placeService.getPlaceDetail(placeId)
        return ResponseEntity.ok(placeDetail)
    }
}
