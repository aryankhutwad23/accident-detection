package com.example.myaccidentapplication.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myaccidentapplication.data.model.AccidentEvent
import com.example.myaccidentapplication.di.AppContainer
import com.example.myaccidentapplication.data.network.TwilioClient
import com.example.myaccidentapplication.ui.screens.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object EmergencyContacts : Screen("emergency_contacts")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object AccidentAlert : Screen("accident_alert")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    appContainer: AppContainer,
    startDestination: String = Screen.Login.route
) {
    val repository = appContainer.accidentRepository
    val currentUser by repository.getCurrentUser().collectAsState(initial = null)

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ✅ Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                repository = repository
            )
        }

        // ✅ Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.EmergencyContacts.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                repository = repository
            )
        }

        // ✅ Emergency Contacts Screen
        composable(Screen.EmergencyContacts.route) {
            EmergencyContactFormScreen(
                onSaveContacts = { c1, c2 ->
                    CoroutineScope(Dispatchers.Main).launch {
                        repository.saveEmergencyContacts(currentUser?.id ?: -1, c1, c2) // FIXED: Int fallback
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.EmergencyContacts.route) { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ✅ Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                userName = currentUser?.name ?: "User",
                onSimulateAccident = {
                    navController.navigate(Screen.AccidentAlert.route)
                },
                onViewProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onLogout = {
                    CoroutineScope(Dispatchers.Main).launch {
                        repository.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // ✅ Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                userName = currentUser?.name ?: "",
                userEmail = currentUser?.email ?: "",
                onBack = { navController.popBackStack() }
            )
        }

        // ✅ Accident Alert Screen
        composable(Screen.AccidentAlert.route) {
            AccidentAlertScreen(
                onSafe = {
                    navController.popBackStack()
                },
                onNeedHelp = { lat, lon ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val contacts = repository.getEmergencyContacts().first()
                        val coordsText = if (lat != null && lon != null) {
                            "Location: https://maps.google.com/?q=$lat,$lon"
                        } else {
                            "Location: unavailable"
                        }
                        val message = "Emergency! Possible accident detected. $coordsText"
                        if (contacts.contact1.isNotBlank()) {
                            TwilioClient.sendAlertMessage(contacts.contact1, message)
                        }
                        if (contacts.contact2.isNotBlank()) {
                            TwilioClient.sendAlertMessage(contacts.contact2, message)
                        }
                        val accidentEvent = AccidentEvent(
                            userId = currentUser?.id ?: -1, // FIXED: Int fallback
                            latitude = lat,
                            longitude = lon,
                            severity = 1.0f
                        )
                        repository.reportAccident(accidentEvent)
                        navController.popBackStack()
                    }
                },
                onTimeout = { lat, lon ->
                    CoroutineScope(Dispatchers.Main).launch {
                        val contacts = repository.getEmergencyContacts().first()
                        val coordsText = if (lat != null && lon != null) {
                            "Location: https://maps.google.com/?q=$lat,$lon"
                        } else {
                            "Location: unavailable"
                        }
                        val message = "Emergency! No response detected after possible accident. $coordsText"
                        if (contacts.contact1.isNotBlank()) {
                            TwilioClient.sendAlertMessage(contacts.contact1, message)
                        }
                        if (contacts.contact2.isNotBlank()) {
                            TwilioClient.sendAlertMessage(contacts.contact2, message)
                        }
                        val accidentEvent = AccidentEvent(
                            userId = currentUser?.id ?: -1, // FIXED: Int fallback
                            latitude = lat,
                            longitude = lon,
                            severity = 1.0f
                        )
                        repository.reportAccident(accidentEvent)
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}