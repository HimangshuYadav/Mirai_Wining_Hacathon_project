package com.example.caretaker.data

data class CaretakerRegisterRequest(
    val username: String,
    val password: String,
    val phone: String
)

data class CaretakerLoginRequest(
    val username: String,
    val password: String
)

data class CaretakerLoginResponse(
    val status: String,
    val username: String,
    val phone: String
)

data class SosAlert(
    val person_name: String,
    val caregiver_phone: String,
    val latitude: Double,
    val longitude: Double,
    val location_link: String,
    val timestamp: String
)

data class SosAlertsResponse(
    val caregiver_phone: String,
    val alerts: List<SosAlert>
)
