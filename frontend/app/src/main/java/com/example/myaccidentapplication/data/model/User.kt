package com.example.myaccidentapplication.data.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String = "" // In production, never store plain passwords
)

