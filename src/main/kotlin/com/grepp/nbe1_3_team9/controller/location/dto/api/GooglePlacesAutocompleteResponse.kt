package com.grepp.nbe1_3_team9.controller.location.dto.api

data class GooglePlacesAutocompleteResponse(
    val predictions: List<Prediction>
) {
    data class Prediction(
        val place_id: String,
        val description: String,
        val main_text: String = ""
    )
}
