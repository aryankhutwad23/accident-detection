package com.example.myaccidentapplication.data.model

data class AccidentResponse(
    val success: Boolean,
    val message: String,
    val accidentId: String? = null
)

