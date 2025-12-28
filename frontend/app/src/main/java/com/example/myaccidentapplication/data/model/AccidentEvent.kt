package com.example.myaccidentapplication.data.model

import java.util.Date

data class AccidentEvent(
    val id: String = "",
    val userId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null,
    val severity: Float = 0f,
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val accelerometerZ: Float = 0f,
    val gyroscopeX: Float = 0f,
    val gyroscopeY: Float = 0f,
    val gyroscopeZ: Float = 0f
)

