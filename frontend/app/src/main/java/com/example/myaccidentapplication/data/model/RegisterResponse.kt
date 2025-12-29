package com.example.myaccidentapplication.data.model

data class RegisterResponse(
    val token: String?,
    val userId: Int,
    val message: String
)