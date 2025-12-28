package com.example.myaccidentapplication.data.repository

import com.example.myaccidentapplication.data.local.UserPreferences
import com.example.myaccidentapplication.data.model.*
import com.example.myaccidentapplication.data.network.AccidentApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class AccidentRepository(
    private val api: AccidentApi,
    private val userPreferences: UserPreferences
) {

    // ---------------- LOGIN ----------------
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val request = LoginRequest(email, password)
            val response = api.login(request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userPreferences.saveUser(user.id, user.name, user.email)
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- REGISTER ----------------
    suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val request = RegisterRequest(name, email, password)
            val response = api.register(request)

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                userPreferences.saveUser(user.id, user.name, user.email)
                Result.success(user)
            } else {
                Result.failure(Exception("Registration failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- ACCIDENT REPORT ----------------
    suspend fun reportAccident(accident: AccidentEvent): Result<AccidentResponse> {
        return try {
            val userId = userPreferences.userId.first()
            val accidentWithUserId = accident.copy(userId = userId)
            val response = api.reportAccident(accidentWithUserId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Accident report failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- LOGOUT ----------------
    suspend fun logout() {
        userPreferences.clearUser()
    }

    // ---------------- USER INFO ----------------
    fun getCurrentUser(): Flow<User> {
        return combine(
            userPreferences.userId,
            userPreferences.userName,
            userPreferences.userEmail
        ) { id, name, email ->
            User(id = id, name = name, email = email)
        }
    }

    fun isLoggedIn(): Flow<Boolean> = userPreferences.isLoggedIn

    // ---------------- EMERGENCY CONTACTS ----------------
    suspend fun saveEmergencyContacts(userId: String, contact1: String, contact2: String) {
        // Optionally call API here if backend supports saving contacts
        userPreferences.saveEmergencyContacts(contact1, contact2)
    }

    fun getEmergencyContacts(): Flow<EmergencyContacts> {
        return combine(
            userPreferences.emergencyContact1,
            userPreferences.emergencyContact2
        ) { c1, c2 ->
            EmergencyContacts(contact1 = c1, contact2 = c2)
        }
    }
}