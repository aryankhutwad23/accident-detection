package com.example.myaccidentapplication.data.model

data class AlertRequest(
    val userId: Int,
    val latitude: Double?,
    val longitude: Double?
)