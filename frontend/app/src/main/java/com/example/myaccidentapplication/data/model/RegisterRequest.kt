package com.example.myaccidentapplication.data.model

import android.provider.ContactsContract

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

